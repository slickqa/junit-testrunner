package com.slickqa.junit.testrunner.example;

import com.slickqa.jupiter.annotations.ParameterizedTest;
import com.slickqa.jupiter.annotations.Step;
import com.slickqa.jupiter.annotations.TestCaseInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;

public class ThirdFeatureTests {

    @Test
    @TestCaseInfo(
            purpose="Verify that with valid credentials, authentication succeds.  This is a happy path test.",
            feature="third",
            component="authentication",
            steps={
                    @Step(step="provide username and password to authentication mechanism",
                          expectation="a session is created and returned"),
                    @Step(step="session is used on another call",
                          expectation="call succeeds because session is valid")
            },
            author="yomama"
    )
    @DisplayName("Verify authentication succeeds with valid credentials")
    public void testAuthenticationWithValidCredentials() {}

    @ParameterizedTest(name = "Verify login fails with username {0} and password {1}")
    @CsvSource({
            "foo, bar",
            "admin, admin",
            ",",
            "admin, password"
    })
    @TestCaseInfo(
            purpose="Verify login attempts fail with some common credentials.",
            feature="third",
            component="authentication",
            steps={
                    @Step(step="Provide username and password to authentication mechanism",
                          expectation="failure is returned")
            },
            author="anonymous"
    )
    public void testAuthenticationWithInvalidCredentialsFails(String username, String password) {}

    @Test
    @DisplayName("Verify missing credentials fails authentication")
    @TestCaseInfo(
            purpose="This test makes sure missing credentials aren't automatically approved.",
            feature="third",
            component="authentication",
            steps={
                    @Step(step="Don't provide credentials, but try to authenticate without them",
                          expectation="Authentication fails with an error.")
            }
    )
    public void testMissingAuthentication() {}
}
