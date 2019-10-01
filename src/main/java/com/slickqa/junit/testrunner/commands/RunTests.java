package com.slickqa.junit.testrunner.commands;

import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.output.FormattedExecutionListener;
import com.slickqa.junit.testrunner.output.OutputFormat;
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

@CommandLine.Command(
        name = "run",
        description = "run tests, either from a testplan or from selectors and filters specified on the command line."
)
public class RunTests implements Callable<Integer> {

    // needed for automatic help
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @CommandLine.Option(names = {"-f", "--format"}, description = "Output format for results, default is table. You can choose one of table, json, or yaml.")
    OutputFormat format = OutputFormat.table;

    @CommandLine.Mixin
    SlickOptions slickOptions;

    @CommandLine.Option(names={"--slick-result-id"}, description="If trying to run a test in 'single test mode' updating one result, put the full url of the result here and skip all other slick options.")
    String slickResultUrl;

    @CommandLine.Option(names={"-q", "--quiet", "--summary-only"}, description="Only print the summary data, not live execution results")
    boolean summaryOnly = false;

    @CommandLine.Option(names={"--include-pass"}, description="This option shows details of tests that PASSED when a table output is used.  Normally if table output is selected (default) then details of testcases with PASS status are omitted.")
    boolean includePass = false;

    @CommandLine.Option(names={"--no-capture"}, description="Do not attempt to capture the stdout and stderr of tests.")
    boolean noCapture = false;

    @CommandLine.Option(names={"--junit-capture"}, description="Use junit's capture instead of the one provided by this runner.")
    boolean junitCapture = false;

    @CommandLine.Option(names={"--no-junit-plugin-discovery"}, description="By default this runner turns on junit auto discovery of plugins via service loader.  Use this option to turn it off.")
    boolean noJunitAutoDiscovery = false;

    @CommandLine.Option(names={"-o", "--summary-output"}, description="Output the summary to a file instead of stdout.")
    File summaryOutput;

    @CommandLine.Parameters(description = "Places to find testcases to run.  You can specify a testplan location, name, or any one of the testcase selectors or filters.", arity = "1..*")
    String[] locators;

    @Override
    public Integer call() throws Exception {
        List<Configuration> configList = new ArrayList<>();
        if (slickResultUrl != null) {
            System.setProperty(ConfigurationNames.RESULT_URL, slickResultUrl);
            // in case there were any options like attributes given
            if (slickOptions != null) {
                slickOptions.configureEnvironment();
            }
        } else if (slickOptions != null && slickOptions.anyOptionsPresent()) {
            List<String> missing = slickOptions.missingRequiredOptions();
            if (missing != null && missing.size() > 0) {
                System.err.println("You specified some slick options, but slick reporting won't work without also these options:");
                System.err.println(String.join(", ", missing));
                return 1;
            }
            slickOptions.configureEnvironment();
        }

        if(!noJunitAutoDiscovery) {
            configList.add(Configuration.Value("junit.jupiter.extensions.autodetection.enabled", "true"));
        }

        if(format == OutputFormat.table) {
            configList.add(Configuration.Value(OutputFormat.COLUMN_WIDTH_OPTION, "30"));
        }

        if(noCapture) {
            configList.add(Configuration.Value(FormattedExecutionListener.NO_CAPTURE, "true"));
        } else if(junitCapture){
            configList.add(Configuration.Value(FormattedExecutionListener.JUNIT_CAPTURE, "true"));
            configList.add(Configuration.Value("junit.platform.output.capture.stdout", "true"));
            configList.add(Configuration.Value("junit.platform.output.capture.stderr", "true"));
        }

        if(summaryOnly) {
            configList.add(Configuration.Value(FormattedExecutionListener.SUMMARY_ONLY, "true"));
        }

        if(includePass) {
            configList.add(Configuration.Value(FormattedExecutionListener.INCLUDE_PASS, "true"));
        }

        Configuration[] config = new Configuration[configList.size()];
        config = configList.toArray(config);
        TestplanFile testplan = TestcaseInfo.locatorsToTesplan(locators);
        if(slickOptions.anyOptionsPresent()) {
            SlickOption testplanName = new SlickOption(ConfigurationNames.TESTPLAN_NAME, slickOptions.slickTestplanName, false, "--slick-testplan");
            if (testplanName.getCmdLineValue() == null || "".equals(testplanName.getCmdLineValue())) {
                System.setProperty(ConfigurationNames.TESTPLAN_NAME, testplan.getName());
            }
        }
        LauncherDiscoveryRequest request = testplan.toLauncherDiscoveryRequest(config);
        Launcher launcher = LauncherFactory.create();
        FormattedExecutionListener listener = new FormattedExecutionListener(format, testplan, config);
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        listener.printSummary(summaryOutput);

        return listener.getResultCode();
    }
}
