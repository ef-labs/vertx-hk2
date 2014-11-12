package com.englishtown.vertx.hk2.integration;

import com.englishtown.vertx.hk2.HK2VerticleFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

/**
 * Integration test to show a module deployed with a injection constructor
 */
public class IntegrationTestVerticle extends VertxTestBase {

    @Test
    public void testDependencyInjection_Compiled() throws Exception {
        String identifier = HK2VerticleFactory.PREFIX + ":" + DependencyInjectionVerticle.class.getName();
        vertx.deployVerticle(identifier, new DeploymentOptions(), ar -> {
            assertTrue(ar.succeeded());
            testComplete();
        });
        await();
    }

    @Test
    public void testDependencyInjection_Uncompiled() throws Exception {
        String identifier = HK2VerticleFactory.PREFIX + ":" + "UncompiledDIVerticle.java";
        vertx.deployVerticle(identifier, new DeploymentOptions(), ar -> {
            assertTrue(ar.succeeded());
            testComplete();
        });
        await();
    }

}
