package com.slickqa.junit.testrunner.output;

import picocli.CommandLine.Help.Ansi;

public class Status {

    public static String getColorizedStatus(String status) {
        String color = getStatusColorName(status);
        if(color != null) {
            return Ansi.AUTO.string("@|" + color + " " + status + "|@");
        } else {
            return status;
        }
    }

    public static String getStatusColorName(String status) {
        if("PASS".equals(status)) {
            return "green";
        } else if("FAIL".equals(status)) {
            return "red";
        } else if("BROKEN".equals(status)) {
            return "yellow";
        } else if("TOTAL".equals(status)) {
            return "bold";
        }
        return null;
    }
}
