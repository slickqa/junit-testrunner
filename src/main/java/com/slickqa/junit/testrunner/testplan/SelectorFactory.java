package com.slickqa.junit.testrunner.testplan;

import org.junit.platform.engine.DiscoverySelector;

@FunctionalInterface
public interface SelectorFactory {
    DiscoverySelector select(String value);
}
