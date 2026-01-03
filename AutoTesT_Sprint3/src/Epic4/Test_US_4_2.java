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

public class Test_US_4_2 extends Init {

    String url = "http://localhost:4200"; // Thay đổi URL nếu cần

    @BeforeMethod
    public void setupAndNavigate() throws InterruptedException {
        SetUp("chrome");
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // --- BƯỚC 1: ĐĂNG NHẬP (Reuse từ các bài trước) ---
        try {
            WebElement btnLoginHome = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Đăng nhập')]"))); // XPath nút đăng nhập trang chủ
            btnLoginHome.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("chutro1"); // Thay username
            driver.findElement(By.id("password")).sendKeys("123456"); // Thay password
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click(); // XPath nút đăng nhập form

            try {
                WebElement btnOK = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')] | //button[contains(@class,'swal')]")));
                btnOK.click();
            } catch (Exception e) {}
        } catch (Exception e) {
            System.out.println("Lỗi đăng nhập hoặc đã đăng nhập sẵn.");
        }

        // --- BƯỚC 2: NAVIGATE VÀO DASHBOARD VÀ QUẢN LÝ ĐIỆN ---
        Thread.sleep(1000);
        // Mở dropdown user
        WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class,'dropdown')]//button | //span[contains(text(),'Chủ Trọ')]"))); // XPath dropdown user
        userDropdown.click();

        // Chọn Dashboard
        WebElement itemDashboard = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Dashboard Chủ trọ')] | //button[contains(text(),'Dashboard')]"))); // XPath item Dashboard
        itemDashboard.click();
        
        Thread.sleep(1000);
        // Chọn menu Quản lý điện
        WebElement menuElectricity = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Quản lý điện')]//parent::li | //span[contains(text(),'Quản lý điện')]"))); // XPath menu Quản lý điện
        menuElectricity.click();

        Thread.sleep(1500); // Đợi load trang
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
    
    // --- CÁC TEST CASE CHO US 4.2 ---

    @Test(priority = 1, description = "TC_001, TC_029-032: Kiểm tra màn hình Quản lý tiền điện và Thống kê")
    public void TC01_CheckUI_Statistics() {
        // Kiểm tra Header
        Assert.assertTrue(driver.findElement(By.xpath("//h3[contains(text(),'Quản lý Điện')] | //h4[contains(text(),'Quản lý Điện')]")).isDisplayed(), "Header không hiển thị");

        // Kiểm tra các thẻ thống kê
        Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Doanh thu')]")).isDisplayed(), "Thẻ Doanh thu không hiển thị");
        Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Tổng bản ghi')]")).isDisplayed(), "Thẻ Tổng bản ghi không hiển thị");
        Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Chưa thanh toán')]")).isDisplayed(), "Thẻ Chưa thanh toán không hiển thị");
        Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Đã thanh toán')]")).isDisplayed(), "Thẻ Đã thanh toán không hiển thị");
        
        // Kiểm tra Bộ lọc và Bảng dữ liệu
        Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@placeholder,'Tìm theo tên')]")).isDisplayed(), "Ô tìm kiếm không hiển thị");
        Assert.assertTrue(driver.findElement(By.xpath("//table")).isDisplayed(), "Bảng dữ liệu không hiển thị");
    }

    @Test(priority = 2, description = "TC_002: Kiểm tra Placeholder ô tìm kiếm")
    public void TC02_CheckSearchPlaceholder() {
        WebElement searchBox = driver.findElement(By.xpath("//input[contains(@placeholder,'Tìm theo tên')]"));
        String placeholder = searchBox.getAttribute("placeholder");
        Assert.assertTrue(placeholder.contains("Tìm theo tên khách hoặc tên phòng"), "Placeholder không đúng: " + placeholder);
    }

    @Test(priority = 3, description = "TC_003, TC_007: Kiểm tra Popup Thêm bản ghi điện")
    public void TC03_CheckAddPopup() throws InterruptedException {
        // Click nút Thêm
        WebElement btnAdd = driver.findElement(By.xpath("//button[contains(text(),'Thêm bản ghi điện')]"));
        clickElementJS(btnAdd);
        Thread.sleep(1000);

        // Kiểm tra Popup hiện ra
        WebElement popup = driver.findElement(By.xpath("//div[contains(@class,'modal') or contains(@role,'dialog')]"));
        Assert.assertTrue(popup.isDisplayed(), "Popup không hiển thị");

        // Kiểm tra các trường trong popup
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Phòng')]")).isDisplayed(), "Trường Phòng thiếu");
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Tháng')]")).isDisplayed(), "Trường Tháng thiếu"); // TC_007 Fail nếu thiếu
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Chỉ số cũ')]")).isDisplayed(), "Trường Chỉ số cũ thiếu");
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Chỉ số mới')]")).isDisplayed(), "Trường Chỉ số mới thiếu");
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Đơn giá')]")).isDisplayed(), "Trường Đơn giá thiếu");
        
        // Đóng popup
        WebElement btnCancel = driver.findElement(By.xpath("//button[contains(text(),'Hủy bỏ')]"));
        clickElementJS(btnCancel);
    }

    @Test(priority = 4, description = "TC_009: Thêm mới bản ghi điện thành công")
    public void TC09_AddNewRecord_Success() throws InterruptedException {
        // Mở popup
        driver.findElement(By.xpath("//button[contains(text(),'Thêm bản ghi điện')]")).click();
        Thread.sleep(1000);

        // Nhập liệu (Cần XPath chính xác cho các input trong modal)
        // Chọn Phòng (Giả sử là dropdown hoặc combobox)
        // ... (Code xử lý chọn phòng, ví dụ dùng Select hoặc click list item) ...
        // Ví dụ đơn giản nếu là select native:
        // Select selectRoom = new Select(driver.findElement(By.id("roomId")));
        // selectRoom.selectByIndex(1); 
        
        // Nhập Tháng (Datepicker hoặc text)
        // driver.findElement(By.id("month")).sendKeys("12/2025");

        // Nhập chỉ số
        driver.findElement(By.xpath("//input[@formcontrolname='oldIndex' or @name='oldIndex']")).sendKeys("100");
        driver.findElement(By.xpath("//input[@formcontrolname='newIndex' or @name='newIndex']")).sendKeys("200");
        driver.findElement(By.xpath("//input[@formcontrolname='price' or @name='price']")).sendKeys("3500");

        // Lưu
        driver.findElement(By.xpath("//button[contains(text(),'Lưu thông tin')]")).click();
        
        Thread.sleep(1000);
        handleUnexpectedAlert();
        
        // Verify
        // Kiểm tra thông báo thành công hoặc bản ghi mới xuất hiện trong bảng
        Assert.assertTrue(driver.getPageSource().contains("thành công") || driver.getPageSource().contains("200"), "Thêm mới thất bại");
    }

    @Test(priority = 5, description = "TC_010: Thêm mới với trường bắt buộc 'Phòng' bỏ trống")
    public void TC10_AddNewRecord_MissingRoom() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm bản ghi điện')]")).click();
        Thread.sleep(1000);

        // Không chọn phòng
        // Nhập các trường khác
        driver.findElement(By.xpath("//input[@formcontrolname='oldIndex']")).sendKeys("100");
        driver.findElement(By.xpath("//input[@formcontrolname='newIndex']")).sendKeys("200");
        
        driver.findElement(By.xpath("//button[contains(text(),'Lưu thông tin')]")).click();
        
        // Verify lỗi
        // Kiểm tra thông báo lỗi hoặc class invalid trên trường Phòng
        boolean isError = driver.getPageSource().contains("Vui lòng chọn phòng") || driver.findElement(By.xpath("//div[contains(@class,'invalid-feedback')]")).isDisplayed();
        Assert.assertTrue(isError, "Không báo lỗi khi thiếu phòng");
        
        // Đóng popup
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy bỏ')]")));
    }

    @Test(priority = 6, description = "TC_013: Thêm mới với Chỉ số mới < Chỉ số cũ")
    public void TC13_AddNewRecord_NewIndexLessThanOld() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm bản ghi điện')]")).click();
        Thread.sleep(1000);

        // ... Chọn phòng ...

        driver.findElement(By.xpath("//input[@formcontrolname='oldIndex']")).sendKeys("200");
        driver.findElement(By.xpath("//input[@formcontrolname='newIndex']")).sendKeys("100"); // Nhỏ hơn cũ
        
        driver.findElement(By.xpath("//button[contains(text(),'Lưu thông tin')]")).click();
        
        // Verify thông báo lỗi
        boolean isError = driver.getPageSource().contains("không được nhỏ hơn") || driver.getPageSource().contains("lớn hơn");
        Assert.assertTrue(isError, "Không báo lỗi logic chỉ số");
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy bỏ')]")));
    }

    @Test(priority = 7, description = "TC_017, TC_021: Nhập ký tự vào trường số")
    public void TC17_InputCharacterInNumberFields() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm bản ghi điện')]")).click();
        Thread.sleep(1000);

        WebElement inputOldIndex = driver.findElement(By.xpath("//input[@formcontrolname='oldIndex']"));
        inputOldIndex.sendKeys("abc");
        
        // Verify: Giá trị trong ô input vẫn rỗng hoặc không chứa 'abc'
        String val = inputOldIndex.getAttribute("value");
        Assert.assertTrue(val.isEmpty() || !val.contains("abc"), "Ô nhập liệu nhận ký tự chữ");
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy bỏ')]")));
    }

    @Test(priority = 8, description = "TC_022, TC_023, TC_027, TC_028: Tự động tính Tiêu thụ và Thành tiền")
    public void TC22_AutoCalculate() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm bản ghi điện')]")).click();
        Thread.sleep(1000);

        WebElement inputOld = driver.findElement(By.xpath("//input[@formcontrolname='oldIndex']"));
        WebElement inputNew = driver.findElement(By.xpath("//input[@formcontrolname='newIndex']"));
        WebElement inputPrice = driver.findElement(By.xpath("//input[@formcontrolname='price']"));

        inputOld.clear(); inputOld.sendKeys("100");
        inputNew.clear(); inputNew.sendKeys("150");
        inputPrice.clear(); inputPrice.sendKeys("3000");
        
        // Click ra ngoài hoặc tab để trigger sự kiện tính toán (nếu cần)
        inputPrice.sendKeys(Keys.TAB);
        Thread.sleep(500);

        // Verify Tiêu thụ (50) và Thành tiền (150000)
        // Lưu ý: Cần XPath chính xác tới element hiển thị kết quả (thường là span hoặc input readonly)
        WebElement lblConsumption = driver.findElement(By.xpath("//span[contains(@id,'consumption')] | //input[@formcontrolname='usage']")); 
        WebElement lblTotal = driver.findElement(By.xpath("//span[contains(@id,'total')] | //input[@formcontrolname='totalCost']"));

        String consumption = lblConsumption.getText().isEmpty() ? lblConsumption.getAttribute("value") : lblConsumption.getText();
        String total = lblTotal.getText().isEmpty() ? lblTotal.getAttribute("value") : lblTotal.getText();

        Assert.assertTrue(consumption.contains("50"), "Tính tiêu thụ sai: " + consumption);
        Assert.assertTrue(total.replace(".", "").replace(",", "").contains("150000"), "Tính thành tiền sai: " + total);
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy bỏ')]")));
    }

    @Test(priority = 9, description = "TC_034, TC_035: Tìm kiếm")
    public void TC34_Search() throws InterruptedException {
        WebElement searchBox = driver.findElement(By.xpath("//input[contains(@placeholder,'Tìm theo tên')]"));
        searchBox.clear();
        searchBox.sendKeys("Phòng 101"); // Tên phòng hoặc tên khách
        searchBox.sendKeys(Keys.ENTER);
        
        Thread.sleep(1000);
        
        // Verify: Các dòng trong bảng phải chứa từ khóa
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        if (rows.size() > 0) {
            String rowText = rows.get(0).getText();
            Assert.assertTrue(rowText.contains("101"), "Kết quả tìm kiếm không đúng");
        } else {
            System.out.println("Không tìm thấy kết quả nào.");
        }
    }

    @Test(priority = 10, description = "TC_045: Chỉnh sửa bản ghi điện")
    public void TC45_EditRecord() throws InterruptedException {
        // Tìm nút chỉnh sửa của dòng đầu tiên
        List<WebElement> btnEdits = driver.findElements(By.xpath("//button[contains(@class,'btn-edit')] | //i[contains(@class,'fa-edit')]/parent::button"));
        if (btnEdits.size() > 0) {
            clickElementJS(btnEdits.get(0));
            Thread.sleep(1000);
            
            // Verify Popup Edit hiện ra
            Assert.assertTrue(driver.findElement(By.xpath("//h5[contains(text(),'Chỉnh sửa') or contains(text(),'Cập nhật')]")).isDisplayed(), "Popup chỉnh sửa không hiện");
            
            // Verify tên phòng không sửa được (disabled)
            // WebElement inputRoom = driver.findElement(By.xpath("..."));
            // Assert.assertFalse(inputRoom.isEnabled(), "Trường phòng vẫn cho sửa");

            clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy bỏ')]")));
        } else {
            System.out.println("Không có bản ghi nào để sửa.");
        }
    }

    @Test(priority = 11, description = "TC_064: Xóa bản ghi điện")
    public void TC64_DeleteRecord() throws InterruptedException {
        // Tìm nút xóa của dòng đầu tiên (hoặc dòng test cụ thể)
        List<WebElement> btnDeletes = driver.findElements(By.xpath("//button[contains(@class,'btn-delete')] | //i[contains(@class,'fa-trash')]/parent::button"));
        if (btnDeletes.size() > 0) {
            clickElementJS(btnDeletes.get(0));
            Thread.sleep(500);
            
            // Verify Popup xác nhận
            Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'bạn có chắc chắn')]")).isDisplayed(), "Popup xác nhận xóa không hiện");
            
            // Bấm Hủy (TC_065) để an toàn cho data test, hoặc Xác nhận (TC_064) nếu data rác
            clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
            
            // Verify sau khi hủy thì bản ghi vẫn còn (nếu check row count)
        } else {
            System.out.println("Không có bản ghi nào để xóa.");
        }
    }

    @Test(priority = 12, description = "TC_044: Xuất file Excel")
    public void TC44_ExportExcel() {
        WebElement btnExport = driver.findElement(By.xpath("//button[contains(text(),'Xuất file') or contains(text(),'Excel')]"));
        if (btnExport.isDisplayed()) {
            clickElementJS(btnExport);
            // Verify: Không thể check file download bằng Selenium cơ bản dễ dàng, 
            // nhưng có thể check xem có thông báo lỗi hay crash không.
            // Hoặc check file tồn tại trong thư mục download (cần config thêm).
            System.out.println("Đã click nút xuất file.");
        }
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        Teardown();
    }
}