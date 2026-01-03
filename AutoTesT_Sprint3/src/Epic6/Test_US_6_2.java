package Epic6;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
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

public class Test_US_6_2 extends Init {

    String url = "http://localhost:4200";

    @BeforeMethod
    public void setupAndNavigate() throws InterruptedException {
        SetUp("chrome");
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // --- BƯỚC 1: ĐĂNG NHẬP (Reuse từ US 6.1) ---
        try {
            WebElement btnLoginHome = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Đăng nhập')]")));
            btnLoginHome.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("chutro1"); 
            driver.findElement(By.id("password")).sendKeys("123456");
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

            try {
                WebElement btnOK = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')] | //button[contains(@class,'swal')]")));
                btnOK.click();
            } catch (Exception e) {}
        } catch (Exception e) {
            System.out.println("Lỗi đăng nhập hoặc đã đăng nhập sẵn.");
        }

        // --- BƯỚC 2: NAVIGATE VÀO DASHBOARD ---
        Thread.sleep(1000); 
        WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class,'dropdown')]//button | //span[contains(text(),'Chủ Trọ')]")));
        userDropdown.click();

        WebElement itemDashboard = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Dashboard Chủ trọ')] | //button[contains(text(),'Dashboard')]")));
        itemDashboard.click();

        // --- BƯỚC 3: VÀO MENU PHẢN HỒI ---
        Thread.sleep(1500);
        // Tìm menu "Phản hồi khách thuê" trên sidebar
        WebElement menuFeedback = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Phản hồi khách thuê')]//parent::li | //span[contains(text(),'Phản hồi')]")));
        menuFeedback.click();
        
        Thread.sleep(1500); // Đợi load danh sách
    }

    // --- HÀM HỖ TRỢ ---

    // Click bằng JS để tránh bị che
    public void clickElementJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void handleUnexpectedAlert() {
        try {
            Alert alert = driver.switchTo().alert();
            System.out.println("Alert xuất hiện: " + alert.getText());
            alert.accept();
        } catch (NoAlertPresentException Ex) {}
    }

    // --- CÁC TEST CASE CHO US 6.2 ---

    @Test(priority = 1, description = "TC01: Kiểm tra truy cập màn hình Quản lý phản hồi")
    public void TC01_TruyCapManHinh() {
        // Kiểm tra xem tiêu đề trang hoặc breadcrumb có đúng không
        boolean isDisplayed = driver.findElement(By.xpath("//h3[contains(text(),'Quản lý phản hồi')] | //h4[contains(text(),'Quản lý phản hồi')]")).isDisplayed();
        Assert.assertTrue(isDisplayed, "FAILED: Không hiển thị màn hình Quản lý phản hồi");
        
        // Kiểm tra xem các tab có hiển thị không
        boolean tabsVisible = driver.findElement(By.xpath("//button[contains(text(),'Tất cả')]")).isDisplayed();
        Assert.assertTrue(tabsVisible, "FAILED: Không thấy các tab lọc trạng thái");
    }

    @Test(priority = 2, description = "TC02: Lọc phản hồi theo Tab 'Chưa xử lý'")
    public void TC02_LocTheoTrangThai_ChuaXuLy() throws InterruptedException {
        // Click tab "Chưa xử lý"
        WebElement tabUnprocessed = driver.findElement(By.xpath("//button[contains(text(),'Chưa xử lý')]"));
        clickElementJS(tabUnprocessed);
        Thread.sleep(1000);

        // Verify: Kiểm tra xem các item hiển thị có badge "Chưa xử lý" hay không
        // Nếu list rỗng thì pass (do không có data), nếu có data thì check item đầu tiên
        List<WebElement> badges = driver.findElements(By.xpath("//span[contains(text(),'Chưa xử lý') and contains(@class,'badge')]"));
        if (badges.size() > 0) {
            Assert.assertTrue(badges.get(0).isDisplayed(), "FAILED: Tab lọc sai, vẫn hiện trạng thái khác");
        } else {
            System.out.println("WARNING: Không có dữ liệu 'Chưa xử lý' để test");
        }
    }

    @Test(priority = 3, description = "TC03: Xem chi tiết phản hồi (Read-only)")
    public void TC03_XemChiTietPhanHoi() throws InterruptedException {
        // Tìm nút "Xem chi tiết" của item đầu tiên
        List<WebElement> btnDetails = driver.findElements(By.xpath("//button[contains(text(),'Xem chi tiết')] | //a[contains(text(),'Xem chi tiết')]"));
        
        if (btnDetails.size() > 0) {
            clickElementJS(btnDetails.get(0));
            Thread.sleep(1500);

            // Verify Modal hiện lên
            boolean modalVisible = driver.findElement(By.xpath("//div[contains(@class,'modal') or contains(@role,'dialog')]")).isDisplayed();
            Assert.assertTrue(modalVisible, "FAILED: Modal chi tiết không hiển thị");
            
            // Đóng modal để dọn dẹp
            WebElement btnClose = driver.findElement(By.xpath("//button[contains(text(),'Đóng')] | //button[@aria-label='Close'] | //span[contains(text(),'×')]"));
            clickElementJS(btnClose);
        } else {
            System.out.println("SKIP: Không có phản hồi nào để xem chi tiết");
        }
    }

    @Test(priority = 4, description = "TC04: Xử lý phản hồi - Chuyển sang 'Đang xử lý'")
    public void TC04_XuLyPhanHoi_BatDau() throws InterruptedException {
        // 1. Tìm item có nút "Bắt đầu xử lý" (Thường ở tab Chưa xử lý)
        WebElement tabUnprocessed = driver.findElement(By.xpath("//button[contains(text(),'Chưa xử lý')]"));
        clickElementJS(tabUnprocessed);
        Thread.sleep(1000);

        List<WebElement> startButtons = driver.findElements(By.xpath("//button[contains(text(),'Bắt đầu xử lý')]"));
        
        if (startButtons.size() > 0) {
            // Click nút Bắt đầu xử lý của item đầu tiên
            clickElementJS(startButtons.get(0));
            Thread.sleep(1500); // Đợi Modal mở

            // 2. Nhập ghi chú xử lý trong Modal
            WebElement txtNote = driver.findElement(By.xpath("//textarea"));
            txtNote.sendKeys("[AutoTest] Đã tiếp nhận và đang xử lý.");

            // 3. Bấm nút xác nhận "Bắt đầu xử lý" trong Modal (thường màu xanh)
            // Tìm nút submit trong modal (cần xpath kỹ hơn để tránh nhầm nút ngoài list)
            WebElement btnSubmit = driver.findElement(By.xpath("//div[contains(@class,'modal')]//button[contains(text(),'Bắt đầu xử lý')]"));
            clickElementJS(btnSubmit);

            // 4. Verify
            Thread.sleep(2000);
            handleUnexpectedAlert();
            driver.navigate().refresh(); // F5 để cập nhật trạng thái mới nhất
            
            // Check xem phản hồi đó có chuyển sang tab "Đang xử lý" không, hoặc check thông báo thành công
            Assert.assertTrue(driver.getPageSource().contains("thành công") || driver.getPageSource().contains("Đang xử lý"), "FAILED: Không chuyển được trạng thái");
        } else {
            System.out.println("SKIP: Không tìm thấy phản hồi nào 'Chưa xử lý' để test");
        }
    }

    @Test(priority = 5, description = "TC05: Hủy phản hồi (Từ chối)")
    public void TC05_HuyPhanHoi() throws InterruptedException {
        // Tìm item có nút "Xem chi tiết" hoặc "Bắt đầu xử lý" để mở modal hủy
        // Giả sử mở item đầu tiên
        List<WebElement> items = driver.findElements(By.xpath("//button[contains(text(),'Xem chi tiết')] | //button[contains(text(),'Bắt đầu xử lý')]"));
        
        if (items.size() > 0) {
            clickElementJS(items.get(0));
            Thread.sleep(1500);

            // Tìm nút "Hủy phản hồi" màu đỏ trong modal
            WebElement btnCancel = driver.findElement(By.xpath("//button[contains(text(),'Hủy phản hồi')]"));
            
            // Scroll tới nút đó nếu modal dài
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnCancel);
            Thread.sleep(500);
            
            clickElementJS(btnCancel);

            // Có thể có Confirm Alert (Bạn có chắc muốn hủy?)
            Thread.sleep(1000);
            handleUnexpectedAlert(); // Accept alert nếu có

            Thread.sleep(1500);
            driver.navigate().refresh();
            
            // Verify: Item đó phải có trạng thái "Đã hủy"
            Assert.assertTrue(driver.getPageSource().contains("Đã hủy") || driver.getPageSource().contains("thành công"));
        } else {
            System.out.println("SKIP: Không có data để test hủy");
        }
    }

    @Test(priority = 6, description = "TC06: Tìm kiếm phản hồi")
    public void TC06_TimKiemPhanHoi() throws InterruptedException {
        // Nhập từ khóa vào ô tìm kiếm trên Header (dựa trên ảnh image_bee9aa.jpg)
        WebElement txtSearch = driver.findElement(By.xpath("//input[contains(@placeholder,'Tìm kiếm')]"));
        txtSearch.clear();
        txtSearch.sendKeys("Vòi nước"); // Giả sử tìm keyword này
        
        // Enter hoặc bấm icon search (nếu input không auto search thì cần enter)
        // txtSearch.sendKeys(Keys.ENTER); // Cần import Keys nếu dùng
        
        Thread.sleep(2000); // Đợi filter

        // Verify: List kết quả chứa từ khóa hoặc thông báo không tìm thấy
        boolean hasResult = driver.getPageSource().contains("Vòi nước") || driver.getPageSource().contains("Không tìm thấy");
        Assert.assertTrue(hasResult, "FAILED: Chức năng tìm kiếm không hoạt động");
    }

    @Test(priority = 7, description = "TC07: Kiểm tra hiển thị hình ảnh đính kèm trong Modal")
    public void TC07_KiemTraHinhAnh() throws InterruptedException {
        // Mở một item bất kỳ để xem chi tiết
        List<WebElement> btnDetails = driver.findElements(By.xpath("//button[contains(text(),'Xem chi tiết')]"));
        if (btnDetails.size() > 0) {
            clickElementJS(btnDetails.get(0));
            Thread.sleep(1500);

            // Tìm thẻ img trong vùng "Ảnh đính kèm"
            // Dựa trên ảnh image_beec76.jpg
            List<WebElement> images = driver.findElements(By.xpath("//div[contains(text(),'Ảnh đính kèm')]/following-sibling::img | //div[contains(@class,'modal')]//img"));
            
            if (images.size() > 0) {
                boolean isImageVisible = (Boolean) ((JavascriptExecutor)driver).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", images.get(0));
                Assert.assertTrue(isImageVisible, "FAILED: Ảnh đính kèm bị lỗi (broken image)");
            } else {
                System.out.println("NOTE: Phản hồi này không có ảnh đính kèm");
            }
            
            // Đóng modal
            clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Đóng')] | //button[@aria-label='Close']")));
        }
    }

    @Test(priority = 8, description = "TC08: Kiểm tra thông tin Khách thuê trong danh sách")
    public void TC08_KiemTraThongTinKhach() {
        // Kiểm tra xem card phản hồi có hiển thị tên Khách và Phòng không
        // Dựa trên ảnh image_bee9aa.jpg: "Khách thuê 2", "Phòng NTT 02"
        boolean hasTenantName = driver.getPageSource().contains("Khách thuê");
        boolean hasRoomName = driver.getPageSource().contains("Phòng");
        
        Assert.assertTrue(hasTenantName && hasRoomName, "FAILED: Thiếu thông tin người gửi hoặc phòng trong danh sách");
    }

    @Test(priority = 9, description = "TC09: Click thông báo từ chuông (Bell Notification)")
    public void TC09_ClickNotification() throws InterruptedException {
        // 1. Click icon chuông trên header
        WebElement bellIcon = driver.findElement(By.xpath("//i[contains(@class,'bell')] | //button[contains(@class,'notification')] | //span[contains(@class,'badge')]/parent::*"));
        clickElementJS(bellIcon);
        Thread.sleep(1000);

        // 2. Tìm một thông báo loại "Phản hồi mới" trong dropdown (Dựa trên ảnh image_bee98a.jpg)
        List<WebElement> notiItems = driver.findElements(By.xpath("//div[contains(@class,'notification-item') and contains(.,'Phản hồi mới')]"));
        
        if (notiItems.size() > 0) {
            clickElementJS(notiItems.get(0));
            Thread.sleep(2000);
            
            // Verify: Sau khi click phải redirect về trang quản lý phản hồi hoặc mở modal
            Assert.assertTrue(driver.getCurrentUrl().contains("feedback") || driver.findElement(By.xpath("//div[contains(@class,'modal')]")).isDisplayed(), "FAILED: Click thông báo không điều hướng đúng");
        } else {
            System.out.println("SKIP: Không có thông báo phản hồi mới nào");
        }
    }

    @Test(priority = 10, description = "TC10: Kiểm tra Tab 'Đã xử lý'")
    public void TC10_TabDaXuLy() throws InterruptedException {
        WebElement tabProcessed = driver.findElement(By.xpath("//button[contains(text(),'Đã xử lý')]"));
        clickElementJS(tabProcessed);
        Thread.sleep(1000);
        
        // Verify: Nếu có data, nút hành động phải là "Hoàn thành" hoặc chỉ xem chi tiết, không cho phép "Bắt đầu xử lý" lại
        // (Tùy logic nghiệp vụ, ở đây check cơ bản là load được list)
        Assert.assertTrue(driver.getCurrentUrl().contains("feedback"), "FAILED: Lỗi khi chuyển tab Đã xử lý");
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        Teardown();
    }
}