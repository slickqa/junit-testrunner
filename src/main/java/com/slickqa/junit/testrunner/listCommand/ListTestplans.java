package com.slickqa.junit.testrunner.listCommand;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jakewharton.fliptables.FlipTable;
import com.slickqa.junit.testrunner.testinfo.Configuration;
import com.slickqa.junit.testrunner.testinfo.TestInformationCollectingExtension;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

@CommandLine.Command(name="testplans")
public class ListTestplans implements Callable<Integer> {

    // needed for automatic help
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Print everything found, even if it turns out not to be a testplan.")
    boolean verbose;

    @CommandLine.Option(names = {"-c", "--count"}, description = "Print a count of the number of testcases in that testplan.")
    boolean count;

    @Override
    public Integer call() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ResourceList potentialTestplansList = (new ClassGraph()).scan().getResourcesMatchingPattern(Pattern.compile("^.*\\.(yml|yaml)"));
        List<TestplanInfo> testplans = new ArrayList<>();
        for(Resource potentialTestplan : potentialTestplansList) {
            try {
                TestplanFile tp = mapper.readValue(potentialTestplan.getURL(), TestplanFile.class);
                if (tp != null) {
                    if (tp.getName() == null || "".equals(tp.getName())) {
                        String name = potentialTestplan.getPathRelativeToClasspathElement();
                        int lastSlashIndex = name.lastIndexOf('/');
                        if (lastSlashIndex > 0) {
                            name = name.substring(lastSlashIndex + 1);
                        }
                        int lastPeriodInName = name.lastIndexOf('.');
                        if (lastPeriodInName > 0) {
                            name = name.substring(0, lastPeriodInName);
                        }
                        tp.setName(name);
                    }
                    TestplanInfo info = new TestplanInfo();
                    info.setTestplan(tp);
                    info.setPath(potentialTestplan.getPathRelativeToClasspathElement());
                    if(count) {
                        String sessionId = TestInformationCollectingExtension.createSession();
                        LauncherDiscoveryRequest request = tp.toLauncherDiscoveryRequest(Configuration.Value(TestInformationCollectingExtension.SESSION_ID_CONFIGURATION_NAME, sessionId),
                                Configuration.Value("junit.jupiter.extensions.autodetection.enabled", "true"));
                        Launcher launcher = LauncherFactory.create();
                        launcher.execute(request);
                        info.setTestCount(TestInformationCollectingExtension.getTestsFromSession(sessionId).size());
                    }
                    testplans.add(info);
                }
            } catch (Exception e) {
                if (verbose) {
                    System.out.println("Not a testplan: " + potentialTestplan.getPathRelativeToClasspathElement() + " -- " + e.getMessage());
                }
            } finally {
                potentialTestplan.close();
            }
        }
        String[] columns;
        if(count) {
            columns = new String[] {"Name", "Test Count", "Location", "Description"};
        } else {
            columns = new String[] {"Name", "Location", "Description"};
        }
        String[][] data = new String[testplans.size()][];
        for(int i = 0; i < testplans.size(); i++) {
            data[i] = new String[columns.length];
            TestplanInfo info = testplans.get(i);
            int j = 0;
            data[i][j++] = info.getTestplan().getName();
            if (count) {
                data[i][j++] = Integer.toString(info.getTestCount());
            }
            data[i][j++] = info.getPath();
            data[i][j++] = info.getTestplan().getDescription();
        }
        System.out.println(FlipTable.of(columns, data));
        return 0;
    }
}

class TestplanInfo {
    TestplanFile testplan;
    String path;
    int testCount;



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
}
