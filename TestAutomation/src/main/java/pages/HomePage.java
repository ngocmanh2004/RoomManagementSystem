package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HomePage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ==============================
    // LOCATORS WITH PAGE FACTORY
    // ==============================

    @FindBy(css = ".room-card")
    private List<WebElement> roomCards;

    @FindBy(xpath = "(//a[contains(text(),'Xem chi tiết')])[1]")
    private WebElement firstRoomCard;

    @FindBy(xpath = "(//select[@ng-reflect-model='Mặc định'])[1]")
    private WebElement sortDropdown;

    // User + Admin navigation
    @FindBy(css = ".user-btn")
    private WebElement userBtn;

    @FindBy(xpath = "//a[normalize-space()='Dashboard Admin']")
    private WebElement adminDashboardBtn;

    @FindBy(xpath = "//a[@routerlink='/admin/tenants']")
    private WebElement tenantMenuBtn;

    @FindBy(xpath = "//a[@routerlink='/rooms'][contains(text(),'Phòng trọ')]")
    private WebElement roomsMenuBtn;

    // ==============================
    // CONSTRUCTOR
    // ==============================

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);  
    }

    // ==============================
    // ACTIONS
    // ==============================

    public RoomDetailPage clickFirstRoom() {
        wait.until(ExpectedConditions.elementToBeClickable(firstRoomCard)).click();
        return new RoomDetailPage(driver);
    }
    
    public boolean isAtRoomDetailPage() {
        // Kiểm tra phần tử tiêu đề phòng có hiển thị hay không
        return driver.findElements(roomName).size() > 0;
    }

    public void sortBy(String option) {
        sortDropdown.sendKeys(option);
    }

    public List<Integer> getRoomPrices() {
        List<Integer> prices = new ArrayList<>();

        for (WebElement room : roomCards) {
            WebElement priceElem = room.findElement(org.openqa.selenium.By.className("price"));
            String text = priceElem.getText();
            String numberOnly = text.replaceAll("[^0-9]", "");
            if (!numberOnly.isEmpty()) {
                prices.add(Integer.parseInt(numberOnly));
            }
        }
        return prices;
    }

    public void printRoomList() {
        System.out.println("Danh sách phòng hiện tại:");
        int i = 1;
        for (WebElement room : roomCards) {
            String title = room.findElement(org.openqa.selenium.By.className("title")).getText();
            String price = room.findElement(org.openqa.selenium.By.className("price")).getText();
            System.out.println(i + ". " + title + " - " + price);
            i++;
        }
    }

    // ==============================
    // NAVIGATION
    // ==============================

    public RoomsPage goToRoomsPage() {
        wait.until(ExpectedConditions.elementToBeClickable(roomsMenuBtn)).click();
        return new RoomsPage(driver);
    }

    public TenantPage goToTenantPage() {
        wait.until(ExpectedConditions.elementToBeClickable(userBtn)).click();
        wait.until(ExpectedConditions.elementToBeClickable(adminDashboardBtn)).click();
        wait.until(ExpectedConditions.elementToBeClickable(tenantMenuBtn)).click();
        return new TenantPage(driver);
    }

}
