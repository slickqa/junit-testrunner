package com.slickqa.junit.testrunner.commands;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name="describe-tp", aliases = "dtp", description = "Describe the format of testplan files",
        footerHeading = "%n@|bold,underline Testplan Files|@%n%n", footer = {
        "Testplans are yaml files, usually embedded in a jar of tests.  The runner searches for any file with the " +
                "ending of '.yml' or '.yaml' and tries to read it as a testplan.  There are 4 properties of a testplan:%n",
        "  @|bold * |@@|bold,underline name|@@|bold :|@ This should be a string (without '/' or '?' in it) that is a short description of the testplan.",
        "  @|bold * |@@|bold,underline description|@@|bold :|@ This should be a longer description of the purpose of the testplan.",
        "  @|bold * |@@|bold,underline selectors|@@|bold :|@ A list of selectors that specify what tests should be loaded.",
        "  @|bold * |@@|bold,underline filters|@@|bold :|@ A list of filters that include or exclude tests.",
        "%nThe selectors are @|bold OR|@'ed together, the filters are @|bold AND|@'ed together.",
        "%n@|bold,underline Selectors:|@%n",
        "  @|bold * |@@|bold,underline className|@@|bold :|@ A DiscoverySelector that selects a Class or class name so that TestEngines can discover tests or containers based on classes.",
        "  @|bold * |@@|bold,underline classPathResource|@@|bold :|@ A DiscoverySelector that selects the name of a classpath resource so that TestEngines can load resources from the classpath — for example, to load XML or JSON files from the classpath, potentially within JARs.",
        "  @|bold * |@@|bold,underline directory|@@|bold :|@ A DiscoverySelector that selects a directory so that TestEngines can discover tests or containers based on directories in the file system.",
        "  @|bold * |@@|bold,underline file|@@|bold :|@ A DiscoverySelector that selects a file so that TestEngines can discover tests or containers based on files in the file system.",
        "  @|bold * |@@|bold,underline method|@@|bold :|@ A DiscoverySelector that selects a Method or a combination of class name, method name, and parameter types so that TestEngines can discover tests or containers based on methods.",
        "  @|bold * |@@|bold,underline module|@@|bold :|@ A DiscoverySelector that selects a module name so that TestEngines can discover tests or containers based on modules.",
        "  @|bold * |@@|bold,underline packageName|@@|bold :|@ A DiscoverySelector that selects a package name so that TestEngines can discover tests or containers based on packages.",
        "  @|bold * |@@|bold,underline uniqueId|@@|bold :|@ A DiscoverySelector that selects a UniqueId so that TestEngines can discover tests or containers based on unique IDs.",
        "  @|bold * |@@|bold,underline uri|@@|bold :|@ A DiscoverySelector that selects a URI so that TestEngines can discover tests or containers based on URIs.",
        "%nFurther information about selectors can be found from junit documentation:%nhttps://junit.org/junit5/docs/current/api/org/junit/platform/engine/discovery/package-summary.html",
        "%n@|bold,underline Filters:|@%n",
        "  @|bold * |@@|bold,underline includeClassNames|@@|bold :|@ This can be a regex of classnames (with package) to include",
        "  @|bold * |@@|bold,underline excludeClassNames|@@|bold :|@ This can be a regex of classnames (with package) to exclude",
        "  @|bold * |@@|bold,underline includeTags|@@|bold :|@ A Tag query language string, see: https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering",
        "  @|bold * |@@|bold,underline excludeTags|@@|bold :|@ A Tag query language string, see: https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering",
        "  @|bold * |@@|bold,underline includePackageName|@@|bold :|@ Package name to exclusively include in the set of running tests.",
        "  @|bold * |@@|bold,underline excludePackageName|@@|bold :|@ Package name to exclude in the set of running tests.",
        "%nRemember selectors are applied first, then filters.  Selectors do discovery of available tests, and filters shrink that to what you want.",
        "%n@|bold,underline Example Testplan|@%n",
        "In this example testplan all tests from package com.slickqa.junit.testrunner.example.secondFeature are used except for those in the SecondFeatureDataDrivenTests class (in that package):%n",
        "name: Feature Two Without Datadriven",
        "description: All the tests for feature two without the data driven ones",
        "selectors:",
        "  - packageName: com.slickqa.junit.testrunner.example.secondFeature",
        "filters:",
        "  - excludeClassNames: .*SecondFeatureDataDrivenTests$"
})
public class TestplanFilesHelp implements Callable<Integer> {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public Integer call() throws Exception {
        CommandLine cmd = spec.commandLine();
        cmd.usage(cmd.getOut());
        return cmd.getCommandSpec().exitCodeOnUsageHelp();
    }
}
