package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;
import pages.TenantPage; 

import java.time.Duration;

public class HomePage {
    WebDriver driver;
    private WebDriverWait wait;
    
    By roomCards = By.cssSelector(".room-card");
    By firstRoomCard = By.xpath("(//a[contains(text(),'Xem chi tiết')])[1]");
    By sortDropdown = By.xpath("(//select[@ng-reflect-model='Mặc định'])[1]");

    By userBtn = By.cssSelector(".user-btn");
    By adminDashboardBtn = By.xpath("//a[normalize-space()='Dashboard Admin']");
    By tenantMenuBtn = By.xpath("//a[@routerlink='/admin/tenants']");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickFirstRoom() {
        driver.findElement(firstRoomCard).click();
    }

    public void sortBy(String option) {
        driver.findElement(sortDropdown).sendKeys(option);
    }

    public List<Integer> getRoomPrices() {
        List<WebElement> rooms = driver.findElements(roomCards);
        List<Integer> prices = new ArrayList<>();
        for (WebElement room : rooms) {
            WebElement priceElem = room.findElement(By.className("price"));
            String text = priceElem.getText();
            String priceStr = text.replaceAll("[^0-9]", "");
            if (!priceStr.isEmpty()) {
                prices.add(Integer.parseInt(priceStr));
            }
        }
        return prices;
    }

    public void printRoomList() {
        List<WebElement> rooms = driver.findElements(roomCards);
        System.out.println("Danh sách phòng hiện tại:");
        int index = 1;
        for (WebElement room : rooms) {
            String name = room.findElement(By.className("title")).getText(); 
            String price = room.findElement(By.className("price")).getText();
            System.out.println(index + ". " + name + " - " + price);
            index++;
        }
    }
    
    public RoomsPage goToRoomsPage() {
        driver.findElement(By.xpath("//a[@routerlink='/rooms'][contains(text(),'Phòng trọ')]")).click();
        return new RoomsPage(driver);
    }

    public TenantPage goToTenantPage() {
        wait.until(ExpectedConditions.elementToBeClickable(userBtn)).click();
        wait.until(ExpectedConditions.elementToBeClickable(adminDashboardBtn)).click();
        wait.until(ExpectedConditions.elementToBeClickable(tenantMenuBtn)).click();

        return new TenantPage(driver);
    }

}
