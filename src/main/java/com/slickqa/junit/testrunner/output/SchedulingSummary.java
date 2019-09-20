package com.slickqa.junit.testrunner.output;

import com.slickqa.client.model.Result;
import com.slickqa.junit.testrunner.Configuration;
import de.vandermeer.asciitable.AsciiTable;
import org.junit.platform.launcher.TestIdentifier;

import java.util.ArrayList;
import java.util.List;

public class SchedulingSummary implements EndUserData {
    List<Result> scheduled;
    List<TestcaseInfo> notScheduled;
    SchedulingSummaryTotals summary;

    public SchedulingSummary(List<Result> scheduled, List<TestIdentifier> failed) {
        this.scheduled = scheduled;
        this.notScheduled = new ArrayList<>(failed.size());
        for(TestIdentifier id : failed) {
            notScheduled.add(TestcaseInfo.fromTestIdentifier(id));
        }
        this.summary = new SchedulingSummaryTotals(scheduled.size(), notScheduled.size(), scheduled.size() + notScheduled.size());
    }

    @Override
    public void addToTable(AsciiTable table, Configuration... options) {
        this.summary.addToTable(table, options);
    }

    @Override
    public boolean addColumnHeadersToTable(AsciiTable table, Configuration... options) {
        return false;
    }
}

class SchedulingSummaryTotals implements EndUserData {
    int Scheduled;
    int NotScheduled;
    int Total;

    public SchedulingSummaryTotals(int scheduled, int notScheduled, int total) {
        Scheduled = scheduled;
        NotScheduled = notScheduled;
        Total = total;
    }

    public int getScheduled() {
        return Scheduled;
    }

    public void setScheduled(int scheduled) {
        Scheduled = scheduled;
    }

    public int getNotScheduled() {
        return NotScheduled;
    }

    public void setNotScheduled(int notScheduled) {
        NotScheduled = notScheduled;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    @Override
    public void addToTable(AsciiTable table, Configuration... options) {
        table.addRow("Scheduled", getScheduled());
        table.addRule();
        table.addRow("Not Scheduled", getNotScheduled());
        table.addRule();
        table.addRow("Total", getTotal());
        table.addRule();
    }

    @Override
    public boolean addColumnHeadersToTable(AsciiTable table, Configuration... options) {
        return false;
    }
}
