package com.slickqa.junit.testrunner.output;

import com.slickqa.junit.testrunner.Configuration;
import de.vandermeer.asciitable.AsciiTable;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;

import picocli.CommandLine.Help.Ansi;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class TestResult {
    String name;
    String uniqueId;
    String method;
    String status;
    String error;
    String stdout;
    String stderr;

    public static TestResult fromExecutionResult(TestIdentifier id, TestExecutionResult result) {
        TestResult item = new TestResult();
        item.addDataFromExecutionResult(id, result);
        return item;
    }

    public void addDataFromExecutionResult(TestIdentifier id, TestExecutionResult result) {
        setName(id.getDisplayName());
        setUniqueId(id.getUniqueId());
        if (id.getSource().isPresent() && MethodSource.class.isAssignableFrom(id.getSource().get().getClass())) {
            MethodSource testMethod = (MethodSource) id.getSource().get();
            setMethod(testMethod.getClassName() + "#" + testMethod.getMethodName());
        }

        String status = "BROKEN";
        if (result.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
            status = "PASS";
        } else if (result.getStatus() == TestExecutionResult.Status.ABORTED) {
            status = "ABORT";
        } else if (result.getThrowable().isPresent() && AssertionError.class.isAssignableFrom(result.getThrowable().get().getClass())) {
            status = "FAIL";
        }
        setStatus(status);

        if (result.getThrowable().isPresent()) {
            Throwable testError = result.getThrowable().get();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            testError.printStackTrace(pw);
            setError(sw.toString());
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public void printToOutput(PrintStream out) {
        Ansi ansi = Ansi.OFF;
        if(out == System.out) {
            ansi = Ansi.AUTO;
        }
        out.println(ansi.string(generateFormattedHeader("Name") + getName()));
        if(ansi == Ansi.AUTO) {
            out.println(ansi.string(generateFormattedHeader("Status")) + Status.getColorizedStatus(getStatus()));
        } else {
            out.println("Status: " + getStatus());
        }
        if(getMethod() != null) {
            out.println(ansi.string(generateFormattedHeader("Method") + getMethod()));
        }
        if(getError() != null) {
            out.println(ansi.string(generateFormattedHeader("Error") + "\n" + getError()));
        }
        if(getStdout() != null && !"".equals(getStdout())) {
            String header = getStderr() == null ? "Output" : "Stdout";
            out.println(ansi.string(generateFormattedHeader(header) + "\n" + getStdout()));
        }
        if(getStderr() != null) {
            out.println(ansi.string(generateFormattedHeader("Stderr") + "\n" + getStderr()));
        }
        out.println();
    }

    static String generateFormattedHeader(String name) {
        return "@|bold,underline " + name + ":|@ ";
    }
}
