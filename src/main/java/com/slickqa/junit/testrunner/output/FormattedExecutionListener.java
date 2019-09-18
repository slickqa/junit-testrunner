package com.slickqa.junit.testrunner.output;

import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.TerminalWidthProvider;
import com.slickqa.junit.testrunner.testplan.TestplanFile;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import picocli.CommandLine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormattedExecutionListener implements TestExecutionListener {
    public static final String SUMMARY_ONLY="SUMMARY_ONLY";
    public static final String INCLUDE_PASS="INCLUDE_PASS";
    public static final String NO_CAPTURE="NO_CAPTURE";
    public static final String JUNIT_CAPTURE="JUNIT_CAPTURE";

    TestplanFile testplan;
    OutputFormat format;
    List<TestResult> results;
    Map<TestIdentifier, TestResult> resultMap;
    Configuration[] options;
    PrintStream realOut;
    PrintStream realErr;
    ByteArrayOutputStream capture;
    TestrunSummary summary;


    public FormattedExecutionListener(OutputFormat format, TestplanFile testplan, Configuration... options) {
        this.format = format;
        this.testplan = testplan;
        this.results = new ArrayList<>();
        this.resultMap = new HashMap<>();
        this.options = options;
        this.realOut = System.out;
        this.realErr = System.err;
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if(testIdentifier.isTest()) {
            if(!Configuration.OptionIsSet(options, NO_CAPTURE, "true") &&
                    !Configuration.OptionIsSet(options, JUNIT_CAPTURE, "true")) {
                if(!Configuration.OptionIsSet(options, SUMMARY_ONLY, "true")) {
                    System.out.print(testIdentifier.getDisplayName() + "...");
                    System.out.flush();
                }
                capture = new ByteArrayOutputStream();
                PrintStream capturePrintStream;
                try {
                    capturePrintStream = new PrintStream(capture, true, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    capturePrintStream = new PrintStream(capture, true);
                }
                System.setOut(capturePrintStream);
                System.setErr(capturePrintStream);
            } else if(!Configuration.OptionIsSet(options, SUMMARY_ONLY, "true")) {
                System.out.println("----- Running " + testIdentifier.getDisplayName());
            }
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if(testIdentifier.isTest()) {
            TestResult result = null;
            if(resultMap.containsKey(testIdentifier)) {
                result = resultMap.get(testIdentifier);
                result.addDataFromExecutionResult(testIdentifier, testExecutionResult);
            } else {
                result = TestResult.fromExecutionResult(testIdentifier, testExecutionResult);
            }
            if(!Configuration.OptionIsSet(options, SUMMARY_ONLY, "true")) {
                if(!Configuration.OptionIsSet(options, JUNIT_CAPTURE, "true") &&
                   !Configuration.OptionIsSet(options, NO_CAPTURE, "true")) {
                    System.setOut(realOut);
                    System.setErr(realErr);
                    result.setStdout(new String(capture.toByteArray(), StandardCharsets.UTF_8));
                    System.out.println(Status.getColorizedStatus(result.getStatus()));
                } else {
                    System.out.println("----- " + result.getStatus() + ": " + result.getName());
                }
            }
            results.add(result);
            resultMap.put(testIdentifier, result);
        }
    }

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        if(testIdentifier.isTest()) {
            if (!resultMap.keySet().contains(testIdentifier)) {
                resultMap.put(testIdentifier, new TestResult());
            }
            TestResult result = resultMap.get(testIdentifier);
            for (Map.Entry<String, String> reportItem : entry.getKeyValuePairs().entrySet()) {
                if ("stdout".equals(reportItem.getKey())) {
                    result.setStdout(reportItem.getValue());
                } else if ("stderr".equals(reportItem.getKey())) {
                    result.setStderr(reportItem.getValue());
                } else {
                    System.err.println("Unknown report item published " + reportItem.getKey() + "=" + reportItem.getValue());
                }
            }
        }
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
    }

    public void printSummary(File output) {
        summary = new TestrunSummary(results);
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
        out.println(format.generateOutput(summary, options));
        out.println();
        out.println();
        if(OutputFormat.table == format) {
            boolean includePass = Configuration.OptionIsSet(options, INCLUDE_PASS, "true");
            for(TestResult result : summary.getResults()) {
                if(includePass || !"PASS".equals(result.getStatus())) {
                    result.printToOutput(out);
                }
            }
        }
    }

    public int getResultCode() {
        return 0;
    }
}
