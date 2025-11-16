package Epic1;

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

public class US13 {
    WebDriver driver;
    WebDriverWait wait;
 
    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TRÌNH DUYỆT ===");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        
        // Đăng nhập
        driver.get("http://localhost:4200/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("huydethuong111");
        driver.findElement(By.id("password")).sendKeys("Ndth02042004@");
        driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();
        
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            System.out.println("Không có alert");
        }
        
        // Điều hướng đến quản lý phòng
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='user-btn']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='Dashboard Admin']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@routerlink='/admin/rooms']"))).click();
        
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
    public void TC131() {
        System.out.println("TC1.3.1 - Xóa phòng trống thành công");
        
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        // Click nút xóa phòng đầu tiên trong danh sách phòng trống
        driver.findElement(By.xpath("//tbody/tr[1]/td[7]/button[2]/i[1]")).click();
        
        // Chọn Đồng ý trong alert
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Xác nhận xóa')]"))).click();
        
        try { Thread.sleep(3000); } catch (Exception e) {}
        
   
    }

    
    @Test(priority = 2)
    public void TC133() {
        System.out.println("TC1.3.3 - Xóa phòng có khách thuê");
        
        driver.findElement(By.xpath("//select[@class='filter-select']")).click();
        driver.findElement(By.xpath("//option[@value='OCCUPIED']")).click();
        
        
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        // Click nút xóa phòng đầu tiên trong danh sách phòng đã thuê
        driver.findElement(By.xpath("//tbody/tr[1]/td[7]/button[2]/i[1]")).click();
        
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            System.out.println("Không có alert");
        }
    }

    // Helper method để kiểm tra element có tồn tại không
    private boolean checkElement(String xpath) {
        try {
            return driver.findElement(By.xpath(xpath)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}