package com.slickqa.junit.testrunner.listCommand;

import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name="testcases", aliases = {"tc"})
public class ListTestcases implements Callable<Integer> {
    // needed for automatic help
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @Override
    public Integer call() throws Exception {

        return 0;
    }
}
