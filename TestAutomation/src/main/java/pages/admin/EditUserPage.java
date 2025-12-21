package pages.admin;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

public class EditUserPage {

    private WebDriver driver;

    public EditUserPage(WebDriver driver) {
        this.driver = driver;
    }

    // -------------------------
    // Locators
    // -------------------------
    private By inputFullName = By.cssSelector("input[name='fullName']");
    private By inputEmail = By.cssSelector("input[name='email']");
    private By selectRole = By.cssSelector("select[name='role']");
    private By selectStatus = By.cssSelector("select[name='status']");
    private By inputPassword = By.cssSelector("input[name='password']");
    private By btnSave = By.cssSelector("button.btn-save");
    private By btnCancel = By.cssSelector("button.btn-cancel");
    private By errorMessage = By.cssSelector(".error-message");
    private By successMessage = By.cssSelector(".toast-message");

    // -------------------------
    // Actions
    // -------------------------
    /**
     * Gộp tất cả field trong form để fill cùng lúc
     * Nếu muốn giữ nguyên field, truyền null
     */
    public void fillForm(String fullName, String email, String role, String status, String password) {
        if (fullName != null) setFullName(fullName);
        if (email != null) setEmail(email);
        if (role != null) setRole(role);
        if (status != null) setStatus(status);
        if (password != null) setPassword(password);
    }

    private void setFullName(String name) {
        WebElement e = driver.findElement(inputFullName);
        e.clear();
        e.sendKeys(name);
    }

    private void setEmail(String email) {
        WebElement e = driver.findElement(inputEmail);
        e.clear();
        e.sendKeys(email);
    }

    private void setRole(String role) {
        Select select = new Select(driver.findElement(selectRole));
        select.selectByVisibleText(role);
    }

    private void setStatus(String status) {
        Select select = new Select(driver.findElement(selectStatus));
        select.selectByVisibleText(status);
    }

    private void setPassword(String password) {
        WebElement e = driver.findElement(inputPassword);
        e.clear();
        e.sendKeys(password);
    }

    public void save() {
        driver.findElement(btnSave).click();
        sleep(700);
    }

    public void cancel() {
        driver.findElement(btnCancel).click();
        sleep(500);
    }

    public String getErrorMessage() {
        try {
            return driver.findElement(errorMessage).getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public String getSuccessMessage() {
        try {
            return driver.findElement(successMessage).getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public boolean isFormDisplayed() {
        return driver.findElements(inputFullName).size() > 0;
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
