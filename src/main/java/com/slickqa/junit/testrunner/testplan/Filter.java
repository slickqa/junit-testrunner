package com.slickqa.junit.testrunner.testplan;

import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.PackageNameFilter;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.TagFilter;

public enum Filter {
    includeClassNames(ClassNameFilter::includeClassNamePatterns, ""),
    excludeClassNames(ClassNameFilter::excludeClassNamePatterns, ""),
    includeTags(TagFilter::includeTags, ""),
    excludeTags(TagFilter::excludeTags, ""),
    includeEngine(EngineFilter::includeEngines, ""),
    excludeEngine(EngineFilter::excludeEngines, ""),
    includePackageName(PackageNameFilter::includePackageNames, ""),
    excludePackageName(PackageNameFilter::excludePackageNames, "");

    private FilterFactory factory;
    private String help;

    Filter(FilterFactory factory, String help) {
        this.factory = factory;
        this.help = help;
    }

    org.junit.platform.engine.Filter filter(String value) {
        return this.factory.filter(value);
    }

    public String getHelp() {
        return this.help;
    }
}
