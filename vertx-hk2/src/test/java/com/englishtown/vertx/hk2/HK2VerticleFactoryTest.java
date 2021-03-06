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
import io.vertx.core.Vertx;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HK2VerticleFactoryTest {

    private HK2VerticleFactory factory;

    @Mock
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        factory = new HK2VerticleFactory();
        factory.init(vertx);
    }

    @Test
    public void testPrefix() {
        assertEquals("java-hk2", factory.prefix());
    }

    @Test
    public void testCreateVerticle() throws Exception {
        String identifier = HK2VerticleFactory.getIdentifier(TestHK2Verticle.class);
        Verticle verticle = factory.createVerticle(identifier, this.getClass().getClassLoader());
        assertThat(verticle, instanceOf(HK2VerticleLoader.class));

        HK2VerticleLoader loader = (HK2VerticleLoader) verticle;
        assertEquals(TestHK2Verticle.class.getName(), loader.getVerticleName());
    }

    @Test
    public void testSetLocator() throws Exception {

        ServiceLocator original = factory.getLocator();
        assertNull(original);

        ServiceLocator locator = mock(ServiceLocator.class);
        factory.setLocator(locator);

        assertEquals(locator, factory.getLocator());

    }

    @Test
    public void testClose() throws Exception {

        ServiceLocator locator = mock(ServiceLocator.class);
        factory.setLocator(locator);
        factory.close();

        verify(locator).shutdown();

    }

}
