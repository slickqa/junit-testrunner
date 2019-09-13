package com.slickqa.junit.testrunner.testinfo;

import de.vandermeer.asciitable.AT_Cell;
import de.vandermeer.asciitable.AT_ColumnWidthCalculator;
import de.vandermeer.asciitable.AT_Context;
import de.vandermeer.asciitable.AT_Row;
import de.vandermeer.skb.interfaces.document.TableRowType;

import java.util.LinkedList;
import java.util.List;

public class SmartColumnWidthCalculator implements AT_ColumnWidthCalculator {
    @Override
    public int[] calculateColumnWidths(LinkedList<AT_Row> rows, int colNumbers, AT_Context ctx) {
        return calculateColumnWidths(rows, colNumbers, ctx.getWidth());
    }

    @Override
    public int[] calculateColumnWidths(LinkedList<AT_Row> rows, int colNumbers, int tableWidth) {
        int[] columnWidths = new int[colNumbers];
        int[] min = new int[colNumbers];
        int[] max = new int[colNumbers];
        int[] expansionPotential = new int[colNumbers];
        int totalMinimumWidth = 0;
        int totalExpansionPotential = 0;

        // borders?
        tableWidth -= colNumbers;

        // 1. calculate min and max and expansion potential of the column:
        //    - min is the width of the shortest line
        //    - max is the width of the longest line
        //    - expansion potential is max - min
        boolean headerRowFound = false;
        for(int i = 0; i < rows.size(); i++) {
            AT_Row row = rows.get(i);
            if(row.getType() == TableRowType.CONTENT) {
                boolean isHeaderRow = false;
                if(!headerRowFound) {
                    isHeaderRow = true;
                    headerRowFound = true;
                }
                LinkedList<AT_Cell> cells = row.getCells();
                if (cells != null) {
                    for (int j = 0; j < cells.size(); j++) {
                        AT_Cell cell = cells.get(j);
                        if (cell != null && cell.getContent() != null) {
                            int length = cell.getContent().toString().length();
                            if (isHeaderRow) {
                                // header row
                                min[j] = length;
                                max[j] = length;
                                columnWidths[j] = length;
                                totalMinimumWidth += length;
                            } else {
                                if (length > max[j]) {
                                    max[j] = length;
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Cells are null for row " + i);
                }
            }
        }

        // 2. calculate available column space (width - min of all columns)
        int availableWidth = tableWidth - totalMinimumWidth;
        if(availableWidth <= 0) {
            return columnWidths;
        }

        // 3. allocate available by % of expansion potential (always round down)
        for(int i = 0; i < colNumbers; i++) {
            expansionPotential[i] = max[i] - min[i];
            totalExpansionPotential += expansionPotential[i];
        }

        for(int i = 0; i < colNumbers; i++) {
            double expansionPotentialPercentage = (double) expansionPotential[i] / totalExpansionPotential;
            columnWidths[i] += (int) Math.floor(expansionPotentialPercentage * availableWidth);

        }
        // process:
        return columnWidths;
    }
}
