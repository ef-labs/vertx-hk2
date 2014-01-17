package com.englishtown.vertx.hk2.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

/**
 * Integration test to show a module deployed with a injection constructor
 */
public class IntegrationTestVerticle extends TestVerticle {

    @Test
    public void testDependencyInjection_Compiled() throws Exception {

        container.deployVerticle(DependencyInjectionVerticle.class.getName(), new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> result) {
                assertTrue(result.succeeded());
                testComplete();
            }
        });

    }

    @Test
    public void testDependencyInjection_Uncompiled() throws Exception {

        container.deployVerticle("UncompiledDIVerticle.java", new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> result) {
                assertTrue(result.succeeded());
                testComplete();
            }
        });

    }

}
