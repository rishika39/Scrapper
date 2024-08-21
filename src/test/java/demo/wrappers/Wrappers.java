package demo.wrappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    ChromeDriver driver;

    public Wrappers(ChromeDriver driver) {
        this.driver = driver;
    }

    public void clickElement(By locator) {
        driver.findElement(locator).click();
    }

    public String getText(By locator) {
        return driver.findElement(locator).getText();
    }

    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    public void clickNextPageButton(By locator) {
        driver.findElement(locator).click();
    }
}
