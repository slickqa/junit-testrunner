package com.slickqa.junit.testrunner.commands;

import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.output.OutputFormat;
import com.slickqa.junit.testrunner.output.TestcaseInfo;
import com.slickqa.junit.testrunner.output.TestplanInfo;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name="testplans", aliases = "tp", description = "List testplans"
)
public class ListTestplans implements Callable<Integer> {

    // needed for automatic help
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @CommandLine.Option(names = {"-c", "--count"}, description = "Print a count of the number of testcases in that testplan.")
    boolean count;

    @CommandLine.Option(names={"-f", "--format"}, description = "Output format.  Default is table. You can choose one of table, json, or yaml.")
    OutputFormat format = OutputFormat.table;

    @CommandLine.Option(names = "--testplan-path", description = "Find any testplan that matches (directory ends with) this path.")
    String testplanPath;

    @CommandLine.Mixin
    SystemPropertyOption systemPropertyOptions;

    @Override
    public Integer call() throws Exception {
        if(systemPropertyOptions != null) {
            systemPropertyOptions.setProperties();
        }

        List<TestplanInfo> testplans;
        if(testplanPath != null && !"".equals(testplanPath)) {
            testplans = TestplanInfo.findAvailableTestplans(testplanPath, count);
        } else {
            testplans = TestplanInfo.findAvailableTestplans(count);
        }
        Configuration[] options = new Configuration[0];
        if(count) {
            options = new Configuration[] {Configuration.Value(TestplanInfo.INCLUDE_COUNT_OPTION, "true")};
        }
        // output
        System.out.println(format.generateOutput(testplans, options));

        return 0;
    }

}

