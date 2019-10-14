package com.slickqa.junit.testrunner.output;

import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.testplan.Filter;
import com.slickqa.junit.testrunner.testplan.Selector;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import com.slickqa.jupiter.annotations.TestCaseInfo;
import de.vandermeer.asciitable.AsciiTable;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.launcher.TestIdentifier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestcaseInfo implements EndUserData {
    public static final String WITH_ID = "WITH_ID";
    private String id;
    private String name;
    private Method method;
    private SlickTestInfo testInfo;



    public static List<TestplanFile> findAllTestplansEndingWithPath(String path, boolean count) {
        List<TestplanFile> matching = new ArrayList<>();
        List<TestplanInfo> availableTestplans = TestplanInfo.findAvailableTestplans(path, count);
        for(TestplanInfo testplan : availableTestplans) {
            matching.add(testplan.getTestplan());
        }
        return matching;
    }

    public static TestplanFile locatorsToTesplan(String[] locators) {
        List<Map<Selector, String>> selectors = new ArrayList<>();
        List<Map<Filter, String>> filters = new ArrayList<>();
        List<TestplanInfo> availableTestplans = TestplanInfo.findAvailableTestplans(false);
        TestplanFile testplan = new TestplanFile();

        for (String locator : locators) {
            if (locator.contains(":")) {
                int indexOfColon = locator.indexOf(':');
                if (locator.startsWith("exclude") || locator.startsWith("include")) {
                    String filterName = locator.substring(0, indexOfColon);
                    String filterValue = locator.substring(indexOfColon + 1);
                    Map<Filter, String> filter = new HashMap<>();
                    try {
                        filter.put(Filter.valueOf(filterName), filterValue);
                        filters.add(filter);
                    } catch (Exception e) {
                        //TODO: log for invalid filter
                    }
                } else {
                    String selectorName = locator.substring(0, indexOfColon);
                    String selectorValue = locator.substring(indexOfColon + 1);
                    Map<Selector, String> selector = new HashMap<>();
                    try {
                        selector.put(Selector.valueOf(selectorName), selectorValue);
                        selectors.add(selector);
                    } catch (Exception e) {
                        //TODO: log for invalid selector
                    }
                }
            } else {
                // at this point we assume we are looking for a testplan
                if (locator.endsWith("yml") || locator.endsWith("yaml")) {
                    for (TestplanInfo tp : availableTestplans) {
                        if (tp.getPath().endsWith(locator)) {
                            testplan = tp.getTestplan();
                            break;
                        }
                    }
                } else if (locator.contains("/")) {
                    for (TestplanInfo tp : availableTestplans) {
                        if (tp.getPath().startsWith(locator)) {
                            testplan = tp.getTestplan();
                            break;
                        }
                    }
                } else {
                    // assume they are filtering based on name
                    for (TestplanInfo tp : availableTestplans) {
                        if (tp.getName().contains(locator)) {
                            testplan = tp.getTestplan();
                            break;
                        }
                    }
                }
            }

        }
        for (Map<Selector, String> selector : selectors) {
            testplan.getSelectors().add(selector);
        }
        for (Map<Filter, String> filter : filters) {
            testplan.getFilters().add(filter);
        }

        return testplan;
    }

    public static List<TestcaseInfo> findTestcases(String[] locators) {
        return locatorsToTesplan(locators).getTests();
    }

    public static TestcaseInfo fromContext(ExtensionContext context) {
        TestcaseInfo info = new TestcaseInfo();
        info.setId(context.getUniqueId());
        info.setName(context.getDisplayName());
        info.method = context.getRequiredTestMethod();
        TestCaseInfo slickInfo = info.method.getDeclaredAnnotation(TestCaseInfo.class);
        if (slickInfo != null) {
            if (!"".equals(slickInfo.title())) {
                info.setName(slickInfo.title());
            }
            info.testInfo = SlickTestInfo.fromAnnotation(slickInfo);
        }
        return info;
    }

    public static TestcaseInfo fromTestIdentifier(TestIdentifier id) {
        TestcaseInfo info = new TestcaseInfo();
        info.setId(id.getUniqueId());
        info.setName(id.getDisplayName());
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
        if (method != null) {
            return method.getDeclaringClass().getCanonicalName() + "#" + method.getName();
        }
        return null;
    }

    public SlickTestInfo getTestInfo() {
        return testInfo;
    }

    public void setTestInfo(SlickTestInfo testInfo) {
        this.testInfo = testInfo;
    }

    @Override
    public void addToTable(AsciiTable table, Configuration... options) {
        if (Configuration.OptionIsSet(options, WITH_ID, "true")) {
            table.addRow(getName(), getMethod(), getId());
        } else {
            table.addRow(getName(), getMethod());
        }
    }

    @Override
    public boolean addColumnHeadersToTable(AsciiTable table, Configuration... options) {
        if (Configuration.OptionIsSet(options, WITH_ID, "true")) {
            table.addRow("Name", "Method", "Id");
        } else {
            table.addRow("Name", "Method");
        }
        return true;
    }
}
