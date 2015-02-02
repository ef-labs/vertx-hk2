package com.englishtown.vertx.hk2.integration;

import com.englishtown.vertx.hk2.MyDependency;
import io.vertx.core.AbstractVerticle;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 * Verticle with dependencies injected
 */
public class DependencyInjectionVerticle extends AbstractVerticle {

    private final MyDependency myDependency;

    @Inject
    public DependencyInjectionVerticle(MyDependency myDependency) {
        this.myDependency = myDependency;
        assertNotNull(myDependency);
    }

}
