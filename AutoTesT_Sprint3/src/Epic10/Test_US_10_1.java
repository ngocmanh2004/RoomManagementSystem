package Epic10;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import Initialization.Init;
import java.time.Duration;
import java.util.List;

public class Test_US_10_1 extends Init {

    String url = "http://localhost:4200";

    @BeforeMethod
    public void setupAndNavigate() throws InterruptedException {
        SetUp("chrome");
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // --- BƯỚC 1: ĐĂNG NHẬP VỚI VAI TRÒ KHÁCH THUÊ ---
        try {
            WebElement btnLoginHome = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Đăng nhập')]")));
            btnLoginHome.click();

            // Đăng nhập bằng tài khoản Khách (tenant)
            // Giả sử user: khachthue1 / pass: 123456 (Cần update theo data thật)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("user2"); 
            driver.findElement(By.id("password")).sendKeys("123456");
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

            try {
                WebElement btnOK = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')] | //button[contains(@class,'swal')]")));
                btnOK.click();
            } catch (Exception e) {}
        } catch (Exception e) {
            System.out.println("Lỗi đăng nhập hoặc đã đăng nhập sẵn.");
        }

        Thread.sleep(1500); // Đợi load Dashboard Khách
    }

    // --- HÀM HỖ TRỢ ---
    public void clickElementJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    // --- CÁC TEST CASE CHO US 10.1 (NHẬN THÔNG BÁO - KHÁCH) ---

    @Test(priority = 1, description = "TC_203, TC_204: Kiểm tra Icon Chuông và Mở Dropdown nhanh")
    public void TC01_CheckBellAndDropdown() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        
        // 1. Kiểm tra icon chuông (Dựa trên hình image_cf6056.jpg)
        WebElement bellIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//i[contains(@class,'bell')] | //button[contains(@class,'notification')] | //span[contains(@class,'badge')]/parent::*")));
        Assert.assertTrue(bellIcon.isDisplayed(), "Icon chuông không hiển thị");

        // Kiểm tra Badge số lượng (chấm đỏ)
        try {
            WebElement badge = bellIcon.findElement(By.xpath(".//span[contains(@class,'badge')] | //span[contains(@class,'count')]"));
            if (badge.isDisplayed()) {
                System.out.println("Có " + badge.getText() + " thông báo mới.");
            }
        } catch (Exception e) {
            System.out.println("Không có badge thông báo mới.");
        }

        // 2. Click mở dropdown
        bellIcon.click();
        Thread.sleep(1000);

