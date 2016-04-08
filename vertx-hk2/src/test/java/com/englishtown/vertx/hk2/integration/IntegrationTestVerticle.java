package com.englishtown.vertx.hk2.integration;

import com.englishtown.vertx.hk2.DefaultMyDependency2;
import com.englishtown.vertx.hk2.HK2VerticleFactory;
import com.englishtown.vertx.hk2.MyDependency2;
import io.vertx.core.DeploymentOptions;
import io.vertx.test.core.VertxTestBase;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.Test;

import javax.inject.Singleton;

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
    public void testDependencyInjection_Fail() throws Exception {
        String identifier = HK2VerticleFactory.PREFIX + ":" + DependencyInjectionVerticle2.class.getName();
        vertx.deployVerticle(identifier, new DeploymentOptions(), ar -> {
            assertTrue(ar.failed());
            testComplete();
        });
        await();
    }

    @Test
    public void testDependencyInjection_ParentLocator() throws Exception {

        HK2VerticleFactory factory = vertx.verticleFactories()
                .stream()
                .filter(f -> f instanceof HK2VerticleFactory)
                .map(f -> (HK2VerticleFactory) f)
                .findFirst()
                .get();

        factory.setLocator(ServiceLocatorUtilities.bind(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(DefaultMyDependency2.class).to(MyDependency2.class).in(Singleton.class);
            }
        }));

        String identifier = HK2VerticleFactory.PREFIX + ":" + DependencyInjectionVerticle2.class.getName();
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
