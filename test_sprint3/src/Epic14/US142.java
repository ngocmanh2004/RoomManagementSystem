package Epic14;

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
import java.util.List;

public class US142 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TEST XỬ LÝ BÁO CÁO (US 14.2) ===");
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();

        driver.get("http://localhost:4200/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("nguyenngu");
        driver.findElement(By.id("password")).sendKeys("111111");
        driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.elementToBeClickable(By.className("user-btn"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/admin/dashboard')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/admin/report-management')]"))).click();
        System.out.println("✓ Đã vào màn hình danh sách báo cáo.");
    }

    @Test(priority = 1)
    public void TC_UI_14_2_2_VerifyNewStatusInList() {
        System.out.println("\n--- TC_UI_14.2.2: Kiểm tra trạng thái Mới ---");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table/tbody/tr")));
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
        boolean hasNew = false;
        for (WebElement row : rows) {
            if (row.getText().contains("Mới")) {
                System.out.println("-> Tìm thấy: " + row.getText().split("\n")[0]);
                hasNew = true;
                break;
            }
        }
        Assert.assertTrue(hasNew, "Không tìm thấy báo cáo 'Mới'!");
    }

    @Test(priority = 2)
    public void TC_14_2_5_and_14_2_6_HandleNotes() {
        System.out.println("\n--- TC_14.2.5 & 14.2.6: Nhập ghi chú (Dòng 2) ---");
        // FIX: Sửa lại tên phương thức presenceOfElementLocated
        WebElement btnXem = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("(//button[contains(text(), 'Xem chi tiết')])[2]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXem);

        // Đợi Modal xuất hiện rõ ràng trước khi tìm textarea
        WebElement txtNote = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'modal')]//textarea")));
        txtNote.clear();
        txtNote.sendKeys("Ghi chú xử lý tự động");
        System.out.println("-> Đã nhập ghi chú.");
    }

    @Test(priority = 3)
    public void TC_14_2_4_and_14_2_7_UpdateToResolved() {
        System.out.println("\n--- TC_14.2.4 & 14.2.7: Cập nhật Đã xử lý ---");
        
        // FIX: Tìm select nằm TRONG modal để tránh nhầm với bộ lọc bên ngoài
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'modal')]")));
        WebElement statusDropdown = modal.findElement(By.xpath(".//select")); 
        
        Select select = new Select(statusDropdown);
        String currentStatus = select.getFirstSelectedOption().getText();
        System.out.println("-> Trạng thái hiện tại trong Modal: " + currentStatus);

        if (!currentStatus.contains("Đã xử lý")) {
            select.selectByVisibleText("Đã xử lý");
            System.out.println("-> Đã chuyển đổi sang: Đã xử lý");
        }

        // FIX: Tìm nút Lưu nằm TRONG modal
        WebElement btnSave = modal.findElement(By.xpath(".//button[contains(text(), 'Lưu xử lý')]"));
        btnSave.click();

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String msg = alert.getText();
            System.out.println("-> Thông báo hệ thống: " + msg);
            Assert.assertFalse(msg.toLowerCase().contains("lỗi"), "Vẫn bị lỗi Backend: " + msg);
            alert.accept();
        } catch (Exception e) {
            System.out.println("-> Hoàn tất (Không có Alert)");
        }
    }

    @Test(priority = 4)
    public void TC_UI_14_2_8_VerifySync() {
        System.out.println("\n--- TC_UI_14.2.8: Kiểm tra đồng bộ ---");
        try {
            // Đóng Modal bằng nút Đóng
            driver.findElement(By.xpath("//div[contains(@class, 'modal')]//button[contains(text(), 'Đóng')]")).click();
        } catch (Exception e) {}
        
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        String tableBody = driver.findElement(By.tagName("table")).getText();
        
        Assert.assertTrue(tableBody.contains("Đã xử lý"), "Lỗi: Trạng thái ngoài danh sách chưa cập nhật!");
        System.out.println("✓ Đồng bộ danh sách thành công.");
    }

    @Test(priority = 5)
    public void TC_14_2_9_LockAccount() {
        System.out.println("\n--- TC_14.2.9: Khóa tài khoản (Dòng 3) ---");
        WebElement btnXem = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("(//button[contains(text(), 'Xem chi tiết')])[3]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXem);

        // Đợi modal mở và tìm nút Khóa bên trong
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'modal')]")));
        WebElement btnLock = modal.findElement(By.xpath(".//button[contains(text(), 'Khóa tài khoản')]"));
        btnLock.click();

        for (int i = 0; i < 2; i++) {
            try {
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                System.out.println("-> Thông báo " + (i + 1) + ": " + alert.getText());
                alert.accept();
            } catch (Exception e) {}
        }
        System.out.println("✓ Hoàn thành luồng test.");
    }

    @AfterClass
    public void afterClass() {
        if (driver != null) driver.quit();
        System.out.println("\n=== KẾT THÚC TEST US 14.2 ===");
    }
}