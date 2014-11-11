package com.englishtown.vertx.hk2.integration;

import io.vertx.core.DeploymentOptions;
import io.vertx.test.core.VertxTestBase;

import org.junit.Test;

/**
 * Integration test to show a module deployed with a injection constructor
 */
public class IntegrationTestVerticle extends VertxTestBase {

    @Test
    public void testDependencyInjection_Compiled() throws Exception {
        vertx.deployVerticle(DependencyInjectionVerticle.class.getName(), new DeploymentOptions(), ar -> {
           assertTrue(ar.succeeded());
           testComplete();
        });
        await();
    }

    @Test
    public void testDependencyInjection_Uncompiled() throws Exception {

        vertx.deployVerticle("UncompiledDIVerticle.java",new DeploymentOptions(), ar -> {
            assertTrue(ar.succeeded());
            testComplete();
        });
        await();
   }

}
