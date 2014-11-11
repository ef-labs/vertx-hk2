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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;
import org.vertx.java.platform.Verticle;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 4/5/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class HK2VerticleFactoryTest {

    JsonObject config = new JsonObject();

    @Mock
    Vertx vertx;
    @Mock
    Container container;
    @Mock
    Logger logger;

    @Before
    public void setUp() throws Exception {

        when(container.config()).thenReturn(config);
        when(container.logger()).thenReturn(logger);

    }

    @Test
    public void testCreateVerticle() throws Exception {

        HK2VerticleFactory factory = new HK2VerticleFactory();

        config.putString("hk2_binder", "com.englishtown.vertx.hk2.BootstrapBinder");

        factory.init(vertx, container, this.getClass().getClassLoader());
        Verticle verticle = factory.createVerticle("com.englishtown.vertx.hk2.TestHK2Verticle");

        assertThat(verticle, instanceOf(HK2VerticleLoader.class));
        assertEquals(container, verticle.getContainer());
        assertEquals(vertx, verticle.getVertx());

    }

    @Test
    public void testReportException() throws Exception {

        HK2VerticleFactory factory = new HK2VerticleFactory();

        factory.reportException(null, null);
        verifyZeroInteractions(logger);

        RuntimeException t = new RuntimeException();
        factory.reportException(logger, t);
        verify(logger).error(anyString(), eq(t));

    }

}
