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

public class US14 {
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
    public void TC141() {
        System.out.println("TC1.4.1 - Cập nhật trạng thái phòng thành công");
        
        // Click nút chỉnh sửa (giống US12)
        driver.findElement(By.xpath("//tbody/tr[8]/td[7]/button[1]/i[1]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='status']")));
        
        // Chỉ thay đổi trạng thái
        Select statusSelect = new Select(driver.findElement(By.xpath("//select[@id='status']")));
        statusSelect.selectByVisibleText("Đã thuê");
        
        // Nhấn Lưu
        driver.findElement(By.xpath("//button[contains(text(),'Lưu')]")).click();
        
        try { Thread.sleep(3000); } catch (Exception e) {}
        
    }

    @Test(priority = 2)
    public void TC142() {
        System.out.println("TC1.4.2 - Cập nhật nhiều lần liên tiếp");
        
        String[] statuses = {"Đã thuê", "Đang sửa chữa", "Trống"};
        
        for (int i = 0; i < statuses.length; i++) {
            // Click chỉnh sửa
            driver.findElement(By.xpath("//tbody/tr[8]/td[7]/button[1]/i[1]")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='status']")));
            
            // Chỉ thay đổi trạng thái
            Select statusSelect = new Select(driver.findElement(By.xpath("//select[@id='status']")));
            statusSelect.selectByVisibleText(statuses[i]);
            
            // Nhấn Lưu
            driver.findElement(By.xpath("//button[contains(text(),'Lưu')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
        }
    }

    @Test(priority = 3)
    public void TC143() {
        System.out.println("TC1.4.3 - Không thay đổi trạng thái");
        
        // Click chỉnh sửa
        driver.findElement(By.xpath("//tbody/tr[8]/td[7]/button[1]/i[1]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='status']")));
        
        // Không thay đổi gì, nhấn Lưu luôn
        driver.findElement(By.xpath("//button[contains(text(),'Lưu')]")).click();
        
        try { Thread.sleep(2000); } catch (Exception e) {}
    }
}