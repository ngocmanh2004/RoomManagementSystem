package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UserFormModalPage {

    WebDriver driver;
    
    By modal = By.cssSelector(".modal-overlay .modal-content");
    By usernameInput = By.cssSelector("input[formControlName='username']");
    By passwordInput = By.cssSelector("input[formControlName='password']");
    By fullNameInput = By.cssSelector("input[formControlName='fullName']");
    By emailInput = By.cssSelector("input[formControlName='email']");
    By phoneInput = By.cssSelector("input[formControlName='phone']");
    By roleSelect = By.cssSelector("select[formControlName='role']");
    By statusSelect = By.cssSelector("select[formControlName='status']");
    By saveBtn = By.cssSelector(".modal-footer .btn-primary");
    By cancelBtn = By.cssSelector(".modal-footer .btn-secondary");
    By requiredError = By.cssSelector(".error-text");
    By emailPatternError = By.xpath("//small[contains(text(),'Email không đúng định dạng')]");
    By phonePatternError = By.xpath("//small[contains(text(),'SĐT không hợp lệ')]");
    By passwordPatternError = By.xpath("//small[contains(text(),'password')]");

    public UserFormModalPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterUsername(String username) {
        WebElement input = driver.findElement(usernameInput);
        input.clear();
        input.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement input = driver.findElement(passwordInput);
        input.clear();
        input.sendKeys(password);
    }

    public void enterFullName(String fullName) {
        WebElement input = driver.findElement(fullNameInput);
        input.clear();
        input.sendKeys(fullName);
    }

    public void enterEmail(String email) {
        WebElement input = driver.findElement(emailInput);
        input.clear();
        input.sendKeys(email);
    }

    public void enterPhone(String phone) {
        WebElement input = driver.findElement(phoneInput);
        input.clear();
        input.sendKeys(phone);
    }

    public void selectRole(String roleValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement dropdown = wait.until(
            ExpectedConditions.elementToBeClickable(roleSelect)
        );

        Select select = new Select(dropdown);
        select.selectByValue(roleValue);

        ((JavascriptExecutor) driver)
            .executeScript("arguments[0].dispatchEvent(new Event('change'))", dropdown);
    }

    public void selectStatus(String statusValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement dropdown = wait.until(
            ExpectedConditions.elementToBeClickable(statusSelect)
        );

        Select select = new Select(dropdown);
        select.selectByValue(statusValue);

        ((JavascriptExecutor) driver)
            .executeScript("arguments[0].dispatchEvent(new Event('change'))", dropdown);
    }

    public void clickSave() {
        driver.findElement(saveBtn).click();
    }

    public void clickCancel() {
        driver.findElement(cancelBtn).click();
    }

    /* ===== VALIDATION ===== */
    public boolean isRequiredErrorDisplayed() {
        return driver.findElements(requiredError).size() > 0;
    }

    public boolean isEmailPatternErrorDisplayed() {
        return driver.findElements(emailPatternError).size() > 0;
    }

    public boolean isPhonePatternErrorDisplayed() {
        return driver.findElements(phonePatternError).size() > 0;
    }

    public boolean isPasswordPatternErrorDisplayed() {
        return driver.findElements(passwordPatternError).size() > 0;
    }

    public boolean isModalDisplayed() {
        return driver.findElements(modal).size() > 0;
    }
    

}
