package com.englishtown.vertx.hk2.examples.integration;

import com.englishtown.vertx.hk2.HK2VerticleFactory;
import com.englishtown.vertx.hk2.examples.MyDependency;
import com.englishtown.vertx.hk2.examples.ParentLocatorVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.test.core.VertxTestBase;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test to show sharing a singleton dependency across 4 deployed Verticles
 */
public class ParentLocatorVerticleTest extends VertxTestBase {

    private ServiceLocator parent;
    private MyDependency singleton;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        initParentLocator();
        CompletableFuture<Void> future = new CompletableFuture<>();

        DeploymentOptions options = new DeploymentOptions().setInstances(4);

        vertx.deployVerticle(HK2VerticleFactory.getIdentifier(ParentLocatorVerticle.class), options, result -> {
            if (result.succeeded()) {
                future.complete(null);
            } else {
                future.completeExceptionally(result.cause());
            }
        });

        future.get(2, TimeUnit.SECONDS);

    }

    private void initParentLocator() {

        singleton = new MyDependency() {
        };

        parent = ServiceLocatorUtilities.bind((String) null, new AbstractBinder() {
            @Override
            protected void configure() {
                bind(singleton).to(MyDependency.class);
            }
        });

        HK2VerticleFactory factory = vertx.verticleFactories()
                .stream()
                .filter(f -> f instanceof HK2VerticleFactory)
                .map(f -> (HK2VerticleFactory) f)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("HK2 verticle factor missing from class path"));

        factory.setLocator(parent);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        parent.shutdown();
    }

    @Test
    public void testHandle() throws Exception {

        int count = 100;
        AtomicInteger replies = new AtomicInteger(0);

        for (int i = 0; i < count; i++) {
            vertx.eventBus().<Integer>send(ParentLocatorVerticle.EB_ADDRESS, null, result -> {

                if (result.failed()) {
                    result.cause().printStackTrace();
                    fail();
                    return;
                }

                assertEquals(new Integer(singleton.hashCode()), result.result().body());

                if (replies.incrementAndGet() == count) {
                    testComplete();
                }

            });
        }

        await();
    }
}