/*
 * The MIT License (MIT)
 * Copyright © 2013 Englishtown <opensource@englishtown.com>
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.impl.LoggerFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.englishtown.vertx.hk2.integration.CustomBinder;
import com.englishtown.vertx.hk2.integration.DependencyInjectionVerticle;

/**
 * Unit tests for {@link HK2VerticleLoader}
 */
@RunWith(MockitoJUnitRunner.class)
public class HK2VerticleLoaderTest {

    JsonObject config = new JsonObject();

    @Mock
    Vertx vertx;
    @Mock
    Context context;

    @BeforeClass
    public static void setupOnce() {
        // Use our own test logger factory / logger instead. We can't use powermock to statically mock the 
        // LoggerFactory since javassist 1.18.x contains a bug that prevents the usage of powermock.
        System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, LogDelegateTestFactory.class.getCanonicalName());
    }

    @Before
    public void setUp() {
        TestLogDelegate.reset();
        when(vertx.context()).thenReturn(context);
        when(context.config()).thenReturn(config);
    }

    private HK2VerticleLoader createLoader(String main) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        HK2VerticleLoader loader = new HK2VerticleLoader(main, cl);
        loader.init(vertx, vertx.context());
        return loader;
    }

    @Test
    public void testStart_Compiled() throws Exception {

        String main = DependencyInjectionVerticle.class.getName();
        Future<Void> vr = Future.future();

        HK2VerticleLoader loader = createLoader(main);
        loader.start(vr);

        assertTrue(vr.succeeded());
        assertFalse("The logger should not have been used.", TestLogDelegate.wasUsed());
        loader.stop();

    }

    @Test
    public void testStart_Uncompiled() throws Exception {

        String main = "UncompiledDIVerticle.java";
        Future<Void> vr = Future.future();

        HK2VerticleLoader loader = createLoader(main);
        loader.start(vr);

        assertTrue(vr.succeeded());
        assertFalse("The logger should not have been used.", TestLogDelegate.wasUsed());
        loader.stop();

    }

    @Test
    public void testStart_Custom_Binder() throws Exception {

        config.put("hk2_binder", CustomBinder.class.getName());

        String main = DependencyInjectionVerticle.class.getName();
        Future<Void> vr = Future.future();

        HK2VerticleLoader loader = createLoader(main);
        loader.start(vr);

        if (vr.failed()) {
            vr.cause().printStackTrace();
        }
        assertTrue("The startup of the verticle should have worked", vr.succeeded());
        assertFalse("The logger should not have been used.", TestLogDelegate.wasUsed());
        loader.stop();

    }

    @Test
    public void testStart_Custom_Binder_Array() throws Exception {

        config.put("hk2_binder", new JsonArray()
                .add(CustomBinder.class.getName())
                .add(BootstrapBinder.class.getName()));

        String main = DependencyInjectionVerticle.class.getName();
        Future<Void> vr = Future.future();

        HK2VerticleLoader loader = createLoader(main);
        loader.start(vr);

        assertTrue(vr.succeeded());
        assertFalse("The logger should not have been used.", TestLogDelegate.wasUsed());
        loader.stop();

    }

    @Test
    public void testStart_Not_A_Binder() throws Exception {

        String binder = String.class.getName();
        config.put("hk2_binder", binder);

        String main = DependencyInjectionVerticle.class.getName();
        Future<Void> vr = Future.future();

        HK2VerticleLoader loader = createLoader(main);
        loader.start(vr);

        assertFalse("The future did indicate that the startup was successful which it should not have been.", vr.succeeded());
        assertNotNull(vr.cause());
        assertEquals("Class " + binder + " does not implement Binder.", TestLogDelegate.getLastError());
        loader.stop();

    }

    @Test
    public void testStart_Class_Not_Found_Binder() throws Exception {

        String binder = "com.englishtown.INVALID_BINDER";
        config.put("hk2_binder", binder);

        String main = DependencyInjectionVerticle.class.getName();
        Future<Void> vr = Future.future();

        HK2VerticleLoader loader = createLoader(main);
        loader.start(vr);

        assertFalse("The future did indicate that the startup was successful which it should not have been.", vr.succeeded());
        assertNotNull(vr.cause());
        assertEquals("HK2 bootstrap binder class " + binder + " was not found.  Are you missing injection bindings?", TestLogDelegate.getLastError());
        loader.stop();

    }

}
