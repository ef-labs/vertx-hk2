package com.englishtown.vertx.hk2;

import org.vertx.java.platform.Verticle;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 4/5/13
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestHK2Verticle extends Verticle {

    private final MyDependency dependency;

    @Inject
    public TestHK2Verticle(MyDependency dependency) {
        this.dependency = dependency;
    }

}
