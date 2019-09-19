package com.slickqa.junit.testrunner.commands;

import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.jupiter.ConfigurationNames;
import com.slickqa.jupiter.PropertyOrEnvVariableConfigurationSource;
import com.slickqa.jupiter.SlickConfigurationSource;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlickOptions {

    SlickOption[] options;

    @CommandLine.Option(names={"--slick-base-url"}, description = "If reporting to slick, ")
    String slickBaseUrl;

    @CommandLine.Option(names={"--slick-project"})
    String slickProject;

    @CommandLine.Option(names={"--slick-release"})
    String slickRelease;

    @CommandLine.Option(names={"--slick-build"})
    String slickBuild;

    @CommandLine.Option(names={"--slick-testrun"})
    String slickTestrunName;

    @CommandLine.Option(names={"--slick-testplan"})
    String slickTestplanName;

    @CommandLine.Option(names={"--slick-attribute"}, description = "Set name=value attributes on the slick result.")
    Map<String, String> slickAttributes = new HashMap<>();

    SlickOption[] getOptions() {
        if(options == null) {
            options = new SlickOption[] {
                    new SlickOption(ConfigurationNames.BASE_URL, slickBaseUrl, true, "--slick-base-url"),
                    new SlickOption(ConfigurationNames.PROJECT_NAME, slickProject, true, "--slick-project"),
                    new SlickOption(ConfigurationNames.RELEASE_NAME, slickRelease, true, "--slick-release"),
                    new SlickOption(ConfigurationNames.BUILD_NAME, slickBuild, true, "--slick-build"),
                    new SlickOption(ConfigurationNames.TESTRUN_NAME, slickTestrunName, false, "--slick-testrun"),
                    new SlickOption(ConfigurationNames.TESTPLAN_NAME, slickTestplanName, false, "--slick-testplan"),
            };
        }
        return options;
    }

    public boolean anyOptionsPresent() {
        for(SlickOption option : getOptions()) {
            if(option.optionIsDefined()) {
                return true;
            }
        }
        if(slickAttributes != null && slickAttributes.size() > 0) {
            return true;
        }

        return false;
    }

    public List<String> missingRequiredOptions() {
        List<String> missing = new ArrayList<>();
        for(SlickOption option : getOptions()) {
            if(option.isRequired() && !option.optionIsDefined()) {
                missing.add(option.getCommandLineOption());
            }
        }
        return missing;
    }

    public void configureEnvironment() {
        for(String key : slickAttributes.keySet()) {
            System.setProperty("attr." + key, slickAttributes.get(key));
        }
        for(SlickOption option : getOptions()) {
            if(option.getCmdLineValue() != null) {
                System.setProperty(option.getName(), option.getCmdLineValue());
            }
        }
    }
}

class SlickOption {
    static SlickConfigurationSource configurationSource = new PropertyOrEnvVariableConfigurationSource();

    String name;
    String cmdLineValue;
    boolean required;
    String commandLineOption = "";

    public SlickOption(String name, String cmdLineValue, boolean required, String commandLineOption) {
        this.name = name;
        this.cmdLineValue = cmdLineValue;
        this.required = required;
        this.commandLineOption = commandLineOption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmdLineValue() {
        return cmdLineValue;
    }

    public void setCmdLineValue(String cmdLineValue) {
        this.cmdLineValue = cmdLineValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getCommandLineOption() {
        return commandLineOption;
    }

    public void setCommandLineOption(String commandLineOption) {
        this.commandLineOption = commandLineOption;
    }

    public String getOptionValue() {
        if(cmdLineValue != null) {
            return cmdLineValue;
        }
        return configurationSource.getConfigurationEntry(name);
    }

    public boolean optionIsDefined() {
        if(getOptionValue() != null) {
            return true;
        } else {
            return false;
        }
    }
}
