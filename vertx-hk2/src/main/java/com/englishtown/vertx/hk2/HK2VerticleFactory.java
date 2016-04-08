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

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;

import java.lang.reflect.Constructor;

/**
 * Implements {@link io.vertx.core.spi.VerticleFactory} using an HK2 verticle wrapper for dependency injection.
 */
public class HK2VerticleFactory implements VerticleFactory {

    public static final String PREFIX = "java-hk2";

    private ServiceLocator locator;

    @Override
    public String prefix() {
        return PREFIX;
    }

    /**
     * Close the factory. The implementation must release all resources.
     */
    @Override
    public void close() {
        if (locator != null) {
            ServiceLocatorFactory.getInstance().destroy(locator);
            locator = null;
        }
    }

    /**
     * Returns the current parent locator
     *
     * @return
     */
    public ServiceLocator getLocator() {
        if (locator == null) {
            locator = createLocator();
        }
        return locator;
    }

    /**
     * Sets the parent locator
     *
     * @param locator
     * @return
     */
    public HK2VerticleFactory setLocator(ServiceLocator locator) {
        this.locator = locator;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        verticleName = VerticleFactory.removePrefix(verticleName);

        // Use the provided class loader to create an instance of HK2VerticleLoader.  This is necessary when working with vert.x IsolatingClassLoader
        @SuppressWarnings("unchecked")
        Class<Verticle> loader = (Class<Verticle>) classLoader.loadClass(HK2VerticleLoader.class.getName());
        Constructor<Verticle> ctor = loader.getConstructor(String.class, ClassLoader.class, ServiceLocator.class);

        if (ctor == null) {
            throw new IllegalStateException("Could not find HK2VerticleLoad constructor");
        }

        return ctor.newInstance(verticleName, classLoader, getLocator());
    }

    protected ServiceLocator createLocator() {
        return null;
    }

}
