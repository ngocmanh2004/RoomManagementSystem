package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class TenantPage {
    WebDriver driver;

    By addButton = By.cssSelector(".btn-add");
    By tenantTableRows = By.cssSelector(".tenant-table tbody tr");

    By fullNameInput = By.name("userFullName");
    By phoneInput = By.name("userPhone");
    By emailInput = By.name("userEmail");
    By cccdInput = By.name("cccd");
    By dateOfBirthInput = By.name("dateOfBirth");
    By addressInput = By.name("address");
    
    public By viewModalName = By.cssSelector(".view-mode .tenant-name");
    By viewModalPhone = By.xpath("//div[@class='info-list']/p[1]/span");
    By viewModalEmail = By.xpath("//div[@class='info-list']/p[2]/span");
    By viewModalCCCD = By.xpath("//div[@class='info-list']/p[3]/span");
    By viewModalDOB = By.xpath("//div[@class='info-list']/p[4]/span");
    By viewModalAddress = By.xpath("//div[@class='info-list']/p[5]/span");

    By submitButton = By.cssSelector("form button[type='submit']");
    By cancelButton = By.cssSelector(".modal button.cancel");

    public TenantPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickAddTenant() {
        driver.findElement(addButton).click();
    }

    public void fillTenantForm(String name, String phone, String email, String cccd, String dob, String address) {
        driver.findElement(fullNameInput).clear();
        driver.findElement(fullNameInput).sendKeys(name);

        driver.findElement(phoneInput).clear();
        driver.findElement(phoneInput).sendKeys(phone);

        driver.findElement(emailInput).clear();
        driver.findElement(emailInput).sendKeys(email);

        driver.findElement(cccdInput).clear();
        driver.findElement(cccdInput).sendKeys(cccd);

        driver.findElement(dateOfBirthInput).clear();
        driver.findElement(dateOfBirthInput).sendKeys(dob);

        driver.findElement(addressInput).clear();
        driver.findElement(addressInput).sendKeys(address);
    }

    public void submitForm() {
        driver.findElement(submitButton).click();
    }

    public void cancelForm() {
        driver.findElement(cancelButton).click();
    }

    public int getTenantCount() {
        List<WebElement> rows = driver.findElements(tenantTableRows);
        return rows.size();
    }

    public void clickViewTenant(int index) {
        driver.findElements(By.cssSelector(".icon.view")).get(index).click();
    }

    public void clickEditTenant(int index) {
        driver.findElements(By.cssSelector(".icon.edit")).get(index).click();
    }

    public void clickDeleteTenant(int index) {
        driver.findElements(By.cssSelector(".icon.delete")).get(index).click();
        driver.switchTo().alert().accept();
    }

    public String getTenantName(int index) {
        return driver.findElements(By.cssSelector(".tenant-info .name")).get(index).getText();
    }

    public String getTenantPhone(int index) {
        return driver.findElements(By.cssSelector("td:nth-child(2) div")).get(index).getText();
    }

    public String getTenantCCCD(int index) {
        return driver.findElements(By.cssSelector("td:nth-child(3)")).get(index).getText();
    }

    public String getTenantDateOfBirth(int index) {
        return driver.findElements(By.cssSelector("td:nth-child(4)")).get(index).getText();
    }
    
    public String getTenantStatus(int index) {
        WebElement selectElement = driver.findElements(
                By.cssSelector("table.tenant-table tbody tr td:nth-child(5) select")
        ).get(index);

        Select dropdown = new Select(selectElement);
        return dropdown.getFirstSelectedOption().getAttribute("value");  
        // Trả về ACTIVE hoặc PENDING
    }

    public String getModalName() {
        return driver.findElement(viewModalName).getText();
    }

    public String getModalPhone() {
        return driver.findElement(viewModalPhone).getText();
    }

    public String getModalCCCD() {
        return driver.findElement(viewModalCCCD).getText();
    }

}
