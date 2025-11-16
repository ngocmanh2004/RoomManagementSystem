package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RoomDetailPage {
    WebDriver driver;

    By roomName = By.cssSelector(".header-section h1");
    By roomPrice = By.cssSelector(".info-bar div:nth-child(1) strong");
    By roomArea = By.xpath("//p[@class='address']");

    public RoomDetailPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getRoomName() {
        return driver.findElement(roomName).getText();
    }

    public String getRoomPrice() {
        return driver.findElement(roomPrice).getText();
    }

    public String getRoomArea() {
        return driver.findElement(roomArea).getText();
    }

    public void printRoomDetail() {
        System.out.println("=== Thông tin chi tiết phòng ===");
        System.out.println("Tên phòng: " + getRoomName());
        System.out.println("Giá thuê: " + getRoomPrice());
        System.out.println("Diện tích: " + getRoomArea());
    }
}
