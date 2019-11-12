package com.slickqa.junit.testrunner.commands;

import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;

public class SystemPropertyOption {
    @CommandLine.Option(names={"-D"}, description = "Set system properties the same as if you passed them on the java command before the jar, ie name=value")
    Map<String, String> systemProperties = new HashMap<>();

    public void setProperties() {
        if(systemProperties != null && systemProperties.size() > 0) {
            for(String propertyName : systemProperties.keySet()) {
                System.setProperty(propertyName, systemProperties.get(propertyName));
            }
        }
    }
}
