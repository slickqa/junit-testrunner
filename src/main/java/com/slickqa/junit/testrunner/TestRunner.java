/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.slickqa.junit.testrunner;

import com.slickqa.junit.testrunner.commands.ListTestcases;
import com.slickqa.junit.testrunner.commands.ListTestplans;
import com.slickqa.junit.testrunner.commands.TestplanFilesHelp;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(mixinStandardHelpOptions = true, versionProvider = TestRunnerVersionProvider.class,
         subcommands = {
            HelpCommand.class,
            ListTestcases.class,
            ListTestplans.class,
            TestplanFilesHelp.class
         })
public class TestRunner {

    static {
        if(System.getProperty("java.util.logging.config.file") == null) {
            URL configResource = TestRunner.class.getClassLoader()
                    .getResource("logging.properties");
            if(configResource == null) {
                configResource = TestRunner.class.getClassLoader()
                        .getResource("com/slickqa/junit/testrunner/default-logging.properties");
            }

            System.setProperty("java.util.logging.config.file", configResource.getFile());
        }
    }

    /*
    commands:
      run:
        - takes standard run options
        - runs tests specified
      schedule:
        - takes standard run options
        - configures slick plugin (system property) to schedule only
      list:
        testcases:
          - takes standard run options
          - activates no-execute jupiter plugin
          - prints names of tests
        testplans:
          - looks in testplans directory (local and in classpath), finds all .yml / .yaml files, and parses and prints
            the names, optionaly descriptions
          - can print counts, if option is selected
          - can print testcases
      export
        - exports to an output directory a testplans.json and a json for each testplan that lists testcases.
      help
        - help for each command
        - help for testplan format
          - list selectors and help for each
          - list filters and help for each

      standard run options:
        positional arguments can be:
          - selector:value
          - filter:value
          - uniqueId
          - testplan (can be local path, or in classpath)
        - -D (set system property)
        -

     */

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new TestRunner());
        cmd.setUsageHelpWidth(TerminalWidthProvider.width());
        cmd.execute(args);
        /*
        try {
            TestplanFile testplan = TestplanFile.readFrom(new File(args[0]));
            System.out.println("We read in testplan " + args[0] + ", there are " + testplan.getSelectors().size() + " selectors and " + testplan.getFilters().size() + " filters.");
            LauncherDiscoveryRequest request = testplan.toLauncherDiscoveryRequest();
            System.out.println("Built a launcher request.");
            Launcher junit = LauncherFactory.create();
            System.out.println("Executing tests...");
            SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
            junit.execute(request, summaryListener);
            TestExecutionSummary summary = summaryListener.getSummary();
            summary.printTo(new PrintWriter(System.out));
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
    }
}
