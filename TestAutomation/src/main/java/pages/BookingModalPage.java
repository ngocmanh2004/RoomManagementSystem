package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class BookingModalPage {

    WebDriver driver;

    By modal = By.cssSelector(".modal-content");

    By fullName = By.cssSelector("input[formcontrolname='fullName']");
    By cccd = By.cssSelector("input[formcontrolname='cccd']");
    By phone = By.cssSelector("input[formcontrolname='phone']");
    By address = By.cssSelector("input[formcontrolname='address']");
    By startDate = By.cssSelector("input[formcontrolname='startDate']");
    By endDate = By.cssSelector("input[formcontrolname='endDate']");
    By deposit = By.cssSelector("input[formcontrolname='deposit']");

    By submitBtn = By.xpath("//button[@type='submit']");
    By errorText = By.cssSelector(".error-text");
    By successAlert = By.cssSelector(".alert-success");

    public BookingModalPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isModalDisplayed() {
        return driver.findElements(modal).size() > 0;
    }

    public String getFullName() {
        return driver.findElement(fullName).getAttribute("value");
    }

    public String getPhone() {
        return driver.findElement(phone).getAttribute("value");
    }
    
    public void clearFullName() {
        driver.findElement(fullName).clear();
    }

    public void setCccd(String value) {
        driver.findElement(cccd).clear();
        driver.findElement(cccd).sendKeys(value);
    }

    public void setPhone(String value) {
        driver.findElement(phone).clear();
        driver.findElement(phone).sendKeys(value);
    }

    public void setStartDate(String value) {
        driver.findElement(startDate).sendKeys(value);
    }

    public void setEndDate(String value) {
        driver.findElement(endDate).sendKeys(value);
    }

    public void submit() {
        driver.findElement(submitBtn).click();
    }

    public boolean isErrorDisplayed() {
        return driver.findElements(errorText).size() > 0;
    }

    public boolean isSuccessDisplayed() {
        return driver.findElements(successAlert).size() > 0;
    }
    
    public void scrollDown(int pixel) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0," + pixel + ");");
    }
}
