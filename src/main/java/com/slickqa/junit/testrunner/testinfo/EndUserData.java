package com.slickqa.junit.testrunner.testinfo;

import de.vandermeer.asciitable.AsciiTable;

public interface EndUserData {
    void addToTable(AsciiTable table, Configuration... options);
    void addColumnHeadersToTable(AsciiTable table, Configuration... options);
}
