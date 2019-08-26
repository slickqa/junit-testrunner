package com.slickqa.junit.testrunner.testplan;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestplanFile {
    private String name;
    private String description;
    private List<Map<Selector, String>> selectors;
    private List<Map<Filter, String>> filters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Map<Selector, String>> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<Map<Selector, String>> selectors) {
        this.selectors = selectors;
    }

    public List<Map<Filter, String>> getFilters() {
        return filters;
    }

    public void setFilters(List<Map<Filter, String>> filters) {
        this.filters = filters;
    }

    public LauncherDiscoveryRequest toLauncherDiscoveryRequest() {
        List<org.junit.platform.engine.Filter> filters = new ArrayList<>();
        List<DiscoverySelector> selectors = new ArrayList<>();
        for(Map<Selector, String> selectorMap : this.getSelectors()) {
            for(Map.Entry<Selector, String> selector : selectorMap.entrySet()) {
                selectors.add(selector.getKey().select(selector.getValue()));
            }
        }
        for(Map<Filter, String> filterMap : this.getFilters()) {
            for(Map.Entry<Filter, String> filter : filterMap.entrySet()) {
                filters.add(filter.getKey().filter(filter.getValue()));
            }
        }
        return LauncherDiscoveryRequestBuilder.request().selectors(selectors).filters((org.junit.platform.engine.Filter[])filters.toArray()).build();
    }
}
