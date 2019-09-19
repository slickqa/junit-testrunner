package com.slickqa.junit.testrunner.output;

import com.slickqa.jupiter.annotations.Step;
import com.slickqa.jupiter.annotations.TestCaseInfo;

import java.util.ArrayList;
import java.util.List;

public class SlickTestInfo {
    String title;
    String purpose;
    String component;
    String feature;
    String automationId;
    String automationKey;
    List<SlickTestStep> steps;
    String triageNote;
    String author;

    public static SlickTestInfo fromAnnotation(TestCaseInfo annotation) {
        SlickTestInfo info = new SlickTestInfo();
        info.title = nullIfEmpty(annotation.title());
        info.purpose = nullIfEmpty(annotation.purpose());
        info.component = nullIfEmpty(annotation.component());
        info.feature = nullIfEmpty(annotation.feature());
        info.automationId = nullIfEmpty(annotation.automationId());
        info.automationKey = nullIfEmpty(annotation.automationKey());
        info.triageNote = nullIfEmpty(annotation.triageNote());
        info.author = nullIfEmpty(annotation.author());
        info.steps = new ArrayList<>(annotation.steps().length);
        for(Step step : annotation.steps()) {
            info.steps.add(SlickTestStep.fromAnnotation(step));
        }

        return info;
    }

    static String nullIfEmpty(String value) {
        if("".equals(value)) {
            return null;
        } else {
            return value;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getAutomationId() {
        return automationId;
    }

    public void setAutomationId(String automationId) {
        this.automationId = automationId;
    }

    public String getAutomationKey() {
        return automationKey;
    }

    public void setAutomationKey(String automationKey) {
        this.automationKey = automationKey;
    }

    public List<SlickTestStep> getSteps() {
        return steps;
    }

    public void setSteps(List<SlickTestStep> steps) {
        this.steps = steps;
    }

    public String getTriageNote() {
        return triageNote;
    }

    public void setTriageNote(String triageNote) {
        this.triageNote = triageNote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

class SlickTestStep {
    String step;
    String expectation;

    public static SlickTestStep fromAnnotation(Step annotation) {
        SlickTestStep step = new SlickTestStep();
        step.step = annotation.step();
        step.expectation = SlickTestInfo.nullIfEmpty(annotation.expectation());
        return step;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getExpectation() {
        return expectation;
    }

    public void setExpectation(String expectation) {
        this.expectation = expectation;
    }
}
