package com.englishtown.vertx.hk2;

import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.logging.LogDelegate;
import io.vertx.core.spi.logging.LogDelegateFactory;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class MockLogDelegateFactory implements LogDelegateFactory {

    private static LogDelegate logDelegate = mock(LogDelegate.class);

    static {
        // Use our own test logger factory / logger instead. We can't use powermock to statically mock the
        // LoggerFactory since javassist 1.18.x contains a bug that prevents the usage of powermock.
        System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, MockLogDelegateFactory.class.getName());
        LoggerFactory.removeLogger(HK2VerticleLoader.class.getName());
        LoggerFactory.initialise();
    }

    public static LogDelegate getLogDelegate() {
        return logDelegate;
    }

    public static void reset() {
        Mockito.reset(logDelegate);
    }

    @Override
    public LogDelegate createDelegate(String name) {
        return logDelegate;
    }
}