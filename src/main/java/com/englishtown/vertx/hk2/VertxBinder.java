package com.englishtown.vertx.hk2;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.vertx.java.core.Vertx;
import org.vertx.java.platform.Container;

import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 4/5/13
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class VertxBinder extends AbstractBinder {

    private final Vertx vertx;
    private final Container container;

    public VertxBinder(Vertx vertx, Container container) {
        this.vertx = vertx;
        this.container = container;
    }

    /**
     * Implement to provide binding definitions using the exposed binding
     * methods.
     */
    @Override
    protected void configure() {
        bind(vertx).to(Vertx.class);
        bind(container).to(Container.class);
    }
}
