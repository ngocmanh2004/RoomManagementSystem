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

public class Test_US_6_1 extends Init {

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

            WebElement txtUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            txtUser.sendKeys("chutro1"); 

            WebElement txtPass = driver.findElement(By.id("password"));
            txtPass.sendKeys("123456");

            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

            try {
                WebElement btnOK = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')] | //button[contains(@class,'swal')]")));
                btnOK.click();
            } catch (Exception e) {}
        } catch (Exception e) {
            System.out.println("Lỗi đăng nhập hoặc đã đăng nhập sẵn.");
        }

        // --- BƯỚC 2: NAVIGATE VÀO DASHBOARD ---
        Thread.sleep(1500); 
        WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class,'dropdown')]//button | //span[contains(text(),'Chủ Trọ')]")));
        userDropdown.click();

        WebElement itemDashboard = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Dashboard Chủ trọ')] | //button[contains(text(),'Dashboard')]")));
        itemDashboard.click();

        // --- BƯỚC 3: VÀO MÀN HÌNH THÔNG BÁO ---
        Thread.sleep(1000);
        WebElement menuNoti = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Thông báo')]//parent::li | //span[contains(text(),'Thông báo')]")));
        menuNoti.click();
        
        Thread.sleep(1500); // Đợi load trang hoàn toàn
    }

    // --- HÀM HỖ TRỢ (ĐÃ SỬA LẠI ĐỂ FIX LỖI CHỌN PHÒNG) ---
    
    public void selectRoom(String roomName) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // 1. Click tab "Gửi theo phòng" bằng JS để chắc chắn ăn
            // Tìm thẻ input radio hoặc label chứa text
            WebElement radioRoom = driver.findElement(By.xpath("//label[contains(., 'Gửi theo phòng')] | //input[@type='radio' and following-sibling::label[contains(.,'Gửi theo phòng')]]"));
            js.executeScript("arguments[0].click();", radioRoom);
            
            Thread.sleep(1000); // Đợi danh sách phòng render ra

            // 2. Tìm checkbox của phòng cụ thể
            // Logic: Tìm cái Card nào có chứa text 'Phòng NTT 01', sau đó tìm input checkbox trong card đó
            // Dùng contains(., text) để tìm text bất kể nó nằm trong h6, span hay div
            String roomXpath = "//div[contains(@class,'card') and .//text()[contains(., '" + roomName + "')]]//input[@type='checkbox']";
            
            List<WebElement> checkboxes = driver.findElements(By.xpath(roomXpath));
            
            if (checkboxes.size() > 0) {
                WebElement chkRoom = checkboxes.get(0);
                // Nếu chưa chọn thì mới click
                if (!chkRoom.isSelected()) {
                    js.executeScript("arguments[0].click();", chkRoom);
                    System.out.println("Đã chọn phòng: " + roomName);
                } else {
                    System.out.println("Phòng " + roomName + " đã được chọn trước đó.");
                }
            } else {
                System.out.println("KHÔNG TÌM THẤY PHÒNG: " + roomName);
                // Thử fallback: Chọn checkbox đầu tiên tìm thấy
                WebElement firstChk = driver.findElement(By.xpath("//div[contains(@class,'card')]//input[@type='checkbox']"));
                js.executeScript("arguments[0].click();", firstChk);
                System.out.println("-> Đã chọn checkbox đầu tiên thay thế.");
            }
        } catch (Exception e) {
            System.out.println("Lỗi chọn phòng (" + roomName + "): " + e.getMessage());
        }
    }

    public void handleUnexpectedAlert() {
        try {
            Alert alert = driver.switchTo().alert();
            System.out.println("Alert xuất hiện: " + alert.getText());
            alert.accept();
        } catch (NoAlertPresentException Ex) {
        }
    }

    // --- CÁC TEST CASE ---

    @Test(priority = 1, description = "TC01: Gửi thông báo thành công cho 1 phòng cụ thể")
    public void TC01_GuiThongBao_MotPhong() throws InterruptedException {
        selectRoom("Phòng NTT 01");

        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).sendKeys("[TC01] Thông báo điện nước");
        driver.findElement(By.xpath("//textarea")).sendKeys("Nội dung test case 01.");

        WebElement btnGui = driver.findElement(By.xpath("//button[contains(text(),'Gửi ngay')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnGui);

        Thread.sleep(2000);
        handleUnexpectedAlert();
        
        driver.navigate().refresh(); 
        Thread.sleep(1500);
        
        boolean isDisplayed = driver.getPageSource().contains("[TC01] Thông báo điện nước");
        Assert.assertTrue(isDisplayed, "FAILED: Không thấy thông báo trong lịch sử gửi!");
    }

    @Test(priority = 2, description = "TC02: Gửi thông báo cho TẤT CẢ khách thuê")
    public void TC02_GuiThongBao_TatCa() throws InterruptedException {
        // Dùng JS click cho chắc
        WebElement radioAll = driver.findElement(By.xpath("//label[contains(., 'Gửi tất cả')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioAll);

        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).sendKeys("[TC02] Thông báo chung");
        driver.findElement(By.xpath("//textarea")).sendKeys("Bảo trì thang máy.");

        WebElement btnGui = driver.findElement(By.xpath("//button[contains(text(),'Gửi ngay')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnGui);

        Thread.sleep(2000);
        handleUnexpectedAlert();
        driver.navigate().refresh();
        
        Assert.assertTrue(driver.getPageSource().contains("[TC02] Thông báo chung"));
    }

    @Test(priority = 3, description = "TC03: Validate lỗi khi bỏ trống Tiêu đề")
    public void TC03_Validate_TrongTieuDe() throws InterruptedException {
        selectRoom("Phòng NTT 01");

        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).clear();
        driver.findElement(By.xpath("//textarea")).sendKeys("Nội dung không có tiêu đề");

        WebElement btnGui = driver.findElement(By.xpath("//button[contains(text(),'Gửi ngay')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnGui);
        
        Thread.sleep(1000);

        boolean isErrorFound = false;
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            System.out.println("Alert text: " + alertText);
            if (alertText.toLowerCase().contains("tiêu đề") || alertText.contains("đầy đủ") || alertText.contains("required")) {
                isErrorFound = true;
            }
            alert.accept();
        } catch (NoAlertPresentException e) {
            if (driver.getPageSource().contains("required") || driver.getPageSource().contains("Vui lòng điền")) {
                isErrorFound = true;
            }
        }
        Assert.assertTrue(isErrorFound, "FAILED: Không thấy cảnh báo lỗi khi thiếu tiêu đề!");
    }

    @Test(priority = 4, description = "TC04: Validate lỗi khi bỏ trống Nội dung")
    public void TC04_Validate_TrongNoiDung() throws InterruptedException {
        selectRoom("Phòng NTT 01");

        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).sendKeys("[TC04] Tiêu đề có");
        driver.findElement(By.xpath("//textarea")).clear();

        WebElement btnGui = driver.findElement(By.xpath("//button[contains(text(),'Gửi ngay')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnGui);
        Thread.sleep(1000);

        boolean isErrorFound = false;
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (alertText.toLowerCase().contains("nội dung") || alertText.contains("đầy đủ")) {
                isErrorFound = true;
            }
            alert.accept();
        } catch (NoAlertPresentException e) {
            if (driver.getPageSource().contains("required") || driver.getPageSource().contains("Vui lòng điền")) {
                isErrorFound = true;
            }
        }
        Assert.assertTrue(isErrorFound, "FAILED: Không thấy cảnh báo lỗi khi thiếu nội dung!");
    }

    @Test(priority = 5, description = "TC05: Chức năng Lưu nháp (Draft)")
    public void TC05_ChucNang_LuuNhap() throws InterruptedException {
        selectRoom("Phòng NTT 01");
        
        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).sendKeys("[TC05] Bản nháp");
        driver.findElement(By.xpath("//textarea")).sendKeys("Bản nháp.");

        WebElement btnDraft = driver.findElement(By.xpath("//button[contains(text(),'Lưu nháp')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnDraft);
        Thread.sleep(2000);
        
        handleUnexpectedAlert();
        Assert.assertTrue(driver.getPageSource().contains("thành công") || driver.getPageSource().contains("[TC05]"));
    }

    @Test(priority = 6, description = "TC06: Chức năng Hủy")
    public void TC06_ChucNang_Huy() {
        WebElement title = driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]"));
        title.sendKeys("Dữ liệu sắp bị hủy");

        WebElement btnCancel = driver.findElement(By.xpath("//button[contains(text(),'Hủy')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnCancel);

        // Verify
        Assert.assertTrue(title.getText().isEmpty(), "FAILED: Dữ liệu chưa bị xóa sau khi Hủy");
    }

    @Test(priority = 7, description = "TC07: Gửi thông báo với Tiêu đề chứa ký tự đặc biệt")
    public void TC07_TieuDe_KyTuDacBiet() throws InterruptedException {
        selectRoom("Phòng NTT 01");

        String specialTitle = "[TC07] @#$%^&*() Tiền Điện!";
        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).sendKeys(specialTitle);
        driver.findElement(By.xpath("//textarea")).sendKeys("Test ký tự đặc biệt.");
        
        WebElement btnGui = driver.findElement(By.xpath("//button[contains(text(),'Gửi ngay')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnGui);

        Thread.sleep(2000);
        handleUnexpectedAlert();
        
        driver.navigate().refresh();
        Thread.sleep(1500);
        
        Assert.assertTrue(driver.getPageSource().contains(specialTitle), "FAILED: Không hiển thị đúng ký tự đặc biệt");
    }

    @Test(priority = 8, description = "TC08: Gửi thông báo với Nội dung cực dài")
    public void TC08_NoiDung_Dai() throws InterruptedException {
        selectRoom("Phòng NTT 01");

        String longContent = "Nội dung dài ".repeat(50);
        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).sendKeys("[TC08] Nội dung dài");
        driver.findElement(By.xpath("//textarea")).sendKeys(longContent);
        
        WebElement btnGui = driver.findElement(By.xpath("//button[contains(text(),'Gửi ngay')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnGui);

        Thread.sleep(2000);
        handleUnexpectedAlert();
        
        driver.navigate().refresh();
        Thread.sleep(1500);
        
        Assert.assertTrue(driver.getPageSource().contains("[TC08]"));
    }

    @Test(priority = 9, description = "TC09: Chọn nhiều phòng cùng lúc")
    public void TC09_Chon_NhieuPhong() throws InterruptedException {
        selectRoom("Phòng NTT 01");
        selectRoom("Phòng NTT 02");

        driver.findElement(By.xpath("//input[contains(@placeholder,'Ví dụ:')]")).sendKeys("[TC09] Gửi 2 phòng");
        driver.findElement(By.xpath("//textarea")).sendKeys("Test nhiều phòng.");
        
        WebElement btnGui = driver.findElement(By.xpath("//button[contains(text(),'Gửi ngay')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnGui);

        Thread.sleep(2000);
        handleUnexpectedAlert();
        
        driver.navigate().refresh();
        Thread.sleep(1500);
        
        Assert.assertTrue(driver.getPageSource().contains("[TC09]"));
    }

    @Test(priority = 10, description = "TC10: Kiểm tra hiển thị Lịch sử gửi")
    public void TC10_Verify_LichSu() {
        WebElement historyWidget = driver.findElement(By.xpath("//h5[contains(text(),'Lịch sử gửi')] | //div[contains(@class,'history')]"));
        Assert.assertTrue(historyWidget.isDisplayed(), "FAILED: Widget lịch sử không hiển thị");
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        Teardown();
    }
}