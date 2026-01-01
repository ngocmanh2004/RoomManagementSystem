package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RoomsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By provinceFilter = By.name("province");
    private By districtFilter = By.name("district");
    private By priceFilter = By.name("price");
    private By acreageFilter = By.name("acreage");

    private By filterButton = By.cssSelector("button[type='submit']");
    private By clearButton = By.xpath("//button[contains(text(),'Làm mới')]");
    private By roomCard = By.cssSelector(".room-grid app-room-card");

    public RoomsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void filterByProvince(String province) {
        Select select = new Select(driver.findElement(provinceFilter));
        select.selectByVisibleText(province);
        wait.until(ExpectedConditions.elementToBeClickable(districtFilter));
    }

    public void filterByDistrict(String district) {
        Select select = new Select(driver.findElement(districtFilter));
        select.selectByVisibleText(district);
    }

    public void filterByPrice(String price) {
        Select select = new Select(driver.findElement(priceFilter));
        select.selectByVisibleText(price);
    }

    public void filterByAcreage(String acreage) {
        Select select = new Select(driver.findElement(acreageFilter));
        select.selectByVisibleText(acreage);
    }

    public void applyFilter() {
        driver.findElement(filterButton).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(roomCard));
    }

    public void clearFilters() {
        driver.findElement(clearButton).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(roomCard));
    }

    public boolean isRoomListDisplayed() {
        return driver.findElements(roomCard).size() > 0;
    }
}
