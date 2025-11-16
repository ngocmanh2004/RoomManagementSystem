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
import org.testng.Assert;

import java.time.Duration;

public class US12 {
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
    public void TC121() {
        System.out.println("TC1.2.1 - Chỉnh sửa thông tin thành công");
        
        // Tìm và click nút chỉnh sửa cho phòng P101
        driver.findElement(By.xpath("//tbody/tr[8]/td[7]/button[1]/i[1]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='price']")));
        
        // Thay đổi giá thuê
        driver.findElement(By.xpath("//input[@id='price']")).clear();
        driver.findElement(By.xpath("//input[@id='price']")).sendKeys("3500000");
        
        // Nhấn nút Lưu
        driver.findElement(By.xpath("//button[contains(text(),'Lưu')]")).click();
        
        try { Thread.sleep(3000); } catch (Exception e) {}
        
    }

    @Test(priority = 2)
    public void TC122() {
        System.out.println("TC1.2.2 - Chỉnh sửa không thành công - bỏ trống dữ liệu bắt buộc");
        
        // Tìm và click nút chỉnh sửa
        driver.findElement(By.xpath("//tbody/tr[8]/td[7]/button[1]/i[1]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='area']")));
        
        // Xóa dữ liệu trường tên phòng
        driver.findElement(By.xpath("//input[@id='area']")).clear();
        
      
     // Đóng form
        driver.findElement(By.xpath("//i[@class='fa-solid fa-xmark']")).click();
        
    }

    @Test(priority = 3)
    public void TC123() {
        System.out.println("TC1.2.3 - Chỉnh sửa không thành công - Nhập sai định dạng giá/diện tích");
        
        // Tìm và click nút chỉnh sửa
        driver.findElement(By.xpath("//tbody/tr[8]/td[7]/button[1]/i[1]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='price']")));
        
        // Nhập ký tự chữ vào trường giá
        driver.findElement(By.xpath("//input[@id='price']")).clear();
        driver.findElement(By.xpath("//input[@id='price']")).sendKeys("abc");
        
        
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        
        // Đóng form
        driver.findElement(By.xpath("//i[@class='fa-solid fa-xmark']")).click();
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