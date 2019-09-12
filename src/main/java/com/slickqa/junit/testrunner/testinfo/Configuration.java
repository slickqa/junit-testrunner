package com.slickqa.junit.testrunner.testinfo;

public class Configuration {
    private String key;
    private String value;
    private Configuration(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Configuration Value(String name, String value) {
        //TODO: validate not null
        return new Configuration(name, value);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
