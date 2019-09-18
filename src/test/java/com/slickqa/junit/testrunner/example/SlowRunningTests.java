package com.slickqa.junit.testrunner.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.google.common.truth.Truth.assertThat;

public class SlowRunningTests {

    @ParameterizedTest(name="Slow test that sleeps for calls Thread.sleep({0})")
    @ValueSource(ints = {100, 200, 300})
    public void dataDrivenSlowTest(int amount) throws Exception {
        Thread.sleep(amount);
    }

    @Test
    @DisplayName("Simple test that takes 1 second to finish")
    public void oneSecondTest() throws Exception{
        Thread.sleep(1000);
    }

    @Test
    @DisplayName("Simple test that takes one second and fails")
    public void oneSecondFailureTest() throws Exception {
        Thread.sleep(1000);
        assertThat(false).isTrue();
    }

    @Test
    @DisplayName("Simple one second test that throws an exception")
    public void oneSecondExceptionTest() throws Exception {
        System.out.println("Before sleep");
        Thread.sleep(1000);
        System.out.println("After sleep");
        throw new Exception("Broken");
    }
}
