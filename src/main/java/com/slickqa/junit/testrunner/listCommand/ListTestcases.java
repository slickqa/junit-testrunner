package com.slickqa.junit.testrunner.listCommand;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.slickqa.junit.testrunner.testinfo.Configuration;
import com.slickqa.junit.testrunner.testinfo.OutputFormat;
import com.slickqa.junit.testrunner.testinfo.TestcaseInfo;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import picocli.CommandLine.*;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name="testcases", aliases = {"tc"})
public class ListTestcases implements Callable<Integer> {
    // needed for automatic help
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @Option(names = {"-f", "--format"}, description = "Output format, default is table. You can choose one of table, json, or yaml.")
    OutputFormat format = OutputFormat.table;

    @Option(names = {"--id"}, description = "include the id in the table.  The other formats always include it.")
    boolean withId;


    @Parameters(index = "0", description = "Path to testplan")
    String testplanLocation;


    @Override
    public Integer call() throws Exception {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        InputStream tpStream = null;
        try {
            tpStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(testplanLocation);
        } catch (Exception e) {
            // do nothing
        }
        if(tpStream == null) {
            System.err.println("Cannot find [" + testplanLocation + "]!");
            return 1;
        }

        TestplanFile tp = null;
        try {
             tp = mapper.readValue(tpStream, TestplanFile.class);
        } catch (Exception e) {
            System.err.println(testplanLocation + " does not seem to be a valid testplan!");
            return 1;
        }

        List<TestcaseInfo> tests = tp.getTests();
        Configuration[] options = new Configuration[0];
        if(withId) {
            options = new Configuration[] {Configuration.Value(TestcaseInfo.WITH_ID, "true")};
        }
        System.out.println(format.generateOutput(tests, options));

        return 0;
    }
}
