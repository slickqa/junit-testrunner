package com.slickqa.junit.testrunner.listCommand;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name="list", subcommands = {
        ListTestplans.class
})
public class ListCommand implements Callable<Integer> {


    @Override
    public Integer call() throws Exception {
        System.out.println("What do you want to list?  (you can specify testcases or testplans).  See help for more details.");
        return 1;
    }
}
