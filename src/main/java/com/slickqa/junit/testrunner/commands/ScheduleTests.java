package com.slickqa.junit.testrunner.commands;

import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.output.OutputFormat;
import com.slickqa.junit.testrunner.output.SchedulingTestExecutionListener;
import com.slickqa.junit.testrunner.output.TestcaseInfo;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import com.slickqa.jupiter.ConfigurationNames;
import com.slickqa.jupiter.SlickJunitControllerFactory;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name="schedule", aliases = {"sched"}, description = "Schedule tests in slick so that they can be run distributed.")
public class ScheduleTests implements Callable<Integer> {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    // needed for automatic help
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @CommandLine.Option(names = {"-f", "--format"}, description = "Output format for results, default is table. You can choose one of table, json, or yaml.")
    OutputFormat format = OutputFormat.table;

    @CommandLine.Option(names = {"-q", "--quiet", "--summary-only"}, description = "Don't print out the tests as they are scheduled, only print out the summary.")
    boolean summaryOnly = false;

    @CommandLine.Mixin
    SlickOptions slickOptions;

    @CommandLine.Option(names={"-o", "--output"}, description="Output the summary to a file instead of stdout.")
    File summaryOutput;

    @CommandLine.Option(names = "--testplan-path", description = "Find any testplan that matches (directory ends with) this path and run it.")
    String testplanPath;

    @CommandLine.Parameters(description = "Places to find testcases to schedule.  You can specify a testplan location, name, or any one of the testcase selectors or filters.")
    String[] locators;

    @Override
    public Integer call() throws Exception {
        handleSlickOptions();
        Configuration[] config = configurationOptions();
        List<TestplanFile> testplans = loadTestplans();

        int resultCode = 0;
        for(TestplanFile testplan : testplans) {
            SlickOption testplanName = new SlickOption(ConfigurationNames.TESTPLAN_NAME, slickOptions.slickTestplanName, false, "--slick-testplan");
            if (testplanName.getCmdLineValue() == null || "".equals(testplanName.getCmdLineValue()) || testplanPath != null) {
                System.setProperty(ConfigurationNames.TESTPLAN_NAME, testplan.getName());
            }
            LauncherDiscoveryRequest request = testplan.toLauncherDiscoveryRequest(config);
            Launcher launcher = LauncherFactory.create();
            SchedulingTestExecutionListener listener = new SchedulingTestExecutionListener(format, testplan, config);
            launcher.registerTestExecutionListeners(listener);

            launcher.execute(request);
            listener.printSummary(summaryOutput);
            if(listener.getResultCode() != 0) {
                resultCode = listener.getResultCode();
            }
            SlickJunitControllerFactory.INSTANCE = null;
        }

        System.exit(resultCode);
        return resultCode;
    }

    public List<TestplanFile> loadTestplans() {
        if(locators != null && locators.length > 0 && testplanPath != null) {
            System.err.println("You specified standard locators as well as a testplan path.  This is invalid.");
            System.exit(1);
        }
        if(locators != null && locators.length == 0 && testplanPath == null) {
            System.err.println("You must specify either a locator or a testplan path");
            spec.commandLine().usage(System.err);
            System.exit(1);
        }

        List<TestplanFile> testplans = new ArrayList<>();
        if(testplanPath != null && !"".equals(testplanPath)) {
            testplans.addAll(TestcaseInfo.findAllTestplansEndingWithPath(testplanPath, false));
        } else {
            testplans.add(TestcaseInfo.locatorsToTesplan(locators));
        }

        return testplans;
    }

    public Configuration[] configurationOptions() {
        List<Configuration> configList = new ArrayList<>();
        if(format == OutputFormat.table) {
            configList.add(Configuration.Value(OutputFormat.COLUMN_WIDTH_OPTION, "30"));
        }

        configList.add(Configuration.Value("junit.jupiter.extensions.autodetection.enabled", "true"));

        Configuration[] config = new Configuration[configList.size()];
        config = configList.toArray(config);
        return config;
    }

    public void handleSlickOptions() {
        if(slickOptions == null) {
            System.err.println("ERROR: You must supply slick options when scheduling tests.");
            spec.commandLine().usage(System.err);
            System.exit(1);
        }
        List<String> missingOptions = slickOptions.missingRequiredOptions();
        if(missingOptions.size() > 0) {
            System.err.println("Unable to use slick without the following options:");
            System.err.println(String.join(", ", missingOptions));
        }

        slickOptions.configureEnvironment();
        System.setProperty(ConfigurationNames.SCHEDULE_TEST_MODE, "true");
    }
}
