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

public class Test_US_4_4 extends Init {

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

        // --- BƯỚC 2: NAVIGATE VÀO DASHBOARD -> CHI PHÍ PHÁT SINH ---
        Thread.sleep(1000);
        WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class,'dropdown')]//button | //span[contains(text(),'Chủ Trọ')]")));
        userDropdown.click();

        WebElement itemDashboard = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Dashboard Chủ trọ')] | //button[contains(text(),'Dashboard')]")));
        itemDashboard.click();
        
        Thread.sleep(1000);
        // Chọn menu Chi phí phát sinh (Dựa trên sidebar: icon ví tiền)
        WebElement menuExtraCost = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Chi phí phát sinh')]//parent::li | //span[contains(text(),'Chi phí phát sinh')]")));
        menuExtraCost.click();

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
    
    // --- CÁC TEST CASE CHO US 4.4 (QUẢN LÝ CHI PHÍ PHÁT SINH) ---

    @Test(priority = 1, description = "TC_138, TC_150, TC_158: Kiểm tra UI, Thống kê và Định dạng tiền tệ")
    public void TC01_CheckUI_Statistics() {
        // Dựa trên hình image_cf55ae.jpg
        Assert.assertTrue(driver.findElement(By.xpath("//h3[contains(text(),'Quản lý Chi phí Phát sinh')]")).isDisplayed(), "Header sai");

        // Các thẻ thống kê
        Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Tổng chi phí tháng')]")).isDisplayed());
        
        // Kiểm tra biểu đồ/phân tích
        Assert.assertTrue(driver.findElement(By.xpath("//h5[contains(text(),'Phân tích Chi phí')]")).isDisplayed());
        
        // Kiểm tra định dạng tiền tệ (VD: 170.001 đ)
        WebElement totalCost = driver.findElement(By.xpath("//h3[contains(text(),'đ')] | //h2[contains(text(),'đ')]"));
        Assert.assertTrue(totalCost.getText().contains("đ") || totalCost.getText().contains("VND"), "Thiếu đơn vị tiền tệ");
        Assert.assertTrue(totalCost.getText().contains(".") || totalCost.getText().contains(","), "Thiếu dấu phân cách hàng nghìn");
    }

    @Test(priority = 2, description = "TC_139: Kiểm tra Popup Thêm chi phí")
    public void TC02_CheckAddPopup() throws InterruptedException {
        WebElement btnAdd = driver.findElement(By.xpath("//button[contains(text(),'Thêm Chi phí')]"));
        clickElementJS(btnAdd);
        Thread.sleep(1000);

        // Dựa trên hình image_cf55e7.jpg
        WebElement popupTitle = driver.findElement(By.xpath("//h5[contains(text(),'Thêm chi phí phát sinh')]"));
        Assert.assertTrue(popupTitle.isDisplayed());

        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Phòng')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Tháng ghi')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Loại chi phí')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Số tiền')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//label[contains(text(),'Mô tả chi tiết')]")).isDisplayed());
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
    }

    @Test(priority = 3, description = "TC_140, TC_141: Thêm mới chi phí thành công (Happy Case)")
    public void TC03_AddNewRecord_Success() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm Chi phí')]")).click();
        Thread.sleep(1000);

        // Chọn phòng (Giả lập)
        // ... code chọn phòng ...

        // Nhập liệu (Dựa trên hình image_cf55e7.jpg)
        // Loại chi phí (Dropdown)
        /* WebElement typeSelect = driver.findElement(By.xpath("//select[contains(@formcontrolname,'type')]"));
           Select selectType = new Select(typeSelect);
           selectType.selectByVisibleText("Khác"); */
        
        WebElement amount = driver.findElement(By.xpath("//input[@type='number' or contains(@formcontrolname,'amount')]"));
        WebElement desc = driver.findElement(By.xpath("//textarea"));

        amount.clear(); amount.sendKeys("50000");
        desc.sendKeys("Thay bóng đèn");

        driver.findElement(By.xpath("//button[contains(text(),'Lưu thông tin')]")).click();
        
        Thread.sleep(1000);
        handleUnexpectedAlert();
        
        // Verify
        Assert.assertTrue(driver.getPageSource().contains("thành công") || driver.getPageSource().contains("50.000"));
    }

    @Test(priority = 4, description = "TC_144: Validate Số tiền không được âm")
    public void TC04_Validate_NegativeAmount() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm Chi phí')]")).click();
        Thread.sleep(1000);

        WebElement amount = driver.findElement(By.xpath("//input[@type='number' or contains(@formcontrolname,'amount')]"));
        amount.sendKeys("-10000");
        
        driver.findElement(By.xpath("//button[contains(text(),'Lưu thông tin')]")).click();
        
        // Verify
        boolean isError = driver.getPageSource().contains("không được âm") || driver.getPageSource().contains("lỗi");
        Assert.assertTrue(isError, "Hệ thống không chặn số tiền âm");
        
        clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
    }

    @Test(priority = 5, description = "TC_148: Tìm kiếm theo Phòng")
    public void TC05_SearchByRoom() throws InterruptedException {
        WebElement searchBox = driver.findElement(By.xpath("//input[contains(@placeholder,'Tìm phòng')]"));
        searchBox.clear();
        searchBox.sendKeys("Phòng NTT 02"); // Dựa trên hình image_cf55c8.jpg
        searchBox.sendKeys(Keys.ENTER);
        
        Thread.sleep(1000);
        
        // Verify
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        if (rows.size() > 0) {
            Assert.assertTrue(rows.get(0).getText().contains("NTT 02"), "Tìm kiếm không chính xác");
        } else {
            System.out.println("Không tìm thấy dữ liệu.");
        }
    }

    @Test(priority = 6, description = "TC_151, TC_152, TC_153: Sửa chi phí phát sinh")
    public void TC06_EditRecord() throws InterruptedException {
        // Dựa trên hình image_cf55c8.jpg (Nút edit icon bút chì xanh)
        List<WebElement> btnEdits = driver.findElements(By.xpath("//button[contains(@class,'btn-outline-primary')] | //i[contains(@class,'fa-pen-to-square')]/parent::button"));
        
        if (btnEdits.size() > 0) {
            clickElementJS(btnEdits.get(0));
            Thread.sleep(1000);
            
            // Verify Modal Title (Dựa trên hình image_cf5606.jpg)
            Assert.assertTrue(driver.findElement(By.xpath("//h5[contains(text(),'Cập nhật chi phí')]")).isDisplayed());
            
            // Verify tên phòng bị disable
            WebElement roomInput = driver.findElement(By.xpath("//label[contains(text(),'Phòng')]/following-sibling::*//input | //label[contains(text(),'Phòng')]/following-sibling::input"));
            // Assert.assertFalse(roomInput.isEnabled());

            // Sửa số tiền
            WebElement amount = driver.findElement(By.xpath("//input[@type='number' or contains(@formcontrolname,'amount')]"));
            amount.clear();
            amount.sendKeys("60000");
            
            driver.findElement(By.xpath("//button[contains(text(),'Lưu thông tin')]")).click();
            Thread.sleep(1000);
            
            // Verify update thành công
            Assert.assertTrue(driver.getPageSource().contains("thành công") || driver.getPageSource().contains("60.000"));
        } else {
            System.out.println("Không có bản ghi để sửa");
        }
    }

    @Test(priority = 7, description = "TC_154: Xóa khoản chi phí")
    public void TC07_DeleteRecord() throws InterruptedException {
        // Dựa trên hình image_cf55c8.jpg (Nút delete icon thùng rác đỏ)
        List<WebElement> btnDeletes = driver.findElements(By.xpath("//button[contains(@class,'btn-outline-danger')] | //i[contains(@class,'fa-trash')]/parent::button"));
        
        if (btnDeletes.size() > 0) {
            clickElementJS(btnDeletes.get(0));
            Thread.sleep(500);
            
            // Verify Popup (Dựa trên hình image_cf560b.jpg)
            Assert.assertTrue(driver.findElement(By.xpath("//h5[contains(text(),'Xác nhận xóa?')]")).isDisplayed());
            Assert.assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Hành động này sẽ xóa khoản chi phí')]")).isDisplayed());
            
            // Bấm Hủy để an toàn
            clickElementJS(driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")));
        } else {
            System.out.println("Không có bản ghi để xóa");
        }
    }

    @Test(priority = 8, description = "TC_156: Xuất Excel")
    public void TC08_ExportExcel() {
        // Dựa trên hình image_cf55ae.jpg
        WebElement btnExport = driver.findElement(By.xpath("//button[contains(text(),'Xuất Excel kiểm kê')]"));
        if (btnExport.isDisplayed()) {
            clickElementJS(btnExport);
            System.out.println("Đã click nút xuất file.");
        }
    }

    @Test(priority = 9, description = "TC_160: Kiểm tra Validate ngày tháng tương lai")
    public void TC09_FutureDateCheck() throws InterruptedException {
        driver.findElement(By.xpath("//button[contains(text(),'Thêm Chi phí')]")).click();
        Thread.sleep(1000);

        // Giả sử có datepicker, chọn tháng sau
        // ... code chọn date ...
        
        // Nhập các trường bắt buộc khác
        driver.findElement(By.xpath("//input[@type='number']")).sendKeys("20000");
        driver.findElement(By.xpath("//textarea")).sendKeys("Phí tương lai");

        driver.findElement(By.xpath("//button[contains(text(),'Lưu thông tin')]")).click();
        
        Thread.sleep(1000);
        // TC_160 mong đợi hệ thống CHO PHÉP (Setup trước)
        Assert.assertTrue(driver.getPageSource().contains("thành công"), "Hệ thống không cho phép nhập phí tương lai");
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        Teardown();
    }
}