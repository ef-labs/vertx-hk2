package com.englishtown.vertx.hk2;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.internal.ServiceLocatorFactoryImpl;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 4/5/13
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class HK2ServiceLocatorFactoryImpl extends ServiceLocatorFactoryImpl {

    public String defaultName;

    public HK2ServiceLocatorFactoryImpl(String defaultName) {
        this.defaultName = defaultName;
    }

    @Override
    public ServiceLocator create(String name, ServiceLocator parent, ServiceLocatorGenerator generator) {
        if (name == null) {
            name = defaultName;
        }
        return super.create(name, parent, generator);
    }
}
