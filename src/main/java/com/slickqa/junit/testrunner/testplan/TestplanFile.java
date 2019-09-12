package com.slickqa.junit.testrunner.testplan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.slickqa.junit.testrunner.testinfo.Configuration;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestplanFile {
    private String name;
    private String description;
    private List<Map<Selector, String>> selectors;
    private List<Map<Filter, String>> filters;

    public TestplanFile() {
        name = "";
        description = "";
        selectors = new ArrayList<>();
        filters = new ArrayList<>();
    }

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

    public LauncherDiscoveryRequest toLauncherDiscoveryRequest(Configuration... entries) {
        List<org.junit.platform.engine.Filter> filters = new ArrayList<>();
        List<DiscoverySelector> selectors = new ArrayList<>();
        Map<String, String> configurationParameters = new HashMap<>();
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
        for(Configuration config : entries) {
            configurationParameters.put(config.getKey(), config.getValue());
        }
        return LauncherDiscoveryRequestBuilder.request()
                .selectors(selectors)
                .filters(filters.toArray(new org.junit.platform.engine.Filter[0]))
                .configurationParameters(configurationParameters)
                .build();
    }

    public static TestplanFile readFrom(File file) throws IOException {
        ObjectMapper mapper;
        if(file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else {
            mapper = new ObjectMapper();
        }
        return mapper.readValue(file, TestplanFile.class);
    }
}
