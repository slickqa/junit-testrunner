package com.slickqa.junit.testrunner.commands;

import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.output.OutputFormat;
import com.slickqa.junit.testrunner.testplan.TestplanInfo;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name="testplans", aliases = "tp")
public class ListTestplans implements Callable<Integer> {

    // needed for automatic help
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @CommandLine.Option(names = {"-c", "--count"}, description = "Print a count of the number of testcases in that testplan.")
    boolean count;

    @CommandLine.Option(names={"-f", "--format"}, description = "Output format.  Default is table. You can choose one of table, json, or yaml.")
    OutputFormat format = OutputFormat.table;

    @Override
    public Integer call() throws Exception {
        List<TestplanInfo> testplans = TestplanInfo.findAvailableTestplans(count);
        Configuration[] options = new Configuration[0];
        if(count) {
            options = new Configuration[] {Configuration.Value(TestplanInfo.INCLUDE_COUNT_OPTION, "true")};
        }
        // output
        System.out.println(format.generateOutput(testplans, options));

        return 0;
    }

}

