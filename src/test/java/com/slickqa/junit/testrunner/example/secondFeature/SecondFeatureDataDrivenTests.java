package com.slickqa.junit.testrunner.example.secondFeature;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SecondFeatureDataDrivenTests {

    @ParameterizedTest(name = "Verify rest endpoint one with data: {0}")
    @ValueSource(strings={
            "hello",
            "world",
            "this",
            "is",
            "example",
            "data"
    })
    public void testInputOfRestEndpointOne(String data) {
    }
}
