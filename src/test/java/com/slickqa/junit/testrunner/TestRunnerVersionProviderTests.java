package com.slickqa.junit.testrunner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestRunnerVersionProviderTests {

    TestRunnerVersionProvider versionProvider;

    @BeforeEach
    public void setup() {
        versionProvider = new TestRunnerVersionProvider();
    }

    @Test
    public void versionInfoFileAtRootTakesPrecedence() throws Exception {
        String[] versionInfo = versionProvider.getVersion();
    }
}
