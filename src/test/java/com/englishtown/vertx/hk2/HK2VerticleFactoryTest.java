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
        when(container.getConfig()).thenReturn(config);
        when(container.getLogger()).thenReturn(logger);

        factory.init(vertx, container, this.getClass().getClassLoader());
        factory.createVerticle("com.englishtown.vertx.hk2.TestHK2Verticle");

    }

}
