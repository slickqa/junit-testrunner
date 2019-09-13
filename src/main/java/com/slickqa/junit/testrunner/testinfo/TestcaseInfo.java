package com.slickqa.junit.testrunner.testinfo;

import de.vandermeer.asciitable.AsciiTable;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

public class TestcaseInfo implements EndUserData {
    public static final String WITH_ID="WITH_ID";
    private String id;
    private String name;
    private Method method;

    public static TestcaseInfo fromContext(ExtensionContext context) {
            TestcaseInfo info = new TestcaseInfo();
            info.setId(context.getUniqueId());
            info.setName(context.getDisplayName());
            info.method = context.getRequiredTestMethod();
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

    public String getMethod() {
        if(method != null) {
            return method.getDeclaringClass().getCanonicalName() + "#" + method.getName();
        }
        return null;
    }

    @Override
    public void addToTable(AsciiTable table, Configuration... options) {
        if(Configuration.OptionIsSet(options, WITH_ID, "true")) {
            table.addRow(getName(), getMethod(), getId());
        } else {
            table.addRow(getName(), getMethod());
        }
    }

    @Override
    public void addColumnHeadersToTable(AsciiTable table, Configuration... options) {
        if(Configuration.OptionIsSet(options, WITH_ID, "true")) {
            table.addRow("Name", "Method", "Id");
        } else {
            table.addRow("Name", "Method");
        }
    }
}
