package com.slickqa.junit.testrunner.output;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import de.vandermeer.asciitable.AsciiTable;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TestplanInfo implements EndUserData {
    public static final String INCLUDE_COUNT_OPTION = "INCLUDE_COUNT";

    TestplanFile testplan;
    String path;
    int testCount;

    public static List<TestplanInfo> findAvailableTestplans(String path, boolean count) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ResourceList potentialTestplansList = (new ClassGraph()).scan().getResourcesMatchingPattern(Pattern.compile("^.*\\.(yml|yaml)"));
        List<TestplanInfo> testplans = new ArrayList<>();
        for(Resource potentialTestplan : potentialTestplansList) {
            try {
                if(path != null && !resourceInPath(potentialTestplan, path)) {
                    continue;
                }
                TestplanFile tp = mapper.readValue(potentialTestplan.getURL(), TestplanFile.class);
                if (tp != null) {
                    TestplanInfo info = new TestplanInfo();
                    info.setTestplan(tp);
                    info.setPath(potentialTestplan.getPathRelativeToClasspathElement());
                    if (count) {
                        info.setTestCount(tp.getTests().size());
                    }
                    testplans.add(info);
                }

            } catch (Exception e) {
                // do nothing
            } finally {
                potentialTestplan.close();
            }
        }
        return testplans;
    }

    public static boolean resourceInPath(Resource resource, String path) {
        String dirname = "/";
        int additional = 0;
        if(path.endsWith("/")) {
            additional = 1;
        }
        if(resource.getPathRelativeToClasspathElement().contains("/")) {
            dirname = resource.getPathRelativeToClasspathElement();
            dirname = dirname.substring(0, dirname.lastIndexOf('/') + additional);
        }
        return dirname.endsWith(path);
    }

    public static List<TestplanInfo> findAvailableTestplans(boolean count) {
        return findAvailableTestplans(null, count);
    }

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
    public boolean addColumnHeadersToTable(AsciiTable table, Configuration... options) {
        if(Configuration.OptionIsSet(options, INCLUDE_COUNT_OPTION, "true")) {
            table.addRow("Name", "Test Count", "Location", "Testplan Description");
        } else {
            table.addRow("Name", "Location", "Testplan Description");
        }
        return true;
    }
}
