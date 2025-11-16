package Epic7;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.time.Duration;

public class US72 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TRÌNH DUYỆT ===");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        
        // Điều hướng đến trang đăng nhập
        driver.get("http://localhost:4200/login");
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    @Test(priority = 1)
    public void TC721() {
        System.out.println("TC7.2.1 - Đăng nhập thành công");
        
        try {
            // 2. Nhập tên đăng nhập và mật khẩu hợp lệ
            driver.findElement(By.xpath("//input[@name='username']")).clear();
            driver.findElement(By.xpath("//input[@name='username']")).sendKeys("huychgau111");
            
            driver.findElement(By.xpath("//input[@name='password']")).clear();
            driver.findElement(By.xpath("//input[@name='password']")).sendKeys("Ndth02042004@");
            
            // 3. Nhấn "Đăng nhập"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Đăng nhập thành công" và chuyển sang trang chủ
            boolean hasSuccess = checkElement("//*[contains(text(),'Đăng nhập thành công')]");
            boolean isHomePage = driver.getCurrentUrl().contains("home") || 
                                driver.getCurrentUrl().contains("dashboard") ||
                                checkElement("//*[contains(text(),'Xin chào')]");
            
            Assert.assertTrue(hasSuccess, "Không hiển thị thông báo thành công");
            Assert.assertTrue(isHomePage, "Không chuyển sang trang chủ");
            System.out.println("✓ Đăng nhập thành công");
            
            logout();
            
        } catch (Exception e) {
            System.out.println("✗ TC721 failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void TC722() {
        System.out.println("TC7.2.2 - Đăng nhập không thành công - Sai mật khẩu");
        
        try {
            // Đảm bảo đang ở trang đăng nhập
            if (!driver.getCurrentUrl().contains("login")) {
                driver.get("http://localhost:4200/login");
                try { Thread.sleep(2000); } catch (Exception e) {}
            }
            
            // 2. Nhập mật khẩu sai
            driver.findElement(By.xpath("//input[@name='username']")).clear();
            driver.findElement(By.xpath("//input[@name='username']")).sendKeys("huychgau111");
            
            driver.findElement(By.xpath("//input[@name='password']")).clear();
            driver.findElement(By.xpath("//input[@name='password']")).sendKeys("Ndth02042");
            
            // 3. Nhấn "Đăng nhập"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Sai tên đăng nhập hoặc mật khẩu"
            boolean hasError = checkElement("//*[contains(text(),'Sai tên đăng nhập hoặc mật khẩu')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi sai mật khẩu");
            System.out.println("✓ Hiển thị lỗi sai mật khẩu");
            
        } catch (Exception e) {
            System.out.println("✗ TC722 failed: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void TC723() {
        System.out.println("TC7.2.3 - Đăng nhập không thành công - Email không tồn tại");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // 2. Nhập email chưa đăng ký
            driver.findElement(By.xpath("//input[@name='username']")).clear();
            driver.findElement(By.xpath("//input[@name='username']")).sendKeys("b@gmail.com");
            
            driver.findElement(By.xpath("//input[@name='password']")).clear();
            driver.findElement(By.xpath("//input[@name='password']")).sendKeys("123456");
            
            // 3. Nhấn "Đăng nhập"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Sai tên đăng nhập hoặc mật khẩu"
            boolean hasError = checkElement("//*[contains(text(),'Sai tên đăng nhập hoặc mật khẩu')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi email không tồn tại");
            System.out.println("✓ Hiển thị lỗi email không tồn tại");
            
        } catch (Exception e) {
            System.out.println("✗ TC723 failed: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void TC724() {
        System.out.println("TC7.2.4 - Đăng nhập không thành công - Bỏ trống thông tin");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // 2. Bỏ trống email hoặc mật khẩu
            driver.findElement(By.xpath("//input[@name='username']")).clear();
            driver.findElement(By.xpath("//input[@name='password']")).clear();
            
            // 3. Nhấn "Đăng nhập"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Vui lòng nhập email và mật khẩu"
            boolean hasError = checkElement("//*[contains(text(),'Vui lòng nhập email và mật khẩu')]") ||
                              checkElement("//*[contains(text(),'không được để trống')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi thiếu thông tin");
            System.out.println("✓ Hiển thị lỗi thiếu thông tin");
            
        } catch (Exception e) {
            System.out.println("✗ TC724 failed: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void TC_UI_722() {
        System.out.println("TC.UI.7.2.2 - Kiểm tra hiển thị lỗi khi nhập sai thông tin");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // 1. Nhập email/mật khẩu sai
            driver.findElement(By.xpath("//input[@name='username']")).clear();
            driver.findElement(By.xpath("//input[@name='username']")).sendKeys("abc@gmail.com");
            
            driver.findElement(By.xpath("//input[@name='password']")).clear();
            driver.findElement(By.xpath("//input[@name='password']")).sendKeys("wrongpassword");
            
            // 2. Nhấn "Đăng nhập"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify hiển thị lỗi với màu đỏ, không lỗi bố cục
            boolean hasError = checkElement("//*[contains(text(),'Sai tên đăng nhập hoặc mật khẩu')]");
            boolean noLayoutError = !checkElement("//*[contains(@style,'overflow')]");
            
            Assert.assertTrue(hasError, "Không hiển thị lỗi đăng nhập");
            Assert.assertTrue(noLayoutError, "Có lỗi bố cục khi hiển thị lỗi");
            
            System.out.println("✓ Hiển thị lỗi khi nhập sai thông tin thành công");
            
        } catch (Exception e) {
            System.out.println("✗ TC.UI.7.2.2 failed: " + e.getMessage());
        }
    }

    @Test(priority = 6)
    public void TC_UI_723() {
        System.out.println("TC.UI.7.2.3 - Kiểm tra hiển thị khi đăng nhập thành công");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // 1. Nhập đúng tài khoản
            driver.findElement(By.xpath("//input[@name='username']")).clear();
            driver.findElement(By.xpath("//input[@name='username']")).sendKeys("huychgau111");
            
            driver.findElement(By.xpath("//input[@name='password']")).clear();
            driver.findElement(By.xpath("//input[@name='password']")).sendKeys("Ndth02042004@");
            
            // 2. Nhấn "Đăng nhập"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify chuyển sang trang dashboard, hiển thị tên người dùng
            boolean isDashboard = driver.getCurrentUrl().contains("dashboard") || 
                                 driver.getCurrentUrl().contains("home");
            boolean hasUserName = checkElement("//*[contains(text(),'Xin chào')]") || 
                                checkElement("//*[contains(@class,'user-name')]") ||
                                checkElement("//*[contains(text(),'huychgau111')]");
            boolean noLayoutError = !checkElement("//*[contains(@class,'error')]");
            
            Assert.assertTrue(isDashboard, "Không chuyển sang trang dashboard");
            Assert.assertTrue(hasUserName, "Không hiển thị tên người dùng");
            Assert.assertTrue(noLayoutError, "Có lỗi layout sau khi đăng nhập");
            
            System.out.println("✓ Hiển thị khi đăng nhập thành công chuẩn");
            
        } catch (Exception e) {
            System.out.println("✗ TC.UI.7.2.3 failed: " + e.getMessage());
        }
    }
    
    // ========== HELPER METHODS ==========
    
    private void logout() {
        try {
            System.out.println("Đang đăng xuất...");
            
            // Tìm và click nút user menu
            driver.findElement(By.xpath("//button[@class='user-btn']")).click();
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            // Tìm và click nút đăng xuất
            driver.findElement(By.xpath("//a[contains(text(),'Đăng xuất')]")).click();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify đã chuyển về trang đăng nhập
            boolean isLoginPage = driver.getCurrentUrl().contains("login");
            Assert.assertTrue(isLoginPage, "Đăng xuất không thành công");
            
            System.out.println("✓ Đăng xuất thành công");
            
        } catch (Exception e) {
            System.out.println("Không thể đăng xuất: " + e.getMessage());
            // Nếu không đăng xuất được, chuyển thẳng đến trang login
            driver.get("http://localhost:4200/login");
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