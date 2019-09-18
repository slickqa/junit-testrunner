package com.slickqa.junit.testrunner.output;

import com.slickqa.junit.testrunner.Configuration;
import de.vandermeer.asciitable.AsciiTable;
import picocli.CommandLine;
import static picocli.CommandLine.Help.Ansi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestrunSummary implements EndUserData {

    private List<TestResult> results;
    private Map<String, Integer> summary;
    private List<String> summaryKeys;

    public TestrunSummary(List<TestResult> results) {
        this.results = results;
        this.summary = new HashMap<>();
        this.summaryKeys = new ArrayList<>();
        int total = 0;
        this.summary.put("PASS", 0);
        this.summaryKeys.add("PASS");
        this.summary.put("FAIL", 0);
        this.summaryKeys.add("FAIL");
        this.summary.put("BROKEN", 0);
        this.summaryKeys.add("BROKEN");
        for(TestResult result : results) {
            if(!this.summary.containsKey(result.getStatus())) {
                this.summary.put(result.getStatus(), 0);
                this.summaryKeys.add(result.getStatus());
            }
            this.summary.put(result.getStatus(), this.summary.get(result.getStatus()) + 1);
            total += 1;
        }
        this.summary.put("TOTAL", total);
        this.summaryKeys.add("TOTAL");
    }

    @Override
    public void addToTable(AsciiTable table, Configuration... options) {
        for(String status : summaryKeys) {
            table.addRow(status, summary.get(status));
            table.addRule();
        }
    }

    @Override
    public boolean addColumnHeadersToTable(AsciiTable table, Configuration... options) {
        return false;
    }

    public List<TestResult> getResults() {
        return results;
    }

    public Map<String, Integer> getSummary() {
        return summary;
    }
}

