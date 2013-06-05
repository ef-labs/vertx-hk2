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

import org.junit.Test;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;
import org.vertx.java.platform.PlatformManagerFactory;
import org.vertx.java.platform.impl.DefaultContainer;
import org.vertx.java.platform.impl.DefaultPlatformManagerFactory;
import org.vertx.testtools.TestVerticle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 4/5/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class HK2VerticleFactoryTest {

    @Test
    public void testCreateVerticle() throws Exception {

        HK2VerticleFactory factory = new HK2VerticleFactory();

        JsonObject config = new JsonObject().putString("hk2_binder", "com.englishtown.vertx.hk2.BootstrapBinder");

        Logger logger = mock(Logger.class);
        Vertx vertx = mock(Vertx.class);

        Container container = mock(Container.class);
        when(container.config()).thenReturn(config);
        when(container.logger()).thenReturn(logger);

        factory.init(vertx, container, this.getClass().getClassLoader());
        factory.createVerticle("com.englishtown.vertx.hk2.TestHK2Verticle");

    }

}
