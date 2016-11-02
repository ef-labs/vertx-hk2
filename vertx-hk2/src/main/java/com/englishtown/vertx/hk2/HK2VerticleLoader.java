/*
 * The MIT License (MIT)
 * Copyright © 2016 Englishtown <opensource@englishtown.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.englishtown.vertx.hk2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.verticle.CompilingClassLoader;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HK2 Verticle to lazy load the real verticle with DI
 */
public class HK2VerticleLoader extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(HK2VerticleLoader.class);

    private final String verticleName;
    private ClassLoader classLoader;
    private ServiceLocator parent;
    private Verticle realVerticle;
    private ServiceLocator locator;

    private static final Map<MultiKey, ServiceLocator> serviceLocatorCache = new ConcurrentHashMap<>();
    private static final Map<ServiceLocator, AtomicInteger> serviceLocatorVerticleInstanceCount = new ConcurrentHashMap<>();

    public static final String CONFIG_EVENT_LOOP_SINGLETON_SERVICE_LOCATOR_NAME = "hk2_singleton_service_locator";
    public static final String CONFIG_BOOTSTRAP_BINDER_NAME = "hk2_binder";
    public static final String BOOTSTRAP_BINDER_NAME = "com.englishtown.vertx.hk2.BootstrapBinder";

    public HK2VerticleLoader(String verticleName, ClassLoader classLoader, ServiceLocator parent) {
        this.verticleName = verticleName;
        this.classLoader = classLoader;
        this.parent = parent;
    }

    /**
     * Initialise the verticle.<p>
     * This is called by Vert.x when the verticle instance is deployed. Don't call it yourself.
     *
     * @param vertx   the deploying Vert.x instance
     * @param context the context of the verticle
     */
    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        try {
            // Create the real verticle and init
            realVerticle = createRealVerticle();
            realVerticle.init(vertx, context);
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new RuntimeException(t);
        }

    }

    /**
     * Override this method to signify that start is complete sometime _after_ the start() method has returned
     * This is useful if your verticle deploys other verticles or modules and you don't want this verticle to
     * be considered started until the other modules and verticles have been started.
     *
     * @param startedResult When you are happy your verticle is started set the result
     * @throws Exception
     */
    @Override
    public void start(Future<Void> startedResult) throws Exception {
        // Start the real verticle
        realVerticle.start(startedResult);
    }

    /**
     * Vert.x calls the stop method when the verticle is undeployed.
     * Put any cleanup code for your verticle in here
     *
     * @throws Exception
     */
    @Override
    public void stop(Future<Void> stopFuture) throws Exception {

        classLoader = null;
        parent = null;

        Future<Void> future = Future.future();
        future.setHandler(result -> {

            if (serviceLocatorVerticleInstanceCount.containsKey(locator)) {
                final int instancesLeft = serviceLocatorVerticleInstanceCount.get(locator).decrementAndGet();
                if (instancesLeft <= 0) {
                    // Destroy the service locator
                    ServiceLocatorFactory.getInstance().destroy(locator);
                }
            } else {
                // Destroy the service locator
                ServiceLocatorFactory.getInstance().destroy(locator);
            }

            locator = null;

            // Pass result to the stop future
            if (result.succeeded()) {
                stopFuture.complete();
            } else {
                stopFuture.fail(future.cause());
            }
        });

        try {
            // Stop the real verticle
            if (realVerticle != null) {
                realVerticle.stop(future);
            } else {
                future.complete();
            }
        } catch (Throwable t) {
            future.fail(t);
        }

    }

    public String getVerticleName() {
        return verticleName;
    }

    public Verticle createRealVerticle() throws Exception {
        String className = verticleName;
        Class<?> clazz;

        if (className.endsWith(".java")) {
            CompilingClassLoader compilingLoader = new CompilingClassLoader(classLoader, className);
            className = compilingLoader.resolveMainClassName();
            clazz = compilingLoader.loadClass(className);
        } else {
            clazz = classLoader.loadClass(className);
        }
        Verticle verticle = createRealVerticle(clazz);
        return verticle;
    }

    private Verticle createRealVerticle(Class<?> clazz) throws Exception {
        final Set<Class> bootstrapBinderClasses = getBootstrapBinderClasses();
        if (isSingletonLocator()) {
            final MultiKey key = new MultiKey(classLoader, parent, bootstrapBinderClasses, Thread.currentThread());
            if (!serviceLocatorCache.containsKey(key)) {
                // Each verticle factory will have it's own service locator instance
                // Passing a null name will not cache the locator in the factory
                locator = ServiceLocatorFactory.getInstance().create(null, parent);

                serviceLocatorCache.put(key, locator);
                serviceLocatorVerticleInstanceCount.put(locator, new AtomicInteger(1));

                logger.info("Caching locator " + locator.getLocatorId() + " for thread " + Thread.currentThread().getName() + " and cache key " + key);
                return bindToVerticle(clazz, locator, bootstrapBinderClasses);
            } else {
                locator = serviceLocatorCache.get(key);
                serviceLocatorVerticleInstanceCount.get(locator).incrementAndGet();

                logger.info("Retrieving cached locator " + locator.getLocatorId() + " for thread " + Thread.currentThread().getName() + " and cache key " + key);
                return bindToVerticle(clazz, locator, bootstrapBinderClasses);
            }
        } else {
            // Each verticle factory will have it's own service locator instance
            // Passing a null name will not cache the locator in the factory
            locator = ServiceLocatorFactory.getInstance().create(null, parent);
            return bindToVerticle(clazz, locator, bootstrapBinderClasses);
        }
    }

    private Verticle bindToVerticle(Class clazz, ServiceLocator locator, Set<Class> bootstrapClasses) throws IllegalAccessException, InstantiationException {
        final List<Binder> bootstraps = new ArrayList<>();

        for (Class bootstrapClass : bootstrapClasses) {
            Object obj = bootstrapClass.newInstance();

            if (obj instanceof Binder) {
                bootstraps.add((Binder) obj);
            } else {
                logger.error("Class " + bootstrapClass.getCanonicalName()
                        + " does not implement Binder.");
            }
        }

        bootstraps.add(0, new HK2VertxBinder(vertx));

        ServiceLocatorUtilities.bind(locator, bootstraps.toArray(new Binder[bootstraps.size()]));
        return (Verticle) locator.createAndInitialize(clazz);
    }

    private Set<Class> getBootstrapBinderClasses() {
        JsonObject config = config();
        Object field = config.getValue(CONFIG_BOOTSTRAP_BINDER_NAME);
        JsonArray bootstrapNames;

        if (field instanceof JsonArray) {
            bootstrapNames = (JsonArray) field;
        } else {
            bootstrapNames = new JsonArray().add((field == null ? BOOTSTRAP_BINDER_NAME : field));
        }

        final Set<Class> bootstrapClasses = new HashSet<>();
        for (int i = 0; i < bootstrapNames.size(); i++) {
            final String bootstrapName = bootstrapNames.getString(i);
            try {
                bootstrapClasses.add(classLoader.loadClass(bootstrapName));
            } catch (ClassNotFoundException e) {
                logger.error("HK2 bootstrap binder class " + bootstrapName
                        + " was not found.  Are you missing injection bindings?");
            }
        }

        return bootstrapClasses;
    }

    private boolean isSingletonLocator() {
        final Boolean configValue = config().getBoolean(CONFIG_EVENT_LOOP_SINGLETON_SERVICE_LOCATOR_NAME, false);
        return Boolean.valueOf(System.getProperty(CONFIG_EVENT_LOOP_SINGLETON_SERVICE_LOCATOR_NAME, configValue.toString()));
    }
}
