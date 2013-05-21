package com.englishtown.vertx.hk2;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.vertx.java.core.Vertx;
import org.vertx.java.platform.Container;

import javax.inject.Singleton;

/**
 * HK2 {@link AbstractBinder} for vertx and container injections
 */
class VertxBinder extends AbstractBinder {

    private final Vertx vertx;
    private final Container container;

    public VertxBinder(Vertx vertx, Container container) {
        this.vertx = vertx;
        this.container = container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(vertx).to(Vertx.class);
        bind(container).to(Container.class);
    }
}
