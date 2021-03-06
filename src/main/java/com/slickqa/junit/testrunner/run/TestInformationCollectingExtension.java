package com.slickqa.junit.testrunner.run;

import com.slickqa.junit.testrunner.output.TestcaseInfo;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.*;

public class TestInformationCollectingExtension implements BeforeEachCallback {
    public static final String SESSION_ID_CONFIGURATION_NAME = "com.slickqa.junit.testrunner.run.TestInformationCollectingExtension";

    private static Map<String, List<TestcaseInfo>> sessions = new HashMap<>();

    public static String createSession() {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, Collections.synchronizedList(new ArrayList<TestcaseInfo>()));
        return sessionId;
    }

    public static List<TestcaseInfo> getTestsFromSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if(context != null) {
            if(context.getConfigurationParameter(SESSION_ID_CONFIGURATION_NAME).isPresent()) {
                getTestsFromSession(context.getConfigurationParameter(SESSION_ID_CONFIGURATION_NAME).get()).add(TestcaseInfo.fromContext(context));
                throw new InterruptedTestExecutionException();
            }
        }
    }
}

