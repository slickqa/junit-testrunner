package com.slickqa.junit.testrunner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class TestRunnerVersionProviderTests {

    TestRunnerVersionProvider versionProvider;

    @BeforeEach
    public void setup() {
        versionProvider = new TestRunnerVersionProvider();
    }

    @Test
    public void versionInfoFileAtRootTakesPrecedence() throws Exception {
        String[] versionInfo = versionProvider.getVersion();
        assertThat(versionInfo.length).isAtLeast(1);
        assertThat(versionInfo[0]).contains("demo");
    }
}
