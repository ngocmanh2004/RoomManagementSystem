package Epic4;

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

import org.openqa.selenium.Keys; 
import org.openqa.selenium.TimeoutException;

public class US41 {
    WebDriver driver;
    WebDriverWait wait;
    String existingInvoice = "HD0000004"; 

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TRÌNH DUYỆT EDGE ĐỂ TEST HÓA ĐƠN ===");
        driver = new EdgeDriver(); 
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();

        driver.get("http://localhost:4200/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("user1");
        driver.findElement(By.id("password")).sendKeys("111111");
        driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.elementToBeClickable(By.className("user-btn"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(.,'Dashboard Chủ trọ')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/landlord/invoices')]"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(),'Quản lý hóa đơn')] | //button[contains(text(),'Tạo hóa đơn')]")));
    }

    @Test(priority = 1)
    public void TC_FUNC_4_1_11_UpdateStatusToPaid() {
        System.out.println("TC_FUNC_4.1.11 - Cập nhật trạng thái hóa đơn sang Đã thanh toán");
        WebElement invoiceRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//tr[td[contains(text(), '" + existingInvoice + "')]]")));
        WebElement statusBadge = invoiceRow.findElement(By.xpath(".//span[contains(@class, 'badge')]"));
        Assert.assertEquals(statusBadge.getText().trim(), "Chưa thanh toán");
        invoiceRow.findElement(By.xpath(".//button[@title='Cập Nhật Thanh Toán']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'modal')]//button[contains(text(),'Xác Nhận')]"))).click();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
        By badgeLocator = By.xpath("//tr[td[contains(text(), '" + existingInvoice + "')]]//span[contains(@class, 'badge')]");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(badgeLocator, "Đã thanh toán"));
        System.out.println("-> Cập nhật PAID thành công.");
    }

    @Test(priority = 2, dependsOnMethods = {"TC_FUNC_4_1_11_UpdateStatusToPaid"})
    public void TC_FUNC_4_1_12_RevertToUnpaid() {
        System.out.println("TC_FUNC_4.1.12 - Cập nhật trạng thái hóa đơn về Chưa thanh toán");
        WebElement invoiceRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//tr[td[contains(text(), '" + existingInvoice + "')]]")));
        invoiceRow.findElement(By.xpath(".//button[@title='Cập Nhật Thanh Toán']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Xác Nhận')]"))).click();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
        By badgeLocator = By.xpath("//tr[td[contains(text(), '" + existingInvoice + "')]]//span[contains(@class, 'badge')]");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(badgeLocator, "Chưa thanh toán"));
        System.out.println("-> Revert UNPAID thành công.");
    }

    @Test(priority = 3)
    public void TC_FUNC_4_1_13_CreateInvoiceSuccess() {
        System.out.println("TC_4.1.13 - Tạo hóa đơn thành công (Tháng 12/2025)");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'+ Tạo Hóa Đơn Mới')]"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Tạo Hóa Đơn Mới']")));
        WebElement dropdown = driver.findElement(By.xpath("//label[contains(text(),'Hợp Đồng')]/following-sibling::select"));
        Select select = new Select(dropdown);
        select.selectByVisibleText(" HD0000006 - Nguyen Le ");
        WebElement monthInput = driver.findElement(By.xpath("//label[contains(text(),'Tháng')]/following-sibling::input[@type='month']"));
        monthInput.click(); 
        monthInput.sendKeys("122025"); 
        monthInput.sendKeys(Keys.TAB); 
        By tienPhongLoc = By.xpath("//label[text()='Tiền Phòng']/following-sibling::input");
        try {
            wait.until(d -> !d.findElement(tienPhongLoc).getAttribute("value").isEmpty());
        } catch (TimeoutException e) { Assert.fail("Lỗi: Không load tiền phòng!"); }
        driver.findElement(By.xpath("//div[contains(@class,'modal')]//button[contains(@class,'primary') and text()=' Tạo ']")).click();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal")));
        System.out.println("✓ TC_4.1.13: PASSED!");
    }
    
    @Test(priority = 4, dependsOnMethods = {"TC_FUNC_4_1_13_CreateInvoiceSuccess"})
    public void TC_FUNC_4_1_14_DuplicateInvoiceError() {
        System.out.println("TC_4.1.14 - Kiểm tra chặn tạo trùng hóa đơn");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'+ Tạo Hóa Đơn Mới')]"))).click();
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(),'Hợp Đồng')]/following-sibling::select")));
        new Select(dropdown).selectByVisibleText(" HD0000006 - Nguyen Le ");
        WebElement monthInput = driver.findElement(By.xpath("//label[contains(text(),'Tháng')]/following-sibling::input[@type='month']"));
        monthInput.sendKeys("122025");
        monthInput.sendKeys(Keys.TAB);
        driver.findElement(By.xpath("//div[contains(@class,'modal')]//button[contains(@class,'primary') and text()=' Tạo ']")).click();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Assert.assertTrue(driver.switchTo().alert().getText().contains("đã tồn tại"));
            driver.switchTo().alert().accept();
        } catch (TimeoutException e) { Assert.fail("Không hiện báo lỗi trùng!"); }
        driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")).click();
        System.out.println("✓ TC_4.1.14: PASSED!");
    }

    @Test(priority = 5)
    public void TC_FUNC_4_1_15_DefaultStatusCheck() {
        System.out.println("TC_4.1.15 - Kiểm tra trạng thái mặc định khi tạo mới");

        // 1. Nhấn nút "+ Tạo Hóa Đơn Mới" để mở Modal
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'+ Tạo Hóa Đơn Mới')]"))).click();
        
        // 2. Chọn Hợp đồng HD0000006
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(),'Hợp Đồng')]/following-sibling::select")));
        new Select(dropdown).selectByVisibleText(" HD0000006 - Nguyen Le ");

        // 3. Chọn Tháng 11/2025 (Khác với tháng 12 trong 4.1.13)
        WebElement monthInput = driver.findElement(By.xpath("//label[contains(text(),'Tháng')]/following-sibling::input[@type='month']"));
        monthInput.click();
        monthInput.sendKeys("112025");
        monthInput.sendKeys(Keys.TAB);
        System.out.println("-> Đã chọn tháng 11/2025");

        // 4. Hệ thống tự động load dữ liệu
        By tienPhongLoc = By.xpath("//label[text()='Tiền Phòng']/following-sibling::input");
        wait.until(d -> !d.findElement(tienPhongLoc).getAttribute("value").isEmpty());

        // 5. Nhấn nút " Tạo "
        driver.findElement(By.xpath("//div[contains(@class,'modal')]//button[contains(@class,'primary') and text()=' Tạo ']")).click();

        // 6. Xử lý Alert thành công
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        // 7. Verify: Trạng thái trong danh sách mặc định là "Chưa thanh toán"
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal")));
        
        // Tìm dòng hóa đơn vừa tạo (HD0000006 - Tháng 11)
        By badgeLoc = By.xpath("//tr[td[contains(text(),'HD0000006')] and td[contains(text(),'T11/2025')]]//span[contains(@class,'badge')]");
        String status = wait.until(ExpectedConditions.visibilityOfElementLocated(badgeLoc)).getText().trim();
        
        System.out.println("-> Trạng thái mặc định kiểm tra được: " + status);
        Assert.assertEquals(status, "Chưa thanh toán", "Lỗi: Trạng thái mặc định không đúng!");
        
        System.out.println("✓ TC_4.1.15: PASSED!");
    }

    @AfterClass
    public void afterClass() {
        if (driver != null) driver.quit();
    }
}