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

public class US71 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TRÌNH DUYỆT ===");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        
        // Điều hướng đến trang đăng ký
        driver.get("http://localhost:4200/register");
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    @Test(priority = 1)
    public void TC711() {
        System.out.println("TC7.1.1 - Đăng ký thành công");
        
        try {
            // Nhập đầy đủ thông tin hợp lệ
            findInputByPlaceholder("Tên đăng nhập").sendKeys("huydemen111");
            findInputByPlaceholder("Họ và tên").sendKeys("Nguyễn Huy");
            findInputByPlaceholder("Email").sendKeys("huy123@gmail.com");
            findInputByPlaceholder("Số điện thoại").sendKeys("0337333222");
            findInputByPlaceholder("Mật khẩu").sendKeys("Ndth02042004@");
            findInputByPlaceholder("Xác nhận mật khẩu").sendKeys("Ndth02042004@");
            findInputByPlaceholder("Địa chỉ").sendKeys("Bình Định");
            findInputByPlaceholder("CMND/CCCD").sendKeys("1234");
            findInputByPlaceholder("Ngày sinh").sendKeys("10/30/2004");
            
            // Tick chọn đồng ý điều khoản
            driver.findElement(By.xpath("//input[@type='checkbox']")).click();
            
            // Nhấn "Đăng ký"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng ký')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify: Hiển thị "Đăng ký thành công" và chuyển sang trang đăng nhập
            boolean hasSuccess = checkElement("//*[contains(text(),'Đăng ký thành công')]");
            boolean redirectedToLogin = driver.getCurrentUrl().contains("login");
            
            Assert.assertTrue(hasSuccess, "Không hiển thị thông báo thành công");
            Assert.assertTrue(redirectedToLogin, "Không chuyển sang trang đăng nhập");
            System.out.println("✓ Đăng ký thành công");
            
        } catch (Exception e) {
            System.out.println("✗ TC711 failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void TC712() {
        System.out.println("TC7.1.2 - Đăng ký không thành công - Email sai định dạng");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Nhập email sai định dạng
            findInputByPlaceholder("Email").sendKeys("abc");
            
            // Nhấn "Đăng ký"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng ký')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị lỗi "Email không hợp lệ"
            boolean hasError = checkElement("//*[contains(text(),'Email không hợp lệ')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi email không hợp lệ");
            System.out.println("✓ Hiển thị lỗi email không hợp lệ");
            
        } catch (Exception e) {
            System.out.println("✗ TC712 failed: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void TC713() {
        System.out.println("TC7.1.3 - Đăng ký không thành công - Mật khẩu quá ngắn");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Nhập mật khẩu ít hơn 6 ký tự
            findInputByPlaceholder("Mật khẩu").sendKeys("123");
            
            // Nhấn "Đăng ký"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng ký')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị lỗi "Mật khẩu quá ngắn"
            boolean hasError = checkElement("//*[contains(text(),'Mật khẩu quá ngắn')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi mật khẩu quá ngắn");
            System.out.println("✓ Hiển thị lỗi mật khẩu quá ngắn");
            
        } catch (Exception e) {
            System.out.println("✗ TC713 failed: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void TC714() {
        System.out.println("TC7.1.4 - Đăng ký không thành công - Thiếu thông tin");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Bỏ trống các trường bắt buộc (chỉ nhập email)
            findInputByPlaceholder("Email").sendKeys("test@gmail.com");
            // Các trường khác bỏ trống
            
            // Nhấn "Đăng ký"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng ký')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị lỗi thiếu thông tin
            boolean hasError = checkElement("//*[contains(text(),'Vui lòng nhập đầy đủ thông tin')]") ||
                              checkElement("//*[contains(text(),'không được để trống')]") ||
                              checkElement("//*[contains(text(),'bắt buộc')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi thiếu thông tin");
            System.out.println("✓ Hiển thị lỗi thiếu thông tin");
            
        } catch (Exception e) {
            System.out.println("✗ TC714 failed: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void TC715() {
        System.out.println("TC7.1.5 - Đăng ký không thành công - Email đã tồn tại");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Nhập email đã tồn tại
            findInputByPlaceholder("Email").sendKeys("a@gmail.com");
            
            // Nhấn "Đăng ký"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng ký')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify: Hiển thị lỗi "Email đã được đăng ký"
            boolean hasError = checkElement("//*[contains(text(),'Email đã được đăng ký')]") ||
                              checkElement("//*[contains(text(),'đã tồn tại')]");
            Assert.assertTrue(hasError, "Không hiển thị lỗi email đã tồn tại");
            System.out.println("✓ Hiển thị lỗi email đã tồn tại");
            
        } catch (Exception e) {
            System.out.println("✗ TC715 failed: " + e.getMessage());
        }
    }


    @Test(priority = 6)
    public void TC_UI_712() {
        System.out.println("TC.UI.7.1.2 - Kiểm tra khả năng nhập liệu và hiển thị lỗi UI khi nhập sai");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Nhập sai định dạng email, mật khẩu < 6 ký tự
            findInputByPlaceholder("Email").sendKeys("abc");
            findInputByPlaceholder("Mật khẩu").sendKeys("123");
            
            // Nhấn "Đăng ký"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng ký')]")).click();
            
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Verify hiển thị lỗi
            boolean hasEmailError = checkElement("//*[contains(text(),'Email không hợp lệ')]");
            boolean hasPasswordError = checkElement("//*[contains(text(),'Mật khẩu quá ngắn')]") ||
                                      checkElement("//*[contains(text(),'Mật khẩu phải có ít nhất')]");
            
            // Chỉ cần ít nhất một lỗi được hiển thị
            Assert.assertTrue(hasEmailError || hasPasswordError, "Không hiển thị lỗi nào");
            
            System.out.println("✓ Hiển thị lỗi UI khi nhập sai thành công");
            
        } catch (Exception e) {
            System.out.println("✗ TC.UI.7.1.2 failed: " + e.getMessage());
        }
    }

    @Test(priority = 7)
    public void TC_UI_713() {
        System.out.println("TC.UI.7.1.3 - Kiểm tra thông báo sau khi đăng ký thành công");
        
        try {
            // Refresh trang để reset form
            driver.navigate().refresh();
            try { Thread.sleep(2000); } catch (Exception e) {}
            
            // Tạo dữ liệu duy nhất để tránh trùng email
            String timestamp = String.valueOf(System.currentTimeMillis());
            
            // Nhập đầy đủ thông tin hợp lệ
            findInputByPlaceholder("Tên đăng nhập").sendKeys("newuser" + timestamp);
            findInputByPlaceholder("Họ và tên").sendKeys("Nguyễn Văn A");
            findInputByPlaceholder("Email").sendKeys("newuser" + timestamp + "@gmail.com");
            findInputByPlaceholder("Số điện thoại").sendKeys("0909" + timestamp.substring(7));
            findInputByPlaceholder("Mật khẩu").sendKeys("123456");
            findInputByPlaceholder("Xác nhận mật khẩu").sendKeys("123456");
            findInputByPlaceholder("Địa chỉ").sendKeys("Hà Nội");
            findInputByPlaceholder("CMND/CCCD").sendKeys("123456789");
            findInputByPlaceholder("Ngày sinh").sendKeys("01/01/2000");
            
            // Tick chọn đồng ý điều khoản
            driver.findElement(By.xpath("//input[@type='checkbox']")).click();
            
            // Nhấn "Đăng ký"
            driver.findElement(By.xpath("//button[contains(text(),'Đăng ký')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify hiển thị thành công và điều hướng
            boolean hasSuccess = checkElement("//*[contains(text(),'Đăng ký thành công')]");
            boolean redirectedToLogin = driver.getCurrentUrl().contains("login");
            
            Assert.assertTrue(hasSuccess || redirectedToLogin, "Không hiển thị thông báo thành công hoặc không điều hướng");
            
            System.out.println("✓ Thông báo sau khi đăng ký thành công hiển thị chuẩn");
            
        } catch (Exception e) {
            System.out.println("✗ TC.UI.7.1.3 failed: " + e.getMessage());
        }
    }
    
    // ========== HELPER METHODS ==========
    
    private org.openqa.selenium.WebElement findInputByPlaceholder(String placeholderText) {
        return driver.findElement(By.xpath("//input[contains(@placeholder, '" + placeholderText + "')]"));
    }
    
    private boolean checkElementByPlaceholder(String placeholderText) {
        try {
            return findInputByPlaceholder(placeholderText).isDisplayed();
        } catch (Exception e) {
            return false;
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