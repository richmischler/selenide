package com.codeborne.selenide;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.net.URL;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.FIREFOX;
import static com.codeborne.selenide.WebDriverRunner.HTMLUNIT;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class WebDriverRunnerTest {

  static WebDriver driver = mock(WebDriver.class, RETURNS_DEEP_STUBS);

  @Test
  public void allowsToSpecifyCustomWebDriverConfiguration() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = "com.codeborne.selenide.WebDriverRunnerTest$CustomWebDriverProvider";

    try {
      assertSame(driver, WebDriverRunner.getWebDriver());
    } finally {
      WebDriverRunner.closeWebDriver();
      Configuration.browser = System.getProperty("browser", FIREFOX);
    }
  }

  @Test
  public void allowsToSpecifyCustomWebDriverProgrammatically() {
    HtmlUnitDriver myDriver = new HtmlUnitDriver();
    myDriver.setJavascriptEnabled(true);
    WebDriverRunner.setWebDriver(myDriver);
    open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
    try {
      assertSame(myDriver, WebDriverRunner.getWebDriver());
    } finally {
      WebDriverRunner.closeWebDriver();
    }
  }

  @Test
  public void userCanAddWebDriverListeners() {
    WebDriverEventListener listener1 = mock(WebDriverEventListener.class);
    WebDriverRunner.addListener(listener1);
    Configuration.browser = HTMLUNIT;
    URL url = currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html");
    open(url);
    verify(listener1).beforeNavigateTo(eq(url.toString()), any(WebDriver.class));

  }

  public static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver() {
      return driver;
    }
  }
}
