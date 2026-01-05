package Epic11;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import java.time.Duration;

public class US115 {
    WebDriver driver;
    WebDriverWait wait;
    String roomDetailUrl; // Để lưu lại URL trang chi tiết phòng

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TEST BÁO CÁO ĐÁNH GIÁ (US 11.5) ===");
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();

        // 1. Đăng nhập
        driver.get("http://localhost:4200/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("user1");
        driver.findElement(By.id("password")).sendKeys("111111");
        driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        // 2. Chọn phòng và vào chi tiết
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("room-list")));
        WebElement btnXemChiTiet = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//a[contains(text(), 'Xem chi tiết')])[1]")));
        
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnXemChiTiet);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXemChiTiet);
        
        // Lưu lại URL để dùng cho TC cuối
        roomDetailUrl = driver.getCurrentUrl();
    }

    @Test(priority = 1)
    public void TC_UI_11_5_1_VerifyReportButtonVisible() {
        System.out.println("TC_UI_11.5.1 - Kiểm tra hiển thị nút Báo cáo");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight/2);");
        WebElement btnReport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-report")));
        Assert.assertTrue(btnReport.isDisplayed());
        System.out.println("✓ Nút Báo cáo đã hiển thị.");
    }

    @Test(priority = 2, dependsOnMethods = "TC_UI_11_5_1_VerifyReportButtonVisible")
    public void TC_UI_11_5_3_and_11_5_4_OpenFormAndVerifyReasons() {
        System.out.println("TC_UI_11.5.3 & 11.5.4 - Mở form và kiểm tra danh sách lý do");
        WebElement btnReport = driver.findElement(By.className("btn-report"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnReport);

        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'modal-content')]//h3")));
        Assert.assertEquals(modalTitle.getText().trim(), "Báo cáo đánh giá");

        WebElement selectElement = driver.findElement(By.name("reason"));
        String selectText = selectElement.getText();
        String[] expectedOptions = {"Spam", "Từ ngữ xúc phạm", "Sai sự thật", "Khác"};
        for (String option : expectedOptions) {
            Assert.assertTrue(selectText.contains(option));
        }
        System.out.println("✓ Form hiển thị đúng.");
    }

    @Test(priority = 3, dependsOnMethods = "TC_UI_11_5_3_and_11_5_4_OpenFormAndVerifyReasons")
    public void TC_11_5_5_SubmitReportSuccessfully() {
        System.out.println("TC_11.5.5 - Gửi báo cáo thành công");
        Select dropdown = new Select(driver.findElement(By.name("reason")));
        dropdown.selectByVisibleText("Từ ngữ xúc phạm");

        driver.findElement(By.name("description")).sendKeys("Nội dung vi phạm quy tắc.");
        driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Gửi báo cáo')]")).click();

        // PHẢI XỬ LÝ ALERT ĐỂ KHÔNG BỊ LỖI UNHANDLED ALERT
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("✓ Xuất hiện Alert: " + alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("! Không thấy Alert thông báo.");
        }
    }

    @Test(priority = 4, dependsOnMethods = "TC_11_5_5_SubmitReportSuccessfully")
    public void TC_11_5_8_LogoutAndVerifyGuestMessage() {
        System.out.println("TC_11.5.8 - Đăng xuất và kiểm tra thông báo yêu cầu đăng nhập");

        // 1. Mở menu User
        WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.className("user-btn")));
        userDropdown.click();

        // 2. Nhấn Đăng xuất
        WebElement btnLogout = wait.until(ExpectedConditions.elementToBeClickable(By.className("logout-btn")));
        btnLogout.click();

        // 3. XỬ LÝ ALERT 1: Xác nhận đăng xuất
        try {
            Alert alertConfirm = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("-> Chấp nhận Alert 1: " + alertConfirm.getText());
            alertConfirm.accept();
        } catch (Exception e) {
            System.out.println("! Không thấy Alert xác nhận.");
        }

        // 4. XỬ LÝ ALERT 2: Thông báo "Đăng xuất thành công!"
        try {
            Alert alertSuccess = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("-> Chấp nhận Alert 2: " + alertSuccess.getText());
            alertSuccess.accept();
        } catch (Exception e) {
            System.out.println("! Không thấy Alert thông báo thành công.");
        }

        // 5. Sau khi đóng hết Alert, quay về Trang chủ
        WebElement linkHome = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Trang chủ')]")));
        linkHome.click();

        // 6. Chọn một phòng để kiểm tra
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("room-list")));
        WebElement btnXemChiTiet = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//a[contains(text(), 'Xem chi tiết')])[1]")));
        
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnXemChiTiet);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXemChiTiet);

        // 7. Cuộn xuống xem thông báo chặn khách
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        WebElement loginMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("not-logged-in-message")));
        String actualText = loginMessage.getText().trim();
        System.out.println("-> Kết quả cuối cùng: " + actualText);

        Assert.assertTrue(actualText.contains("Vui lòng đăng nhập"), "Lỗi: Không thấy thông báo yêu cầu đăng nhập!");
        System.out.println("✓ TC_11.5.8: PASSED!");
    }

    @AfterClass
    public void afterClass() {
        if (driver != null) driver.quit();
    }
}