package com.slickqa.junit.testrunner;

import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class TestRunnerVersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        // look for /version-info.txt in the classpath
        // if no /version-info.txt read /com/slickqa/junit/testrunner/version-info.txt
        InputStream versionInfoFile = null;
        try {
            versionInfoFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("version-info.txt");
        } catch (Exception e) {
        }
        if(versionInfoFile == null) {
            try {
                versionInfoFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/slickqa/junit/testrunner/version-info.txt");
            } catch (Exception e) {
            }
        }
        if (versionInfoFile != null) {
            ArrayList<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(versionInfoFile))) {
                while (br.ready()) {
                    lines.add(br.readLine());
                }
            }
            return lines.toArray(new String[0]);
        }

        return new String[0];
    }
}
