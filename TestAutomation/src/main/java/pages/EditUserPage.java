package pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class EditUserPage {

    WebDriver driver;

    private By editForm = By.cssSelector(".modal-content");

    private By fullNameInput = By.cssSelector("input[formControlName='fullName']");
    private By emailInput = By.cssSelector("input[formControlName='email']");
    private By phoneInput = By.cssSelector("input[formControlName='phone']");
    private By passwordInput = By.cssSelector("input[formControlName='password']");
    private By roleSelect = By.cssSelector("select[formControlName='role']");
    private By statusSelect = By.cssSelector("select[formControlName='status']");

    private By saveBtn = By.xpath("//button[contains(text(),'Lưu thay đổi')]");
    private By cancelBtn = By.cssSelector(".btn.btn-secondary");
    private By errorMessage = By.cssSelector(".error-text");

	private WebDriverWait wait;

    public EditUserPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public boolean isEditFormDisplayed() {
        return driver.findElements(editForm).size() > 0;
    }

    public boolean isPasswordDisplayed() {
        return driver.findElements(passwordInput).size() > 0;
    }

    public void clearFullName() {
        driver.findElement(fullNameInput).clear();
    }

    public void setFullName(String name) {
        WebElement el = driver.findElement(fullNameInput);
        el.clear();
        el.sendKeys(name);
    }

    public void setEmail(String email) {
        WebElement el = driver.findElement(emailInput);
        el.clear();
        el.sendKeys(email);
    }

    public void setPhone(String phone) {
        WebElement el = driver.findElement(phoneInput);
        el.clear();
        el.sendKeys(phone);
    }

    public void selectRole(String role) {
        driver.findElement(roleSelect).sendKeys(role);
    }

    public void selectStatus(String status) {
        driver.findElement(statusSelect).sendKeys(status);
    }

    public String clickSave() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement saveBtn = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Lưu thay đổi')]")
            )
        );
        saveBtn.click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();

        return text;
    }

    public void clickCancel() {
        waitUntilClickable(cancelBtn).click();
    }

    private WebElement waitUntilClickable(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(editForm));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean isErrorDisplayed() {
        List<WebElement> errors = driver.findElements(errorMessage);
        return errors.size() > 0 && errors.get(0).isDisplayed();
    }

   

}
