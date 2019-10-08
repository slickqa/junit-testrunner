package com.slickqa.junit.testrunner.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ThreadedTest {

    @Test
    @DisplayName("This test is bad and starts a never ending thread")
    public void threadedTest() throws Exception {
        Thread t = new Thread() {
            @Override
            public void run() {
                while(true) {
                    System.out.println("Thread still alive");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        t.start();
        Thread.sleep(500);
    }
}
