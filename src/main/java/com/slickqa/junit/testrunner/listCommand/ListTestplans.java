package com.slickqa.junit.testrunner.listCommand;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.slickqa.junit.testrunner.testinfo.Configuration;
import com.slickqa.junit.testrunner.testinfo.OutputFormat;
import com.slickqa.junit.testrunner.testinfo.TestplanInfo;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

@CommandLine.Command(name="testplans", aliases = "tp")
public class ListTestplans implements Callable<Integer> {

    // needed for automatic help
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Print everything found, even if it turns out not to be a testplan.")
    boolean verbose;

    @CommandLine.Option(names = {"-c", "--count"}, description = "Print a count of the number of testcases in that testplan.")
    boolean count;

    @CommandLine.Option(names={"-f", "--format"}, description = "Output format.  Default is table. You can choose one of table, json, or yaml.")
    OutputFormat format = OutputFormat.table;

    @Override
    public Integer call() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ResourceList potentialTestplansList = (new ClassGraph()).scan().getResourcesMatchingPattern(Pattern.compile("^.*\\.(yml|yaml)"));
        List<TestplanInfo> testplans = new ArrayList<>();
        for(Resource potentialTestplan : potentialTestplansList) {
            try {
                TestplanFile tp = mapper.readValue(potentialTestplan.getURL(), TestplanFile.class);
                if (tp != null) {
                    TestplanInfo info = new TestplanInfo();
                    info.setTestplan(tp);
                    info.setPath(potentialTestplan.getPathRelativeToClasspathElement());
                    if(count) {
                        info.setTestCount(tp.getTests().size());
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

        Configuration[] options = new Configuration[0];
        if(count) {
            options = new Configuration[] {Configuration.Value(TestplanInfo.INCLUDE_COUNT_OPTION, "true")};
        }
        // output
        System.out.println(format.generateOutput(testplans, options));

        return 0;
    }
}

