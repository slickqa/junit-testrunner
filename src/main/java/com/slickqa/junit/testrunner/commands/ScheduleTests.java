package com.slickqa.junit.testrunner.commands;

import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.output.OutputFormat;
import com.slickqa.junit.testrunner.output.SchedulingTestExecutionListener;
import com.slickqa.junit.testrunner.output.TestcaseInfo;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import com.slickqa.jupiter.ConfigurationNames;
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

    @CommandLine.Parameters(description = "Places to find testcases to schedule.  You can specify a testplan location, name, or any one of the testcase selectors or filters.", arity = "1..*")
    String[] locators;

    @Override
    public Integer call() throws Exception {
        List<Configuration> configList = new ArrayList<>();
        if(slickOptions == null) {
            System.err.println("ERROR: You must supply slick options when scheduling tests.");
            spec.commandLine().usage(System.err);
            return 1;
        }
        List<String> missingOptions = slickOptions.missingRequiredOptions();
        if(missingOptions.size() > 0) {
            System.err.println("Unable to use slick without the following options:");
            System.err.println(String.join(", ", missingOptions));
        }

        slickOptions.configureEnvironment();
        System.setProperty(ConfigurationNames.SCHEDULE_TEST_MODE, "true");

        if(format == OutputFormat.table) {
            configList.add(Configuration.Value(OutputFormat.COLUMN_WIDTH_OPTION, "30"));
        }

        configList.add(Configuration.Value("junit.jupiter.extensions.autodetection.enabled", "true"));

        Configuration[] config = new Configuration[configList.size()];
        config = configList.toArray(config);
        TestplanFile testplan = TestcaseInfo.locatorsToTesplan(locators);
        LauncherDiscoveryRequest request = testplan.toLauncherDiscoveryRequest(config);
        Launcher launcher = LauncherFactory.create();
        SchedulingTestExecutionListener listener = new SchedulingTestExecutionListener(format, testplan, config);
        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request);

        listener.printSummary(summaryOutput);

        return listener.getResultCode();
    }
}
