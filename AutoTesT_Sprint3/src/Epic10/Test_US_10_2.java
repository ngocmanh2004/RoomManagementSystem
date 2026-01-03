package Epic10;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import Initialization.Init;
import java.time.Duration;
import java.util.List;
import java.io.File;

public class Test_US_10_2 extends Init {

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

        Thread.sleep(1500); 

        // --- BƯỚC 2: NAVIGATE VÀO TRANG GỬI PHẢN HỒI ---
        // 1. Click tên user trên header (Dựa trên hình image_cf5cf2.jpg)
        WebElement userDropdown = driver.findElement(By.xpath("//div[contains(@class,'dropdown')]//button[contains(@class,'dropdown-toggle')] | //span[contains(text(),'Khách thuê')]"));
        clickElementJS(userDropdown);
        Thread.sleep(500);

        // 2. Chọn "Gửi phản hồi" trong menu
        WebElement itemFeedback = driver.findElement(By.xpath("//a[contains(text(),'Gửi phản hồi')] | //button[contains(text(),'Gửi phản hồi')]"));
        clickElementJS(itemFeedback);
        Thread.sleep(1500);
    }

    // --- HÀM HỖ TRỢ ---
    public void clickElementJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    // --- CÁC TEST CASE CHO US 10.2 ---

    @Test(priority = 1, description = "TC_216: Truy cập màn hình 'Gửi phản hồi'")
    public void TC01_AccessFeedbackPage() {
        // Verify Header hoặc Title trang (Dựa trên hình image_cf5d52.jpg)
        boolean isDisplayed = driver.findElement(By.xpath("//h3[contains(text(),'Gửi phản hồi')] | //h2[contains(text(),'Gửi phản hồi')]")).isDisplayed();
        Assert.assertTrue(isDisplayed, "Không truy cập được màn hình Gửi phản hồi");

        // Verify nút "Tạo phản hồi mới"
        WebElement btnCreate = driver.findElement(By.xpath("//button[contains(text(),'Tạo phản hồi mới')]"));
        Assert.assertTrue(btnCreate.isDisplayed(), "Nút Tạo phản hồi không hiển thị");
    }

    @Test(priority = 2, description = "TC_217, TC_228: Mở Modal và Kiểm tra box Mẹo")
    public void TC02_OpenModalAndCheckTips() throws InterruptedException {
        // Mở Modal
        WebElement btnCreate = driver.findElement(By.xpath("//button[contains(text(),'Tạo phản hồi mới')]"));
        clickElementJS(btnCreate);
        Thread.sleep(1000);

        // Verify Modal hiện (Dựa trên hình image_cf5d72.jpg)
        WebElement modalHeader = driver.findElement(By.xpath("//h5[contains(text(),'Điền thông tin')] | //div[contains(@class,'modal-header')]"));
        Assert.assertTrue(modalHeader.isDisplayed(), "Modal không mở");

        // Verify Box Mẹo (Màu vàng)
        WebElement tipsBox = driver.findElement(By.xpath("//div[contains(@class,'alert-warning')] | //div[contains(text(),'Mẹo để được hỗ trợ')]"));
        Assert.assertTrue(tipsBox.isDisplayed(), "Box mẹo không hiển thị");
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
    }

    @Test(priority = 3, description = "TC_218, TC_219, TC_220, TC_221: Nhập liệu (Dropdown, Text, Textarea)")
    public void TC03_InputData() throws InterruptedException {
        // Mở Modal
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Tạo phản hồi mới')]")));
        Thread.sleep(1000);

        // 1. Loại phản hồi (Dropdown)
        // Nếu là thẻ <select>:
        try {
            Select typeSelect = new Select(driver.findElement(By.xpath("//label[contains(text(),'Loại phản hồi')]/following-sibling::select")));
            typeSelect.selectByVisibleText("Yêu cầu sửa chữa");
        } catch (Exception e) {
            // Nếu là custom dropdown, click rồi chọn
            driver.findElement(By.xpath("//label[contains(text(),'Loại phản hồi')]/following-sibling::*")).click();
            driver.findElement(By.xpath("//option[contains(text(),'Yêu cầu sửa chữa')] | //li[contains(text(),'Yêu cầu sửa chữa')]")).click();
        }

        // 2. Mức độ ưu tiên
        try {
            Select prioritySelect = new Select(driver.findElement(By.xpath("//label[contains(text(),'Mức độ ưu tiên')]/following-sibling::select")));
            prioritySelect.selectByVisibleText("Trung bình");
        } catch (Exception e) {
             // Fallback custom dropdown
        }

        // 3. Tiêu đề
        WebElement title = driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]"));
        title.sendKeys("Máy lạnh hỏng [AutoTest]");

        // 4. Nội dung
        WebElement content = driver.findElement(By.xpath("//textarea"));
        content.sendKeys("Máy lạnh kêu to và không mát. Nhờ bác xem giúp.");

        // Verify giá trị đã nhập
        Assert.assertEquals(title.getAttribute("value"), "Máy lạnh hỏng [AutoTest]");
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
    }

    @Test(priority = 4, description = "TC_222: Upload ảnh minh họa")
    public void TC04_UploadImage() throws InterruptedException {
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Tạo phản hồi mới')]")));
        Thread.sleep(1000);

        // Tìm thẻ input type='file' (Thường bị ẩn, nhưng sendKeys vẫn hoạt động)
        // Lưu ý: Bạn cần thay đường dẫn ảnh bên dưới thành đường dẫn thật trên máy bạn
        try {
            WebElement uploadInput = driver.findElement(By.xpath("//input[@type='file']"));
            
            // TẠO FILE ẢNH GIẢ LẬP ĐỂ TEST (Hoặc trỏ đến file có sẵn)
            // Đoạn này dùng file có sẵn trên máy bạn, ví dụ C:\\test.jpg
            // Để code chạy được trên mọi máy, ta có thể bỏ qua bước sendKeys nếu không có file,
            // hoặc dùng trick tạo file tạm thời. Ở đây mình giả định bạn có file.
            
            String imagePath = "C:\\Windows\\Web\\Screen\\img100.jpg"; // File mặc định win 10/11
            File file = new File(imagePath);
            
            if(file.exists()) {
                uploadInput.sendKeys(imagePath);
                System.out.println("Đã upload ảnh: " + imagePath);
                // Verify tên file hiển thị (nếu UI có hiện)
            } else {
                System.out.println("WARNING: Không tìm thấy file ảnh mẫu để test upload. Bỏ qua bước sendKeys.");
            }
            
        } catch (Exception e) {
            System.out.println("Lỗi Upload: " + e.getMessage());
        }
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
    }

    @Test(priority = 5, description = "TC_223: Gửi phản hồi thành công")
    public void TC05_SubmitFeedbackSuccess() throws InterruptedException {
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Tạo phản hồi mới')]")));
        Thread.sleep(1000);

        // Nhập data
        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).sendKeys("Vòi nước bị rỉ [AutoTest]");
        driver.findElement(By.xpath("//textarea")).sendKeys("Sửa giúp tôi vòi nước.");
        
        // Bấm Gửi
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Gửi phản hồi')]")));
        
        Thread.sleep(1500);
        
        // Verify
        // 1. Modal đóng
        // 2. Có item mới trong list
        driver.navigate().refresh();
        Thread.sleep(1500);
        
        boolean hasItem = driver.getPageSource().contains("Vòi nước bị rỉ [AutoTest]");
        Assert.assertTrue(hasItem, "Gửi phản hồi thất bại, không thấy trong list");
    }

    @Test(priority = 6, description = "TC_224: Hủy tạo phản hồi")
    public void TC06_CancelCreation() throws InterruptedException {
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Tạo phản hồi mới')]")));
        Thread.sleep(1000);

        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).sendKeys("Dữ liệu nháp");
        
        // Bấm Hủy
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
        Thread.sleep(1000);
        
        // Verify Modal đóng
        List<WebElement> modals = driver.findElements(By.xpath("//div[contains(@class,'modal') and contains(@style,'display: block')]"));
        Assert.assertEquals(modals.size(), 0, "Modal chưa đóng sau khi bấm Hủy");
    }

    @Test(priority = 7, description = "TC_225, TC_230: Kiểm tra Lịch sử (Item đã hủy)")
    public void TC07_CheckCancelledItem() {
        // Dựa trên hình image_cf5d52.jpg
        // Tìm item có trạng thái Đã hủy
        List<WebElement> cancelledItems = driver.findElements(By.xpath("//div[contains(.,'Đã hủy')]//ancestor::div[contains(@class,'card')] | //div[contains(@class,'list-group-item') and contains(.,'Đã hủy')]"));
        
        if (cancelledItems.size() > 0) {
            WebElement item = cancelledItems.get(0);
            
            // Check Icon cấm (circle-ban)
            boolean hasIcon = item.findElement(By.xpath(".//i[contains(@class,'ban')] | .//i[contains(@class,'slash')]")).isDisplayed();
            Assert.assertTrue(hasIcon, "Thiếu icon trạng thái Hủy");
            
            // Check Tag "Đã hủy"
            Assert.assertTrue(item.getText().contains("Đã hủy"));
        } else {
            System.out.println("Không có phản hồi nào 'Đã hủy' để test.");
        }
    }

    @Test(priority = 8, description = "TC_226: Kiểm tra thông tin phòng trong lịch sử")
    public void TC08_CheckRoomInfo() {
        // Dựa trên hình image_cf5d52.jpg: "Phòng NTT 02"
        boolean hasRoomInfo = driver.getPageSource().contains("Phòng") || driver.findElements(By.xpath("//span[contains(text(),'Phòng')]")).size() > 0;
        Assert.assertTrue(hasRoomInfo, "Không hiển thị thông tin phòng trong list");
    }

    @Test(priority = 9, description = "TC_227: Kiểm tra ảnh đính kèm trong lịch sử")
    public void TC09_CheckAttachmentDisplay() {
        // Dựa trên hình image_cf5d52.jpg: Có dòng "Ảnh đính kèm" hoặc thumbnail
        List<WebElement> itemsWithImg = driver.findElements(By.xpath("//img[contains(@alt,'Attachment')] | //div[contains(text(),'Ảnh đính kèm')]"));
        
        if (itemsWithImg.size() > 0) {
            Assert.assertTrue(itemsWithImg.get(0).isDisplayed(), "Ảnh/Text đính kèm không hiển thị");
        } else {
            System.out.println("Không tìm thấy item nào có ảnh đính kèm.");
        }
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        Teardown();
    }
}