package com.englishtown.vertx.hk2.integration;

import com.englishtown.vertx.hk2.MyDependency2;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 * Verticle with dependencies injected
 */
public class DependencyInjectionVerticle2 extends AbstractVerticle {

    private final MyDependency2 myDependency;
    private final Vertx vertx;

    @Inject
    public DependencyInjectionVerticle2(MyDependency2 myDependency, Vertx vertx) {
        this.myDependency = myDependency;
        this.vertx = vertx;
        assertNotNull(myDependency);
    }

}
