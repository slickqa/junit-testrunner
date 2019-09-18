package com.slickqa.junit.testrunner.output;

import com.slickqa.junit.testrunner.Configuration;
import de.vandermeer.asciitable.AsciiTable;

public interface EndUserData {
    void addToTable(AsciiTable table, Configuration... options);
    boolean addColumnHeadersToTable(AsciiTable table, Configuration... options);
}
