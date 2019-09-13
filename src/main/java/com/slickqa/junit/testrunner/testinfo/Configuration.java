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

    public static boolean OptionIsSet(Configuration[] options, String name, String value) {
        if(name == null) {
            return false;
        }

        for(Configuration option : options) {
            if(name.equals(option.getKey())) {
                if(value == null) {
                    if(option.getValue() == null) {
                        return true;
                    }
                } else if(value.equalsIgnoreCase(option.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String GetOptionIfSet(Configuration[] options, String name) {
        if(name == null) {
            return null;
        }

        for(Configuration option : options) {
            if(name.equals(option.getKey())) {
                return option.getValue();
            }
        }

        return null;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
