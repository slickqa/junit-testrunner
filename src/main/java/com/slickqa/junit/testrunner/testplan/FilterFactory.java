package com.slickqa.junit.testrunner.testplan;

@FunctionalInterface
public interface FilterFactory {
    org.junit.platform.engine.Filter filter(String value);
}
