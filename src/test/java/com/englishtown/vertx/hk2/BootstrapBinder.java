package com.englishtown.vertx.hk2;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 4/5/13
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class BootstrapBinder extends AbstractBinder {
    /**
     * Implement to provide binding definitions using the exposed binding
     * methods.
     */
    @Override
    protected void configure() {
        bind(DefaultMyDependency.class).to(MyDependency.class);
    }
}
