package com.englishtown.vertx.hk2;

import io.vertx.core.impl.IsolatingClassLoader;
import sun.misc.CompoundEnumeration;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

/**
 * URLClassLoader wrapper over an {@link io.vertx.core.impl.IsolatingClassLoader}
 */
public class WrappedIsolatingClassLoader extends URLClassLoader {

    private final IsolatingClassLoader parent;

    /**
     * {@inheritDoc}
     */
    public WrappedIsolatingClassLoader(IsolatingClassLoader wrapped) {
        super(wrapped.getURLs(), wrapped);
        this.parent = wrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResource(String name) {

        // First check the IsolatingClassLoader URLs
        URL url = findResource(name);

        // If not found, use the parent
        if (url == null) {
            url = parent.getResource(name);
        }

        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        @SuppressWarnings("unchecked")
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[2];

        tmp[0] = findResources(name);
        tmp[1] = parent.getResources(name);

        return new CompoundEnumeration<>(tmp);
    }
}
