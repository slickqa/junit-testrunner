package com.slickqa.junit.testrunner.commands;

import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.output.OutputFormat;
import com.slickqa.junit.testrunner.output.TestcaseInfo;
import picocli.CommandLine.*;

import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name="testcases",
        aliases = {"tc"},
        description = "List testcases",
        footerHeading = "%n@|bold,underline Examples|@%n",
        footer = {
                "%nList the testcases in a package:%n",
                "  <command> tc packageName:com.slickqa.junit.testrunner",
                "%nList the testcases in a testplan file in the classpath with the name @|underline FeatureOne.yml|@%n",
                "  <command> tc FeatureOne.yml"
        }
)
public class ListTestcases implements Callable<Integer> {
    // needed for automatic help
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @Option(names = {"-f", "--format"}, description = "Output format, default is table. You can choose one of table, json, or yaml.")
    OutputFormat format = OutputFormat.table;

    @Option(names = {"--id"}, description = "include the id in the table.  The other formats always include it.")
    boolean withId;


    @Parameters(description = "Places to find testcases.  You can specify a testplan location, name, or any one of the testcase selectors or filters.", arity="1..*")
    String[] locators;


    @Override
    public Integer call() throws Exception {
        List<TestcaseInfo> tests = TestcaseInfo.findTestcases(locators);
        Configuration[] options = new Configuration[0];
        if(withId) {
            options = new Configuration[] {Configuration.Value(TestcaseInfo.WITH_ID, "true")};
        }
        System.out.println(format.generateOutput(tests, options));

        return 0;
    }
}
