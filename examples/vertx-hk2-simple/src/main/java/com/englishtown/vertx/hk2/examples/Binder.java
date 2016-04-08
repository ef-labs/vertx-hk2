package com.englishtown.vertx.hk2.examples;

import com.englishtown.vertx.hk2.examples.impl.MyDependencyImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * HK2 binder
 */
public class Binder extends AbstractBinder {
    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(MyDependencyImpl.class).to(MyDependency.class).in(Singleton.class);
    }
}