        // Kiểm tra Dropdown hiển thị (Tiêu đề 'Thông báo')
        WebElement dropdownTitle = driver.findElement(By.xpath("//h6[contains(text(),'Thông báo')] | //div[contains(@class,'dropdown-header')]"));
        Assert.assertTrue(dropdownTitle.isDisplayed(), "Dropdown thông báo không mở");
    }

    @Test(priority = 2, description = "TC_205, TC_215: Kiểm tra nội dung tóm tắt và Icon trong dropdown")
    public void TC02_CheckDropdownContent() throws InterruptedException {
        // Mở dropdown
        WebElement bellIcon = driver.findElement(By.xpath("//i[contains(@class,'bell')] | //button[contains(@class,'notification')] | //span[contains(@class,'badge')]/parent::*"));
        clickElementJS(bellIcon);
        Thread.sleep(1000);

        // Lấy list item trong dropdown (Dựa trên hình image_cf6056.jpg)
        List<WebElement> items = driver.findElements(By.xpath("//div[contains(@class,'notification-item')] | //li[contains(@class,'notification-item')]"));
        
        if (items.size() > 0) {
            WebElement firstItem = items.get(0);
            
            // Check Icon chuông vàng (class fa-bell text-warning hoặc tương tự)
            boolean hasIcon = firstItem.findElement(By.xpath(".//i[contains(@class,'fa-bell')] | .//img")).isDisplayed();
            Assert.assertTrue(hasIcon, "Thiếu icon loại tin");

            // Check Tiêu đề đậm
            boolean hasTitle = firstItem.findElement(By.xpath(".//strong | .//h6")).isDisplayed();
            Assert.assertTrue(hasTitle, "Thiếu tiêu đề đậm");

            // Check nút 'x' xóa nhanh (Dựa trên hình: dấu x mờ bên phải)
            boolean hasCloseBtn = firstItem.findElement(By.xpath(".//button[contains(@class,'close')] | .//span[contains(@class,'close')] | .//i[contains(@class,'times')]")).isDisplayed();
            Assert.assertTrue(hasCloseBtn, "Thiếu nút xóa nhanh (x)");
        } else {
            System.out.println("Không có thông báo nào trong dropdown để test.");
        }
    }

    @Test(priority = 3, description = "TC_206: Xóa/Ẩn thông báo nhanh từ dropdown")
    public void TC03_RemoveNotificationQuickly() throws InterruptedException {
        // Mở dropdown
        WebElement bellIcon = driver.findElement(By.xpath("//i[contains(@class,'bell')] | //button[contains(@class,'notification')] | //span[contains(@class,'badge')]/parent::*"));
        clickElementJS(bellIcon);
        Thread.sleep(1000);

        List<WebElement> items = driver.findElements(By.xpath("//div[contains(@class,'notification-item')]"));
        int initialCount = items.size();

        if (initialCount > 0) {
            // Click nút 'x' của item đầu tiên
            WebElement closeBtn = items.get(0).findElement(By.xpath(".//button[contains(@class,'close')] | .//span[contains(text(),'×')]"));
            clickElementJS(closeBtn);
            Thread.sleep(1000);

            // Verify số lượng giảm đi 1
            List<WebElement> itemsAfter = driver.findElements(By.xpath("//div[contains(@class,'notification-item')]"));
            Assert.assertEquals(itemsAfter.size(), initialCount - 1, "Thông báo không bị xóa khỏi dropdown");
        }
    }

    @Test(priority = 4, description = "TC_207, TC_214: Chuyển đến trang 'Xem tất cả'")
    public void TC04_GoToAllNotifications() throws InterruptedException {
        // 1. Mở dropdown bell
        WebElement bellIcon = driver.findElement(By.xpath("//i[contains(@class,'bell')] | //button[contains(@class,'notification')] | //span[contains(@class,'badge')]/parent::*"));
        clickElementJS(bellIcon);
        Thread.sleep(1000);

        // 2. Click "Xem tất cả" (Dựa trên hình image_cf6056.jpg: dòng chữ xanh cuối dropdown)
        WebElement viewAllLink = driver.findElement(By.xpath("//a[contains(text(),'Xem tất cả')] | //button[contains(text(),'Xem tất cả')]"));
        clickElementJS(viewAllLink);
        
        Thread.sleep(1500);
        // Verify URL hoặc Header trang
        Assert.assertTrue(driver.getCurrentUrl().contains("notification"), "Không chuyển trang đúng");
        Assert.assertTrue(driver.findElement(By.xpath("//h3[contains(text(),'Thông báo')] | //h4[contains(text(),'Thông báo')]")).isDisplayed());
    }

    // --- CÁC TEST CASE Ở TRANG DANH SÁCH (FULL PAGE) ---
    
    // Helper method để vào trang danh sách thông báo từ menu Header (theo yêu cầu đặc biệt)
    public void navigateToNotificationPage() throws InterruptedException {
        // 1. Click tên user trên header (Dựa trên hình image_cf5cf2.jpg: "Khách thuê 2")
        WebElement userDropdown = driver.findElement(By.xpath("//div[contains(@class,'dropdown')]//button[contains(@class,'dropdown-toggle')] | //span[contains(text(),'Khách thuê')]"));
        clickElementJS(userDropdown);
        Thread.sleep(500);

        // 2. Chọn "Thông báo" trong menu
        WebElement itemNoti = driver.findElement(By.xpath("//a[contains(text(),'Thông báo')] | //button[contains(text(),'Thông báo')]"));
        clickElementJS(itemNoti);
        Thread.sleep(1500);
    }

    @Test(priority = 5, description = "TC_208, TC_209, TC_210: Kiểm tra các Tab Lọc (Tất cả, Chưa đọc, Đã đọc)")
    public void TC05_CheckTabs() throws InterruptedException {
        navigateToNotificationPage();

        // Dựa trên hình image_cf5d12.jpg
        WebElement tabAll = driver.findElement(By.xpath("//a[contains(text(),'Tất cả')] | //button[contains(text(),'Tất cả')]"));
        WebElement tabUnread = driver.findElement(By.xpath("//a[contains(text(),'Chưa đọc')] | //button[contains(text(),'Chưa đọc')]"));
        WebElement tabRead = driver.findElement(By.xpath("//a[contains(text(),'Đã đọc')] | //button[contains(text(),'Đã đọc')]"));

        Assert.assertTrue(tabAll.isDisplayed());
        Assert.assertTrue(tabUnread.isDisplayed());
        Assert.assertTrue(tabRead.isDisplayed());

        // Test switch tab
        clickElementJS(tabRead);
        Thread.sleep(1000);
        // Verify active class hoặc list hiển thị (Check element nào đó đặc trưng)
        Assert.assertTrue(driver.getCurrentUrl().contains("notification"), "Vẫn ở trang thông báo");
    }

    @Test(priority = 6, description = "TC_211, TC_212, TC_215: Kiểm tra chi tiết item trong danh sách đầy đủ")
    public void TC06_CheckListItemDetails() throws InterruptedException {
        navigateToNotificationPage();

        // Lấy list item (Dựa trên hình image_cf5d12.jpg)
        List<WebElement> items = driver.findElements(By.xpath("//div[contains(@class,'list-group-item')] | //div[contains(@class,'card')]")); // Cần XPath đúng cho row item
        
        if (items.size() > 0) {
            WebElement firstItem = items.get(0);
            
            // Check Icon
            Assert.assertTrue(firstItem.findElement(By.xpath(".//i[contains(@class,'fa')] | .//img")).isDisplayed(), "Thiếu icon");
            
            // Check Tiêu đề đậm (VD: [TC09] Gửi 2 phòng)
            Assert.assertTrue(firstItem.findElement(By.xpath(".//strong | .//h5 | .//h6")).isDisplayed(), "Thiếu tiêu đề");
            
            // Check Nội dung (Text dài)
            WebElement content = firstItem.findElement(By.xpath(".//p | .//span[contains(@class,'content')]"));
            Assert.assertTrue(content.isDisplayed() && content.getText().length() > 0, "Thiếu nội dung");
            
            // Check Tag "Hệ thống" hoặc "Đã hủy" (Màu xanh/đỏ)
            Assert.assertTrue(firstItem.findElement(By.xpath(".//span[contains(@class,'badge')] | .//small[contains(@class,'text')]")).isDisplayed(), "Thiếu tag/thời gian");
        } else {
            System.out.println("Danh sách thông báo trống.");
        }
    }

    @Test(priority = 7, description = "TC_213: Kiểm tra Responsive (Resize window)")
    public void TC07_CheckResponsive() throws InterruptedException {
        navigateToNotificationPage();

        // Resize window về kích thước mobile
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 812)); // iPhone X size
        Thread.sleep(1000);

        // Verify layout không vỡ (Cơ bản check element vẫn visible)
        WebElement listContainer = driver.findElement(By.xpath("//div[contains(@class,'list-group')] | //div[contains(@class,'container')]"));
        Assert.assertTrue(listContainer.isDisplayed(), "Layout bị ẩn khi resize");

        // Resize lại full screen
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        Teardown();
    }
}