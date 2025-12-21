package pages.admin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AddUserPage {
    private WebDriver driver;

    public AddUserPage(WebDriver driver) {
        this.driver = driver;
    }

    private By inputUsername = By.id("username");
    private By inputEmail = By.id("email");
    private By inputPassword = By.id("password");
    private By selectRole = By.id("role");
    private By btnSave = By.cssSelector("button.btn-save");
    private By btnCancel = By.cssSelector("button.btn-cancel");
    private By alertError = By.cssSelector(".alert-danger");
    private By alertSuccess = By.cssSelector(".alert-success");

    public void fillForm(String username, String email, String password, String role) {
        driver.findElement(inputUsername).clear();
        driver.findElement(inputUsername).sendKeys(username);

        driver.findElement(inputEmail).clear();
        driver.findElement(inputEmail).sendKeys(email);

        driver.findElement(inputPassword).clear();
        driver.findElement(inputPassword).sendKeys(password);

        driver.findElement(selectRole).click();
        driver.findElement(By.xpath("//option[text()='" + role + "']")).click();
    }

    public void submit() {
        driver.findElement(btnSave).click();
    }

    public void cancel() {
        driver.findElement(btnCancel).click();
    }

    public String getErrorMessage() {
        return driver.findElement(alertError).getText();
    }

    public String getSuccessMessage() {
        return driver.findElement(alertSuccess).getText();
    }
    
    // -------------------------
    // Method kiểm tra visibility form
    // -------------------------
    public boolean isFormDisplayed() {
        return !driver.findElements(inputUsername).isEmpty() &&
               !driver.findElements(inputEmail).isEmpty() &&
               !driver.findElements(inputPassword).isEmpty();
    }

    public By getInputUsername() { return inputUsername; }
    public By getInputEmail() { return inputEmail; }
    public By getInputPassword() { return inputPassword; }
}
