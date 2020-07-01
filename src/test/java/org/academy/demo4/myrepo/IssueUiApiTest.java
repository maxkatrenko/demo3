package org.academy.demo4.myrepo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.academy.TestConfigurations;
import org.academy.api.pojo.IssuePojo;
import org.academy.api.requests.IssueRequests;
import org.academy.utils.web.AbstractWebDriver;
import org.academy.web.demo4pages.BasePage;
import org.academy.web.demo4pages.IssuesInRepoPage;
import org.academy.web.demo4pages.MainPage;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Base64;

@Slf4j
public class IssueUiApiTest extends AbstractWebDriver {

    private MainPage mainPage;
    private BasePage basePage;

    @BeforeMethod
    private void login() {
        log.info("logining...");
        mainPage = new MainPage(webDriver, true, TestConfigurations.getUrl());
        basePage = mainPage
                .clickOnSignIn()
                .login();
        log.info("logged");
    }

    @Test
    public void checkNewIssueFunctionWithApiTest() {
        IssueRequests issueRequests = new IssueRequests();
        ObjectMapper objectMapper = new ObjectMapper();
        IssuesInRepoPage issuesInRepoPage = new IssuesInRepoPage(webDriver);

        byte[] decoded = Base64.getDecoder().decode(TestConfigurations.getEncodedApiToken());
        String decodedToken = new String(decoded);

        String nameOfIssue = "test_issue";
        String jsonObject = null;

        IssuePojo issuePojo = new IssuePojo(nameOfIssue, "unused text");

        try {
            jsonObject = objectMapper.writeValueAsString(issuePojo);
        } catch (JsonProcessingException e) {
            Assert.fail();
        }

        issueRequests.createIssue(decodedToken, jsonObject, 201);
        log.info("Issue created");

        boolean isIssuePresent =
                basePage.clickOnRepositoryLink()
                        .clickOnIssuesInRepoLink()
                        .checkIsIssuePresentByName(nameOfIssue);
        Assert.assertTrue(isIssuePresent);

        issuesInRepoPage
                .clickOnIssueByName(nameOfIssue)
                .clickDeleteAndConfirmButton();
        log.info("Issue deleted");
    }

    @AfterMethod
    private void afterMethod() {
        webDriver.get(TestConfigurations.getUrl());
    }
}
