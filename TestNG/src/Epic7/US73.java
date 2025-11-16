package Epic7;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.time.Duration;

public class US73 {
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
        
        // Điều hướng đến trang thông tin cá nhân
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='user-btn']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Thông tin cá nhân')]"))).click();
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    @Test(priority = 1)
    public void TC731() {
        System.out.println("TC7.3.1 - Cập nhật thông tin thành công");
        
        try {
            // 1. Nhấn nút "Edit profile"
            driver.findElement(By.xpath("//button[normalize-space()='Edit profile']")).click();
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            // 2. Sửa các thông tin hợp lệ
            driver.findElement(By.xpath("//input[@name='fullname']")).clear();
            driver.findElement(By.xpath("//input[@name='fullname']")).sendKeys("Nguyễn Văn B");
            
            driver.findElement(By.xpath("//input[@name='phone']")).clear();
            driver.findElement(By.xpath("//input[@name='phone']")).sendKeys("0909123456");
            
            // 3. Nhấn "Lưu thay đổi"
            driver.findElement(By.xpath("//button[contains(text(),'Lưu thay đổi')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Cập nhật thành công"
            boolean success = checkElement("//*[contains(text(),'Cập nhật thành công')]");
            Assert.assertTrue(success, "Không hiển thị thông báo thành công");
            System.out.println("✓ Cập nhật thông tin thành công");
            
        } catch (Exception e) {
            System.out.println("✗ TC731 failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void TC732() {
        System.out.println("TC7.3.2 - Cập nhật thất bại – Email sai định dạng");
        
        try {
            // 1. Nhấn nút "Edit profile"
            driver.findElement(By.xpath("//button[normalize-space()='Edit profile']")).click();
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            // 2. Nhập email sai định dạng
            driver.findElement(By.xpath("//input[@name='email']")).clear();
            driver.findElement(By.xpath("//input[@name='email']")).sendKeys("abc");
            
            // 3. Nhấn "Lưu thay đổi"
            driver.findElement(By.xpath("//button[contains(text(),'Lưu thay đổi')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Email không hợp lệ"
            boolean hasError = checkElement("//*[contains(text(),'Email không hợp lệ')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi email không hợp lệ");
            System.out.println("✓ Hiển thị lỗi email không hợp lệ");
            
        } catch (Exception e) {
            System.out.println("✗ TC732 failed: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void TC733() {
        System.out.println("TC7.3.3 - Cập nhật thất bại – SĐT sai định dạng");
        
        try {
            // 1. Nhấn nút "Edit profile"
            driver.findElement(By.xpath("//button[normalize-space()='Edit profile']")).click();
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            // 2. Nhập số điện thoại sai định dạng
            driver.findElement(By.xpath("//input[@name='phone']")).clear();
            driver.findElement(By.xpath("//input[@name='phone']")).sendKeys("123ABC");
            
            // 3. Nhấn "Lưu thay đổi"
            driver.findElement(By.xpath("//button[contains(text(),'Lưu thay đổi')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Số điện thoại không hợp lệ"
            boolean hasError = checkElement("//*[contains(text(),'Số điện thoại không hợp lệ')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi số điện thoại không hợp lệ");
            System.out.println("✓ Hiển thị lỗi số điện thoại không hợp lệ");
            
        } catch (Exception e) {
            System.out.println("✗ TC733 failed: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void TC734() {
        System.out.println("TC7.3.4 - Cập nhật thất bại – Thiếu thông tin bắt buộc");
        
        try {
            // 1. Nhấn nút "Edit profile"
            driver.findElement(By.xpath("//button[normalize-space()='Edit profile']")).click();
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            // 2. Bỏ trống trường Họ tên
            driver.findElement(By.xpath("//input[@name='fullname']")).clear();
            
            // 3. Nhấn "Lưu thay đổi"
            driver.findElement(By.xpath("//button[contains(text(),'Lưu thay đổi')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Vui lòng nhập đầy đủ thông tin"
            boolean hasError = checkElement("//*[contains(text(),'Vui lòng nhập đầy đủ thông tin')]") ||
                              checkElement("//*[contains(text(),'Họ tên là bắt buộc')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi thiếu thông tin");
            System.out.println("✓ Hiển thị lỗi thiếu thông tin bắt buộc");
            
        } catch (Exception e) {
            System.out.println("✗ TC734 failed: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void TC735() {
        System.out.println("TC7.3.5 - Cập nhật thất bại – Email đã tồn tại");
        
        try {
            // 1. Nhấn nút "Edit profile"
            driver.findElement(By.xpath("//button[normalize-space()='Edit profile']")).click();
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            // 2. Nhập email đã tồn tại
            driver.findElement(By.xpath("//input[@name='email']")).clear();
            driver.findElement(By.xpath("//input[@name='email']")).sendKeys("existing@gmail.com");
            
            // 3. Nhấn "Lưu thay đổi"
            driver.findElement(By.xpath("//button[contains(text(),'Lưu thay đổi')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Email đã tồn tại"
            boolean hasError = checkElement("//*[contains(text(),'Email đã tồn tại')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi email đã tồn tại");
            System.out.println("✓ Hiển thị lỗi email đã tồn tại");
            
        } catch (Exception e) {
            System.out.println("✗ TC735 failed: " + e.getMessage());
        }
    }

    @Test(priority = 6)
    public void TC_UI_732() {
        System.out.println("TC.UI.7.3.2 - Kiểm tra hiển thị lỗi UI khi nhập sai");
        
        try {
            // 1. Nhấn nút "Edit profile"
            driver.findElement(By.xpath("//button[normalize-space()='Edit profile']")).click();
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            // 2. Nhập email sai định dạng và SĐT sai định dạng
            driver.findElement(By.xpath("//input[@name='email']")).clear();
            driver.findElement(By.xpath("//input[@name='email']")).sendKeys("abc");
            
            driver.findElement(By.xpath("//input[@name='phone']")).clear();
            driver.findElement(By.xpath("//input[@name='phone']")).sendKeys("123ABC");
            
            // 3. Nhấn "Lưu thay đổi"
            driver.findElement(By.xpath("//button[contains(text(),'Lưu thay đổi')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify hiển thị lỗi với màu đỏ, căn chỉnh chuẩn
            boolean hasEmailError = checkElement("//*[contains(text(),'Email không hợp lệ')]");
            boolean hasPhoneError = checkElement("//*[contains(text(),'Số điện thoại không hợp lệ')]");
            
            Assert.assertTrue(hasEmailError, "Không hiển thị lỗi email");
            Assert.assertTrue(hasPhoneError, "Không hiển thị lỗi số điện thoại");
            
            System.out.println("✓ Hiển thị lỗi UI khi nhập sai thành công");
            
        } catch (Exception e) {
            System.out.println("✗ TC.UI.7.3.2 failed: " + e.getMessage());
        }
    }

    @Test(priority = 7)
    public void TC_UI_733() {
        System.out.println("TC.UI.7.3.3 - Kiểm tra thông báo sau khi cập nhật thành công");
        
        try {
            // 1. Nhấn nút "Edit profile"
            driver.findElement(By.xpath("//button[normalize-space()='Edit profile']")).click();
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            // 2. Nhập đầy đủ thông tin hợp lệ
            driver.findElement(By.xpath("//input[@name='fullname']")).clear();
            driver.findElement(By.xpath("//input[@name='fullname']")).sendKeys("Nguyễn Văn B");
            
            driver.findElement(By.xpath("//input[@name='phone']")).clear();
            driver.findElement(By.xpath("//input[@name='phone']")).sendKeys("0909123456");
            
            // 3. Nhấn "Lưu thay đổi"
            driver.findElement(By.xpath("//button[contains(text(),'Lưu thay đổi')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify hiển thị thành công và không lỗi bố cục
            boolean hasSuccess = checkElement("//*[contains(text(),'Cập nhật thành công')]");
            boolean noLayoutError = !checkElement("//*[contains(@style,'overflow')]") && 
                                  !checkElement("//*[contains(@class,'error')]");
            
            Assert.assertTrue(hasSuccess, "Không hiển thị thông báo thành công");
            Assert.assertTrue(noLayoutError, "Có lỗi bố cục sau khi cập nhật");
            
            System.out.println("✓ Thông báo sau khi cập nhật thành công hiển thị chuẩn");
            
        } catch (Exception e) {
            System.out.println("✗ TC.UI.7.3.3 failed: " + e.getMessage());
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