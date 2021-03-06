package com.slickqa.junit.testrunner.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slickqa.junit.testrunner.output.TestcaseInfo;
import com.slickqa.junit.testrunner.output.TestplanInfo;
import org.junit.platform.commons.JUnitException;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name="dump",
        description="Output all testplans and test cases to json files in a directory structure"
)
public class DumpTestplans implements Callable<Integer> {

    @CommandLine.Parameters(
            index = "0",
            paramLabel = "PATH",
            arity = "1",
            description = "Directory to output json files to, it will be created if it doesn't exist."
    )
    String outputPath;

    @CommandLine.Mixin
    SystemPropertyOption systemPropertyOptions;

    @Override
    public Integer call() throws Exception {
        if(systemPropertyOptions != null) {
            systemPropertyOptions.setProperties();
        }
        Path outputBaseDir = Paths.get(outputPath);
        if(!Files.exists(outputBaseDir)) {
            Files.createDirectories(outputBaseDir);
        }

        ObjectMapper mapper = new ObjectMapper();

        Map<TestplanInfo, List<TestcaseInfo>> testplanMap = new HashMap<>();
        List<TestplanInfo> testplans = TestplanInfo.findAvailableTestplans(false); // don't do count, we'll do that later

        for(TestplanInfo tp : testplans) {
            List<TestcaseInfo> tests = new ArrayList<>();
            try {
                tests = tp.getTestplan().getTests();
            } catch(JUnitException e) {
                System.err.println(MessageFormat.format("Error dumping testplan {0}: {1}", tp.getName(), e.getMessage()));
            }
            tp.setPath(tp.getPath().replace(".yaml", ".json"));
            tp.setPath(tp.getPath().replace(".yml", ".json"));
            testplanMap.put(tp, tests);
            tp.setTestCount(tests.size());
            String[] parts = tp.getPath().split("\\/");
            if(parts.length > 1) {
                Path dir = Paths.get(outputPath);
                for(int i = 0; i < parts.length - 1; i++) {
                    dir = Paths.get(dir.toString(), parts[i]);
                    if(!Files.exists(dir)) {
                        Files.createDirectories(dir);
                    }
                }
            }
            mapper.writeValue(new File(outputPath + File.separator + tp.getPath()), testplanMap.get(tp));
        }

        mapper.writeValue(Paths.get(outputPath, "testplans.json").toFile(), testplans);

        return 0;
    }
}
