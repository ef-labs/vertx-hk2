package com.englishtown.vertx.hk2.integration;

import com.englishtown.vertx.hk2.MyDependency;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 * Verticle with dependencies injected
 */
public class DependencyInjectionVerticle extends AbstractVerticle {

    private final MyDependency myDependency;
    private final Vertx vertx;

    @Inject
    public DependencyInjectionVerticle(MyDependency myDependency, Vertx vertx) {
        this.myDependency = myDependency;
        this.vertx = vertx;
        assertNotNull(myDependency);
    }

}
