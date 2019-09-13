package com.slickqa.junit.testrunner.testinfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.TA_GridThemes;

public class TestplanInfo implements EndUserData {
    public static final String INCLUDE_COUNT_OPTION = "INCLUDE_COUNT";

    TestplanFile testplan;
    String path;
    int testCount;

    public String getName() {
        String name = testplan.getName();
        if(name == null || "".equals(name)) {
            name = path;
            int lastSlashIndex = name.lastIndexOf('/');
            if (lastSlashIndex > 0) {
                name = name.substring(lastSlashIndex + 1);
            }
            int lastPeriodInName = name.lastIndexOf('.');
            if (lastPeriodInName > 0) {
                name = name.substring(0, lastPeriodInName);
            }
        }
        return name;
    }

    public String getDescription() {
        return testplan.getDescription();
    }

    @JsonIgnore
    public TestplanFile getTestplan() {
        return testplan;
    }

    public void setTestplan(TestplanFile testplan) {
        this.testplan = testplan;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    @Override
    public void addToTable(AsciiTable table, Configuration... options) {
        if(Configuration.OptionIsSet(options, INCLUDE_COUNT_OPTION, "true")) {
            table.addRow(getName(), getTestCount(), getPath(), getDescription());
        } else {
            table.addRow(getName(), getPath(), getDescription());
        }
    }

    @Override
    public void addColumnHeadersToTable(AsciiTable table, Configuration... options) {
        if(Configuration.OptionIsSet(options, INCLUDE_COUNT_OPTION, "true")) {
            table.addRow("Name", "Test Count", "Location", "Testplan Description");
        } else {
            table.addRow("Name", "Location", "Testplan Description");
        }
    }
}
