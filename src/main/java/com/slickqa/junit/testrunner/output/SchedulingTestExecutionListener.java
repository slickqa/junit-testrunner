package com.slickqa.junit.testrunner.output;

import com.slickqa.client.model.Result;
import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import com.slickqa.jupiter.SlickJunitController;
import com.slickqa.jupiter.SlickJunitControllerFactory;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SchedulingTestExecutionListener  implements TestExecutionListener {
    public static final String SUMMARY_ONLY="SUMMARY_ONLY";

    TestplanFile testplan;
    OutputFormat format;
    List<Result> results;
    List<TestIdentifier> failedToSchedule;
    Configuration[] options;

    public SchedulingTestExecutionListener(OutputFormat format, TestplanFile testplan, Configuration... options) {
        this.format = format;
        this.testplan = testplan;
        this.results = new ArrayList<>();
        this.failedToSchedule = new ArrayList<>();
        this.options = options;
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if(testIdentifier.isTest()) {
            if(!Configuration.OptionIsSet(options, SUMMARY_ONLY, "true")) {
                    System.out.print(testIdentifier.getDisplayName() + "...");
                    System.out.flush();
            }
        }
    }

    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (testIdentifier.isTest()) {
            SlickJunitController slickController = SlickJunitControllerFactory.getControllerInstance();
            Result result = slickController.getResultFor(testIdentifier.getUniqueId());
            if(result == null) {
                failedToSchedule.add(testIdentifier);
                if(!Configuration.OptionIsSet(options, SUMMARY_ONLY, "true")) {
                    System.out.println("Failed to Schedule");
                }
            } else {
                results.add(result);
                if(!Configuration.OptionIsSet(options, SUMMARY_ONLY, "true")) {
                    System.out.println("Scheduled.");
                }
            }
        }
    }


    public void printSummary(File output) {
        SchedulingSummary summary = new SchedulingSummary(results, failedToSchedule);

        PrintStream out = System.out;

        if(output != null) {
            try {
                if(!output.exists()) {
                    output.createNewFile();
                }
                out = new PrintStream(new FileOutputStream(output));
            } catch (IOException e) {
                System.err.println("Unable to set output to " + output.getName() + ": " + e.getMessage());
            }
        }
        if(format == OutputFormat.table && !Configuration.OptionIsSet(options, SUMMARY_ONLY, "true")) {
            out.println();
        }
        out.println(format.generateOutput(summary, options));
    }

    public int getResultCode() {
        if(failedToSchedule.size() == 0) {
            return 0;
        } else {
            return 1;
        }
    }
}
