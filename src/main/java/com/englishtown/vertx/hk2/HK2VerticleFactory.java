package com.englishtown.vertx.hk2;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.Binder;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.Container;
import org.vertx.java.platform.Verticle;
import org.vertx.java.platform.impl.java.CompilingClassLoader;
import org.vertx.java.platform.impl.java.JavaVerticleFactory;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 4/5/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class HK2VerticleFactory extends JavaVerticleFactory {

    private Vertx vertx;
    private Container container;
    private ClassLoader cl;

    private static final Logger logger = LoggerFactory.getLogger(HK2VerticleFactory.class);

    private static final String CONFIG_SERVICE_LOCATOR_NAME = "vertx_service_locator_name";
    private static final String SERVICE_LOCATOR_NAME = "vertx.service.locator";
    private static final String CONFIG_BOOTSTRAP_BINDER_NAME = "hk2_binder";
    private static final String BOOTSTRAP_BINDER_NAME = "com.englishtown.vertx.hk2.BootstrapBinder";

    @Override
    public void init(Vertx vertx, Container container, ClassLoader cl) {
        super.init(vertx, container, cl);

        this.vertx = vertx;
        this.container = container;
        this.cl = cl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Verticle createVerticle(String main) throws Exception {
        String className = main;
        Class<?> clazz;

        if (isJavaSource(main)) {
            // TODO - is this right???
            // Don't we want one CompilingClassloader per instance of this?
            CompilingClassLoader compilingLoader = new CompilingClassLoader(cl, main);
            className = compilingLoader.resolveMainClassName();
            clazz = compilingLoader.loadClass(className);
        } else {
            clazz = cl.loadClass(className);
        }

        JsonObject config = this.container.getConfig();
        if (config == null) {
            config = new JsonObject();
        }
        String bootstrapName = config.getString(CONFIG_BOOTSTRAP_BINDER_NAME, BOOTSTRAP_BINDER_NAME);
        Binder bootstrap = null;

        try {
            Class bootstrapClass = cl.loadClass(bootstrapName);
            Object obj = bootstrapClass.newInstance();

            if (obj instanceof Binder) {
                bootstrap = (Binder) obj;
            } else {
                logger.error("Class " + bootstrapName
                        + " does not implement Binder.");
            }
        } catch (ClassNotFoundException e) {
            logger.error("HK2 bootstrap binder class " + bootstrapName
                    + " was not found.", e);
        }

        setServiceLocatorFactory(config);
        ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
        ServiceLocator locator = factory.create(null);

        bind(locator, new VertxBinder(this.vertx, this.container));
        if (bootstrap != null) {
            bind(locator, bootstrap);
        }

        return (Verticle) locator.createAndInitialize(clazz);

    }

    private void setServiceLocatorFactory(JsonObject config) {
        String containerName = config.getString(CONFIG_SERVICE_LOCATOR_NAME, SERVICE_LOCATOR_NAME);
        ServiceLocatorFactory factory = new HK2ServiceLocatorFactoryImpl(containerName);

        Class factoryClass = ServiceLocatorFactory.class;

        try {
            Field instance = factoryClass.getDeclaredField("INSTANCE");
            instance.setAccessible(true);

            instance.set(null, factory);

        } catch (NoSuchFieldException e) {
            logger.error("NoSuchFieldException while setting the service locator factory", e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException while setting the service locator factory", e);
            throw new RuntimeException(e);
        }

    }

    private boolean isJavaSource(String main) {
        return main.endsWith(".java");
    }

    private static void bind(ServiceLocator locator, Binder binder) {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration dc = dcs.createDynamicConfiguration();

        locator.inject(binder);
        binder.bind(dc);

        dc.commit();
    }
}
