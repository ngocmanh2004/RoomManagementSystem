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

public class US11 {
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Thêm phòng mới')]")));
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
    public void TC111() {
        System.out.println("TC1.1.1 - Thêm phòng trọ thành công");
        
        driver.findElement(By.xpath("//button[contains(text(),'Thêm phòng mới')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='building']")));
        
        driver.findElement(By.xpath("//select[@id='building']")).click();
        driver.findElement(By.xpath("//option[@value='2']")).click();
        driver.findElement(By.xpath("//input[@id='name']")).sendKeys("P101");
        driver.findElement(By.xpath("//input[@id='area']")).sendKeys("25");
        driver.findElement(By.xpath("//input[@id='price']")).sendKeys("3000000");
        driver.findElement(By.xpath("//select[@id='status']")).click();
        driver.findElement(By.xpath("//option[contains(text(),'Trống')]")).click();
        driver.findElement(By.xpath("//textarea[@id='description']")).sendKeys("Phòng đẹp");
        driver.findElement(By.xpath("//button[normalize-space()='Thêm phòng']")).click();
        
        try { Thread.sleep(3000); } catch (Exception e) {}
        
        boolean success = checkElement("//*[contains(text(),'thành công')]") || 
                         checkElement("//td[contains(text(),'P101')]");
        Assert.assertTrue(success, "Thêm phòng không thành công");
        System.out.println("✓ Thêm phòng thành công");
    }

    @Test(priority = 2)
    public void TC112() {
        System.out.println("TC1.1.2 - Thêm phòng với các trạng thái khác nhau");
        
        driver.findElement(By.xpath("//button[contains(text(),'Thêm phòng mới')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='building']")));
        
        driver.findElement(By.xpath("//select[@id='building']")).click();
        driver.findElement(By.xpath("//option[@value='2']")).click();
        driver.findElement(By.xpath("//input[@id='name']")).sendKeys("P1010");
        driver.findElement(By.xpath("//input[@id='area']")).sendKeys("25");
        driver.findElement(By.xpath("//input[@id='price']")).sendKeys("3000000");
        driver.findElement(By.xpath("//select[@id='status']")).click();
        driver.findElement(By.xpath("//option[contains(text(),'Đã thuê')]")).click();
        driver.findElement(By.xpath("//textarea[@id='description']")).sendKeys("Phòng đẹp");
        driver.findElement(By.xpath("//button[normalize-space()='Thêm phòng']")).click();
        
        try { Thread.sleep(3000); } catch (Exception e) {}
        
        boolean success = checkElement("//*[contains(text(),'thành công')]") || 
                         checkElement("//td[contains(text(),'P101')]");
        Assert.assertTrue(success, "Thêm phòng không thành công");
        System.out.println("✓ Thêm phòng thành công");
        
        try { Thread.sleep(3000); } catch (Exception e) {}
        
        driver.findElement(By.xpath("//button[contains(text(),'Thêm phòng mới')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='building']")));
        
        driver.findElement(By.xpath("//select[@id='building']")).click();
        driver.findElement(By.xpath("//option[@value='2']")).click();
        driver.findElement(By.xpath("//input[@id='name']")).sendKeys("P1010");
        driver.findElement(By.xpath("//input[@id='area']")).sendKeys("25");
        driver.findElement(By.xpath("//input[@id='price']")).sendKeys("3000000");
        driver.findElement(By.xpath("//select[@id='status']")).click();
        driver.findElement(By.xpath("//select[@id='status']//option[@value='REPAIRING'][contains(text(),'Đang sửa chữa')]")).click();
        driver.findElement(By.xpath("//textarea[@id='description']")).sendKeys("Phòng đẹp");
        driver.findElement(By.xpath("//button[normalize-space()='Thêm phòng']")).click();
        
        try { Thread.sleep(3000); } catch (Exception e) {}
        
        boolean success1 = checkElement("//*[contains(text(),'thành công')]") || 
                         checkElement("//td[contains(text(),'P101')]");
        Assert.assertTrue(success1, "Thêm phòng không thành công");
        System.out.println("✓ Thêm phòng thành công");
    }

    @Test(priority = 3)
    public void TC113() {
        System.out.println("TC1.1.3 - Thiếu thông tin bắt buộc");
        
        driver.findElement(By.xpath("//button[contains(text(),'Thêm phòng mới')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='building']")));
        
        // Chỉ nhập một số trường, bỏ trống tên phòng (trường bắt buộc)
        driver.findElement(By.xpath("//select[@id='building']")).click();
        driver.findElement(By.xpath("//option[@value='2']")).click();
        // Bỏ trống tên phòng
        driver.findElement(By.xpath("//input[@id='area']")).sendKeys("25");
        driver.findElement(By.xpath("//input[@id='price']")).sendKeys("3000000");
        driver.findElement(By.xpath("//button[normalize-space()='Thêm phòng']")).click();
        
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        // VERIFY QUAN TRỌNG: Form vẫn phải mở (vì thêm phòng thất bại)
        boolean formStillOpen = checkElement("//select[@id='building']");
        Assert.assertTrue(formStillOpen, "Form đã đóng - không đúng với kết quả mong đợi (thêm phòng phải thất bại)");
        System.out.println("✓ Form vẫn mở - thêm phòng thất bại như mong đợi");
        
        // Đóng form
        driver.findElement(By.xpath("//i[@class='fa-solid fa-xmark']")).click();
    }

    @Test(priority = 4)
    public void TC114() {
        System.out.println("TC1.1.4 - Không nhập thông tin");
        
        driver.findElement(By.xpath("//button[contains(text(),'Thêm phòng mới')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='building']")));
        
        // Không nhập bất kỳ thông tin nào
        driver.findElement(By.xpath("//button[normalize-space()='Thêm phòng']")).click();
        
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        // VERIFY QUAN TRỌNG: Form vẫn phải mở
        boolean formStillOpen = checkElement("//select[@id='building']");
        Assert.assertTrue(formStillOpen, "Form đã đóng - không đúng với kết quả mong đợi");
        System.out.println("✓ Form vẫn mở - thêm phòng thất bại như mong đợi");
        
        // Đóng form
        driver.findElement(By.xpath("//i[@class='fa-solid fa-xmark']")).click();
    }

    @Test(priority = 5)
    public void TC115() {
        System.out.println("TC1.1.5 - Sai định dạng diện tích/giá");
        
        driver.findElement(By.xpath("//button[contains(text(),'Thêm phòng mới')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='building']")));
        
        driver.findElement(By.xpath("//select[@id='building']")).click();
        driver.findElement(By.xpath("//option[@value='2']")).click();
        driver.findElement(By.xpath("//input[@id='name']")).sendKeys("P105");
        // Nhập chữ vào trường số
        driver.findElement(By.xpath("//input[@id='area']")).sendKeys("Hai mươi lăm");
        driver.findElement(By.xpath("//input[@id='price']")).sendKeys("Ba triệu");
        driver.findElement(By.xpath("//button[normalize-space()='Thêm phòng']")).click();
        
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        // VERIFY QUAN TRỌNG: Form vẫn phải mở
        boolean formStillOpen = checkElement("//select[@id='building']");
        Assert.assertTrue(formStillOpen, "Form đã đóng - không đúng với kết quả mong đợi");
        System.out.println("✓ Form vẫn mở - thêm phòng thất bại như mong đợi");
        
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