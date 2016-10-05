package com.englishtown.vertx.hk2.examples.integration;

import com.englishtown.vertx.hk2.HK2VerticleFactory;
import com.englishtown.vertx.hk2.examples.Binder;
import com.englishtown.vertx.hk2.examples.SimpleVerticle;
import com.englishtown.vertx.hk2.examples.impl.MyDependencyImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.englishtown.vertx.hk2.HK2VerticleLoader.CONFIG_BOOTSTRAP_BINDER_NAME;

/**
 * Integration test for {@link SimpleVerticle}
 */
public class SimpleVerticleTest extends VertxTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();

        CompletableFuture<Void> future = new CompletableFuture<>();

        JsonObject config = new JsonObject().put(CONFIG_BOOTSTRAP_BINDER_NAME, Binder.class.getName());

        vertx.deployVerticle(HK2VerticleFactory.getIdentifier(SimpleVerticle.class), new DeploymentOptions().setConfig(config), result -> {
            if (result.succeeded()) {
                future.complete(null);
            } else {
                future.completeExceptionally(result.cause());
            }
        });

        future.get(2, TimeUnit.SECONDS);

    }

    @Test
    public void testHandle() throws Exception {

        vertx.eventBus().<String>send(SimpleVerticle.EB_ADDRESS, null, result -> {

            if (result.failed()) {
                result.cause().printStackTrace();
                fail();
                return;
            }

            assertEquals(MyDependencyImpl.class.getName(), result.result().body());
            testComplete();

        });

        await();
    }
}