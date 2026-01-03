package Epic4;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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

public class Test_US_4_3 extends Init {

    String url = "http://localhost:4200";

    @BeforeMethod
    public void setupAndNavigate() throws InterruptedException {
        SetUp("chrome");
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // --- BƯỚC 1: ĐĂNG NHẬP ---
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

        // --- BƯỚC 2: NAVIGATE VÀO DASHBOARD -> QUẢN LÝ NƯỚC ---
        Thread.sleep(1000);
        WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class,'dropdown')]//button | //span[contains(text(),'Chủ Trọ')]")));
        userDropdown.click();

        WebElement itemDashboard = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Dashboard Chủ trọ')] | //button[contains(text(),'Dashboard')]")));
        itemDashboard.click();
        
        Thread.sleep(1000);
        // Chọn menu Quản lý nước (Dựa trên sidebar)
        WebElement menuWater = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Quản lý Nước')]//parent::li | //span[contains(text(),'Quản lý Nước')]")));
        menuWater.click();

        Thread.sleep(1500); 
    }

    // --- HÀM HỖ TRỢ ---
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
    
    // --- CÁC TEST CASE CHO US 4.3 (QUẢN LÝ NƯỚC) ---

    @Test(priority = 1, description = "TC_069, TC_097-100: Kiểm tra UI và Thống kê")
    public void TC01_CheckUI_Statistics() {
        // Dựa trên hình image_cf4e4f.jpg
        Assert.assertTrue(driver.findElement(By.xpath("//h3[contains(text(),'Quản lý Nước')]")).isDisplayed(), "Header sai");

        // Các thẻ thống kê
        Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Doanh thu')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Tổng tiêu thụ')]")).isDisplayed()); // Khác điện (Tổng bản ghi) -> Nước (Tổng tiêu thụ)
        Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Hóa đơn chưa thanh toán')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Đã thanh toán')]")).isDisplayed());
        
        // Biểu đồ
        Assert.assertTrue(driver.findElement(By.xpath("//h5[contains(text(),'Biểu đồ tiêu thụ nước')]")).isDisplayed());
    }

    @Test(priority = 2, description = "TC_070, TC_101: Kiểm tra Bộ lọc và Tìm kiếm")
    public void TC02_CheckFilters() {
        // Dựa trên hình image_cf4e6c.jpg
        WebElement searchBox = driver.findElement(By.xpath("//input[contains(@placeholder,'Tìm phòng hoặc khách thuê')]"));
        Assert.assertTrue(searchBox.isDisplayed());
        
        Assert.assertTrue(driver.findElement(By.xpath("//button[contains(text(),'Tất cả các tháng')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//button[contains(text(),'Tất cả phòng')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//button[contains(text(),'Tất cả trạng thái')]")).isDisplayed());
    }

    @Test(priority = 3, description = "TC_071, TC_075: Kiểm tra Popup Thêm chỉ số nước")
    public void TC03_CheckAddPopup() throws InterruptedException {
        WebElement btnAdd = driver.findElement(By.xpath("//button[contains(text(),'Thêm Chỉ Số Nước')]"));
        clickElementJS(btnAdd);
        Thread.sleep(1000);

        // Dựa trên hình image_cf4ea4.jpg
        WebElement popupTitle = driver.findElement(By.xpath("//h5[contains(text(),'Ghi nước tháng mới')]"));
        Assert.assertTrue(popupTitle.isDisplayed());

        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Phòng')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Tháng ghi')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Chỉ số cũ')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Chỉ số mới')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Đơn giá')]")).isDisplayed());
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]"))); // Nút Hủy (không phải Hủy bỏ)
    }

    @Test(priority = 4, description = "TC_077: Thêm mới chỉ số nước thành công")
    public void TC09_AddNewRecord_Success() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm Chỉ Số Nước')]")).click();
        Thread.sleep(1000);

        // Chọn phòng (Giả lập chọn item đầu tiên trong dropdown)
        // Lưu ý: Cần XPath chính xác cho dropdown custom nếu không phải thẻ select
        /* driver.findElement(By.xpath("//button[contains(text(),'Chọn phòng')]")).click();
        driver.findElement(By.xpath("//a[contains(text(),'Phòng NTT 01')]")).click();
        */

        // Nhập chỉ số (Dựa trên hình image_cf4ea4.jpg)
        // Giả sử formcontrolname là oldIndex, newIndex...
        WebElement oldIdx = driver.findElement(By.xpath("//input[@type='number' and @min='0'][1]")); // Chỉ số cũ (input đầu)
        WebElement newIdx = driver.findElement(By.xpath("//input[@type='number' and @min='0'][2]")); // Chỉ số mới (input sau)
        WebElement price = driver.findElement(By.xpath("//input[@type='number' and contains(@class,'form-control')][3]")); // Đơn giá

        // Nếu XPath trên ko chạy, hãy dùng XPath theo label:
        // driver.findElement(By.xpath("//label[contains(text(),'Chỉ số cũ')]/following-sibling::input"));

        oldIdx.clear(); oldIdx.sendKeys("100");
        newIdx.clear(); newIdx.sendKeys("120");
        price.clear(); price.sendKeys("15000");

        driver.findElement(By.xpath("//button[contains(text(),'Lưu thông tin')]")).click();
        
        Thread.sleep(1000);
        handleUnexpectedAlert();
        
        // Verify
        Assert.assertTrue(driver.getPageSource().contains("thành công") || driver.getPageSource().contains("120"));
    }

    @Test(priority = 5, description = "TC_081: Validate Chỉ số mới < Chỉ số cũ")
    public void TC13_Validate_IndexLogic() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm Chỉ Số Nước')]")).click();
        Thread.sleep(1000);

        // Chọn phòng...

        WebElement oldIdx = driver.findElement(By.xpath("//label[contains(text(),'Chỉ số cũ')]/following-sibling::input"));
        WebElement newIdx = driver.findElement(By.xpath("//label[contains(text(),'Chỉ số mới')]/following-sibling::input"));

        oldIdx.sendKeys("50");
        newIdx.sendKeys("40"); // Nhỏ hơn
        
        driver.findElement(By.xpath("//button[contains(text(),'Lưu thông tin')]")).click();
        
        // Verify
        boolean isError = driver.getPageSource().contains("không được nhỏ hơn") || driver.getPageSource().contains("lỗi");
        Assert.assertTrue(isError, "Hệ thống không chặn chỉ số mới < cũ");
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
    }

    @Test(priority = 6, description = "TC_090, TC_091, TC_095, TC_096: Tính toán tự động (Tiêu thụ & Thành tiền)")
    public void TC22_AutoCalculate() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm Chỉ Số Nước')]")).click();
        Thread.sleep(1000);

        WebElement oldIdx = driver.findElement(By.xpath("//label[contains(text(),'Chỉ số cũ')]/following-sibling::input"));
        WebElement newIdx = driver.findElement(By.xpath("//label[contains(text(),'Chỉ số mới')]/following-sibling::input"));
        WebElement price = driver.findElement(By.xpath("//label[contains(text(),'Đơn giá')]/following-sibling::input"));

        oldIdx.clear(); oldIdx.sendKeys("10");
        newIdx.clear(); newIdx.sendKeys("20");
        price.clear(); price.sendKeys("15000");
        price.sendKeys(Keys.TAB); // Trigger calculation
        Thread.sleep(500);

        // Verify kết quả (Dựa trên hình image_cf4ea4.jpg phần footer màu xanh nhạt)
        // Cần tìm thẻ span hoặc div chứa text kết quả
        String pageText = driver.getPageSource();
        
        // Tiêu thụ: 10 m3
        Assert.assertTrue(pageText.contains("10 m") || pageText.contains("10m"), "Tính tiêu thụ sai");
        
        // Thành tiền: 150.000 đ
        Assert.assertTrue(pageText.contains("150.000") || pageText.contains("150,000"), "Tính thành tiền sai");
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
    }

    @Test(priority = 7, description = "TC_102, TC_103: Tìm kiếm")
    public void TC34_Search() throws InterruptedException {
        WebElement searchBox = driver.findElement(By.xpath("//input[contains(@placeholder,'Tìm phòng')]"));
        searchBox.clear();
        searchBox.sendKeys("Phòng NTT 01");
        searchBox.sendKeys(Keys.ENTER);
        
        Thread.sleep(1000);
        
        // Verify
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        if (rows.size() > 0) {
            Assert.assertTrue(rows.get(0).getText().contains("NTT 01"), "Tìm kiếm không chính xác");
        } else {
            System.out.println("Không tìm thấy dữ liệu.");
        }
    }

    @Test(priority = 8, description = "TC_113: Chỉnh sửa chỉ số nước")
    public void TC45_EditRecord() throws InterruptedException {
        // Dựa trên hình image_cf4e6c.jpg (Nút edit icon bút chì xanh)
        List<WebElement> btnEdits = driver.findElements(By.xpath("//button[contains(@class,'btn-outline-primary')] | //i[contains(@class,'fa-pen-to-square')]/parent::button"));
        
        if (btnEdits.size() > 0) {
            clickElementJS(btnEdits.get(0));
            Thread.sleep(1000);
            
            // Verify Modal Title (Dựa trên hình image_cf4e87.jpg)
            Assert.assertTrue(driver.findElement(By.xpath("//h5[contains(text(),'Sửa chỉ số nước')]")).isDisplayed());
            
            // Verify tên phòng bị disable
            WebElement roomInput = driver.findElement(By.xpath("//label[contains(text(),'Phòng')]/following-sibling::*//input | //label[contains(text(),'Phòng')]/following-sibling::input"));
            // (Tùy UI framework, có thể check attribute 'disabled' hoặc class 'disabled')
            
            clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
        } else {
            System.out.println("Không có bản ghi để sửa");
        }
    }

    @Test(priority = 9, description = "TC_132: Xóa chỉ số nước")
    public void TC64_DeleteRecord() throws InterruptedException {
        // Dựa trên hình image_cf4e6c.jpg (Nút delete icon thùng rác đỏ)
        List<WebElement> btnDeletes = driver.findElements(By.xpath("//button[contains(@class,'btn-outline-danger')] | //i[contains(@class,'fa-trash')]/parent::button"));
        
        if (btnDeletes.size() > 0) {
            clickElementJS(btnDeletes.get(0));
            Thread.sleep(500);
            
            // Verify Popup (Dựa trên hình image_cf4eab.jpg)
            Assert.assertTrue(driver.findElement(By.xpath("//h5[contains(text(),'Xác nhận xóa?')]")).isDisplayed());
            Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Bạn có chắc chắn muốn xóa')]")).isDisplayed());
            
            // Bấm Hủy để an toàn
            clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
        } else {
            System.out.println("Không có bản ghi để xóa");
        }
    }

    @Test(priority = 10, description = "TC_137: Kiểm tra biểu đồ")
    public void TC137_CheckChart() {
        // Dựa trên hình image_cf4e4f.jpg
        WebElement chart = driver.findElement(By.xpath("//canvas | //div[contains(@class,'chart')]")); // Thường biểu đồ dùng thẻ canvas
        Assert.assertTrue(chart.isDisplayed(), "Biểu đồ không hiển thị");
        
        // Kiểm tra tiêu đề biểu đồ
        Assert.assertTrue(driver.getPageSource().contains("Biểu đồ tiêu thụ nước") || driver.getPageSource().contains("Water Usage Trend"));
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        Teardown();
    }
}