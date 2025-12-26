package Epic13;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.time.Duration;

public class US131 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TRÌNH DUYỆT ===");
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        
        // Đăng nhập admin
        driver.get("http://localhost:4200/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("admin123");
        driver.findElement(By.id("password")).sendKeys("123123");
        driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();
        
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            System.out.println("Không có alert");
        }
        
        // Điều hướng đến quản lý yêu cầu đăng ký chủ trọ
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'user-btn')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Dashboard Admin')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Quản lý Kiểm duyệt')]"))).click();
        
        try { Thread.sleep(3000); } catch (Exception e) {}
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
    public void TC1311() {
        System.out.println("TC13.1.1 - Hiển thị danh sách yêu cầu");
        
        try {
            // Danh sách các trường cần kiểm tra
            String[] requiredFields = {
                "Họ tên", "Email", "CCCD", "Địa chỉ", 
                "Giấy phép KD", "Ngày gửi", "Trạng thái", "Hành động"
            };
            
            boolean allFieldsPresent = true;
            
            // Kiểm tra từng trường
            for (String field : requiredFields) {
                boolean fieldExists = checkElement("//th[contains(text(),'" + field + "')]");
                if (fieldExists) {
                    System.out.println("✓ Có trường: " + field);
                } else {
                    System.out.println("✗ Thiếu trường: " + field);
                    allFieldsPresent = false;
                }
            }
            
            // Assert tất cả trường đều có
            Assert.assertTrue(allFieldsPresent, "Thiếu một hoặc nhiều trường bắt buộc");
            
            System.out.println("✓ PASS - Tất cả các trường đều hiển thị");
            
        } catch (Exception e) {
            System.out.println("✗ TC1311 failed: " + e.getMessage());
            Assert.fail("TC1311 failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void TC_UI_1311() {
        System.out.println("TC.UI13.1.1 - Kiểm tra giao diện danh sách yêu cầu");
        
        try {
            // Verify layout không bị vỡ
            boolean tableVisible = checkElement("//table");
            boolean noLayoutError = !checkElement("//*[contains(@style,'overflow')]") &&
                                  !checkElement("//*[contains(@class,'error')]");
            
            Assert.assertTrue(tableVisible, "Bảng không hiển thị");
            Assert.assertTrue(noLayoutError, "Layout bị lỗi");
            
            System.out.println("✓ Giao diện danh sách yêu cầu hiển thị tốt");
            
        } catch (Exception e) {
            System.out.println("✗ TC.UI13.1.1 failed: " + e.getMessage());
            Assert.fail("TC.UI13.1.1 failed: " + e.getMessage());
        }
    }
    
    private boolean checkElement(String xpath) {
        try {
            return driver.findElement(By.xpath(xpath)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}