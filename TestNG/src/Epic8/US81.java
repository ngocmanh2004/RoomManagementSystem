package Epic8;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import java.time.Duration;

public class US81 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
    	System.out.println("=== KHỞI TẠO TRÌNH DUYỆT ===");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        
        // Truy cập trang chủ (không cần đăng nhập)
        driver.get("http://localhost:4200");
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    @AfterClass
    public void afterClass() {
        System.out.println("=== ĐÓNG TRÌNH DUYỆT ===");
        if (driver != null) {
            driver.quit();
        }
    }
    
    @BeforeMethod
    public void beforeMethod() {
        System.out.println("--- Bắt đầu test case ---");
    }

    @AfterMethod
    public void afterMethod() {
        System.out.println("--- Kết thúc test case ---");
    }

    @Test(priority = 1)
    public void TC811() {
        System.out.println("TC8.1.1 - Xem chi tiết phòng đang thuê hợp lệ");
        
        driver.findElement(By.xpath("//body[1]/app-root[1]/app-public-layout[1]/main[1]/app-home[1]/section[3]/div[1]/div[2]/app-room-card[1]/div[1]/div[2]/div[2]/div[1]/a[1]")).click();
    }

 
    
}