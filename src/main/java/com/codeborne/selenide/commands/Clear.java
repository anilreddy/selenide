package com.codeborne.selenide.commands;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clean the input field value.
 * <br>
 * <p>
 *  The standard Selenium method {@link WebElement#clear()} does not help in case of
 *  "tricky" inputs generated by Vue.js, React and other fancy frameworks.
 * </p>
 * <br>
 * <p>
 *  That's why we need to clear the field value by emulating real user actions:
 * </p>
 * <ol>
 *   <li>Select the whole text</li>
 *   <li>press "Backspace"</li>
 * </ol>
 */
public class Clear extends FluentCommand {
  private static final Logger log = LoggerFactory.getLogger(Clear.class);

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    WebElement input = locator.findAndAssertElementIsEditable();
    clearAndTrigger(locator.driver(), input);
  }

  /**
   * Clear the input content and trigger "change" and "blur" events
   *
   * <p>
   * This is the shortest keys combination I found in May 2022.<br>
   * It seems to work in Firefox, Chrome on Mac and on Linux smoothly.
   * </p>
   */
  protected void clearAndTrigger(Driver driver, WebElement input) {
    clear(driver, input);
    blurSafely(driver, input);
  }

  protected void blurSafely(Driver driver, WebElement input) {
    try {
      driver.executeJavaScript("arguments[0].blur()", input);
    }
    catch (StaleElementReferenceException elementHasDisappeared) {
      log.debug("The input has disappeared after clearing: {}", elementHasDisappeared.toString());
    }
  }

  /**
   * Clear the input content without triggering "change" and "blur" events
   */
  protected void clear(Driver driver, WebElement input) {
    input.clear();
  }
}
