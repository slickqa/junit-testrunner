package com.slickqa.junit.testrunner.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FirstFeatureTests {

    @Test
    @DisplayName("Test that a feature compiles or something.")
    public void testCompileFeature() {}

    @Test
    @DisplayName("Verify feature one has data.")
    public void testDataAvailable() {}

    @Test
    @DisplayName("Feature one does cleanup on normal exit.")
    public void testFeatureOneShutsDown() {}
}
