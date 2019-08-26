package com.slickqa.junit.testrunner.testplan;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;

public enum Selector {
    className(DiscoverySelectors::selectClass, ""),
    classPathResource(DiscoverySelectors::selectClasspathResource, ""),
    directory(DiscoverySelectors::selectDirectory, ""),
    file(DiscoverySelectors::selectFile, ""),
    method(DiscoverySelectors::selectMethod, ""),
    module(DiscoverySelectors::selectModule, ""),
    packageName(DiscoverySelectors::selectPackage, ""),
    uniqueId(DiscoverySelectors::selectUniqueId, ""),
    uri(DiscoverySelectors::selectUri, "");

    private SelectorFactory factory;
    private String help;

    Selector(SelectorFactory factory, String help) {
        this.factory = factory;
        this.help = help;
    }

    DiscoverySelector select(String value) {
        return this.factory.select(value);
    }

    public String getHelp() {
        return this.help;
    }
}
