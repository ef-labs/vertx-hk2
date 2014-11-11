package com.englishtown.vertx.hk2.integration;

import io.vertx.core.AbstractVerticle;


import javax.inject.Inject;
import static org.junit.Assert.assertNotNull;
import com.englishtown.vertx.hk2.MyDependency;

/**
 * Verticle with dependencies injected
 */
public class DependencyInjectionVerticle extends AbstractVerticle {

    private final MyDependency myDependency;

    //TODO Migration: Is this constructor really needed?
    public DependencyInjectionVerticle() {
        myDependency =null;
    }

    @Inject
    public DependencyInjectionVerticle(MyDependency myDependency) {
        this.myDependency = myDependency;
        assertNotNull(myDependency);
    }

}
