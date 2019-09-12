package com.slickqa.junit.testrunner.testinfo;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

public class TestcaseInfo {
    private String id;
    private String name;
    private Method method;

    public static TestcaseInfo fromContext(ExtensionContext context) {
            TestcaseInfo info = new TestcaseInfo();
            info.setId(context.getUniqueId());
            info.setName(context.getDisplayName());
            info.setMethod(context.getRequiredTestMethod());
            return info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
