package com.slickqa.junit.testrunner;

import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class TerminalWidthProvider {
    private static Integer width = null;

    public static synchronized int width() {
        if(width == null) {
            if(System.getenv("COLUMNS") != null) {
                width = Integer.parseInt(System.getenv("COLUMNS"));
            } else {
                try {
                    width = TerminalBuilder.terminal().getWidth();
                } catch (IOException e) {
                    width = 80;
                }
            }
        }
        return width;
    }
}
