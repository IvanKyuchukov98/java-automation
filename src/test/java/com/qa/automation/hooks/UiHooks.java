package com.qa.automation.hooks;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.qa.automation.config.Config;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.selenide.AllureSelenide;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class UiHooks {

    @Before("@ui")
    public void startBrowser() {
        Configuration.baseUrl = Config.uiBaseUrl();
        Configuration.browser = Config.get("browser");
        Configuration.headless = Config.getBoolean("browser.headless");
        Configuration.browserSize = Config.get("browser.size");
        Configuration.timeout = Config.getLong("timeout.ms");
        Configuration.reportsFolder = "target/selenide-reports";
        Configuration.downloadsFolder = "target/downloads";

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
    }

    @After("@ui")
    public void closeBrowser(Scenario scenario) {
        try {
            if (scenario.isFailed() && WebDriverRunner.hasWebDriverStarted()) {
                byte[] screenshot = ((TakesScreenshot) WebDriverRunner.getWebDriver())
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failure screenshot");
            }
        } finally {
            SelenideLogger.removeListener("AllureSelenide");
            Selenide.closeWebDriver();
        }
    }
}
