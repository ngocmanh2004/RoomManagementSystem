package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HeaderComponent {

    private WebDriver driver;

    private By userButton = By.xpath("//button[@class='user-btn']");
    private By firstDropdownItem = By.xpath("//a[normalize-space()='Dashboard Admin']");
    private By userManagementLink = By.xpath("//a[@routerlink='/admin/users']");

    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
    }


    public void openUserMenu() {
        driver.findElement(userButton).click();
    }

    public void clickFirstDropdown() {
        driver.findElement(firstDropdownItem).click();
    }

    public void goToUserManagement() {
        driver.findElement(userManagementLink).click();
    }

    public void openUserManagementPage() {
        openUserMenu();
        clickFirstDropdown();
        goToUserManagement();
    }
}
