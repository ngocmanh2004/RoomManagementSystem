package Epic9;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import java.time.Duration;
import java.util.Set;

public class US92 {
    WebDriver driver;
    WebDriverWait wait;
    String originalWindow; 

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TEST LUỒNG THANH TOÁN (US 9.2) ===");
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.manage().window().maximize();

        driver.get("http://localhost:4200/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("user2");
        driver.findElement(By.id("password")).sendKeys("111111");
        driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        driver.get("http://localhost:4200/tenant-profile");
        WebElement btnXemHoaDon = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//div[contains(text(), 'Xem hóa đơn')]]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXemHoaDon);
    }

    @Test(priority = 1)
    public void TC_9_2_3_OpenBankSelection() {
        System.out.println("TC_9.2.3 - Mở màn hình chọn ngân hàng");
        originalWindow = driver.getWindowHandle(); 
        
        WebElement btnThanhToan = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//tr[contains(., 'Chưa thanh toán')]//button[@title='Thanh toán']")));
        btnThanhToan.click();

        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        Set<String> allWindows = driver.getWindowHandles();
        for (String windowHandle : allWindows) {
            if (!originalWindow.contentEquals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
        Assert.assertTrue(driver.getCurrentUrl().contains("vnpayment.vn"));
    }

    @Test(priority = 2, dependsOnMethods = "TC_9_2_3_OpenBankSelection")
    public void TC_9_2_4_VerifyBankListDisplayed() {
        System.out.println("TC_9.2.4 - Hiển thị danh sách ngân hàng");
        WebElement btnDomestic = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(@class, 'list-method-button')]//div[contains(text(), 'Thẻ nội địa')]")));
        btnDomestic.click();
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("NCB"))).isDisplayed());
    }

    @Test(priority = 3, dependsOnMethods = "TC_9_2_4_VerifyBankListDisplayed")
    public void TC_UI_9_2_5_and_9_2_6_SelectBankAndVerifyAmount() {
        System.out.println("TC_UI_9.2.5 & 9.2.6 - Chọn ngân hàng và kiểm tra tiền");
        driver.findElement(By.id("NCB")).click();
        WebElement amountLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalAmountDt")));
        Assert.assertTrue(amountLabel.getText().replaceAll("[^\\d]", "").contains("3500000"));
    }

    @Test(priority = 4, dependsOnMethods = "TC_UI_9_2_5_and_9_2_6_SelectBankAndVerifyAmount")
    public void TC_9_2_8_VerifyEmptyCardNumberError() {
        System.out.println("TC_9.2.8 - Kiểm tra thông báo lỗi khi để trống số thẻ");
        driver.findElement(By.id("btnContinue")).click();
        WebElement errorLi = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='err-card']//li[@class='parsley-required']")));
        Assert.assertEquals(errorLi.getText().trim(), "Quý khách vui lòng nhập Số thẻ");
        System.out.println("✓ TC_9.2.8: PASSED!");
    }

    @Test(priority = 5, dependsOnMethods = "TC_9_2_8_VerifyEmptyCardNumberError")
    public void TC_9_2_7_VerifyOTPPageDisplayed() {
        System.out.println("TC_9.2.7 - Kiểm tra chuyển sang màn hình nhập OTP");
        driver.findElement(By.id("card_number_mask")).sendKeys("9704198526191432198");
        driver.findElement(By.id("cardHolder")).sendKeys("NGUYEN VAN A");
        driver.findElement(By.id("cardDate")).sendKeys("07/15");
        driver.findElement(By.id("btnContinue")).click();

        try {
            WebElement btnAgree = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnAgree")));
            btnAgree.click();
        } catch (Exception e) {}

        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("otpvalue"))).isDisplayed());
        System.out.println("✓ TC_9.2.7: PASSED!");
    }

    @Test(priority = 6, dependsOnMethods = "TC_9_2_7_VerifyOTPPageDisplayed")
    public void TC_9_2_10_PaymentWithCorrectOTP() {
        System.out.println("TC_9.2.10 - Thanh toán thành công với OTP 123456");
        driver.findElement(By.id("otpvalue")).sendKeys("123456");
        driver.findElement(By.id("btnConfirm")).click();

        // Chờ quay về localhost
        wait.until(ExpectedConditions.urlContains("localhost"));
        Assert.assertTrue(driver.getPageSource().contains("thành công") || driver.getCurrentUrl().contains("success"));
        System.out.println("✓ TC_9.2.10: PASSED!");
    }

    @Test(priority = 7, dependsOnMethods = "TC_9_2_10_PaymentWithCorrectOTP")
    public void TC_UI_9_2_13_VerifyStatusAutoUpdated() {
        System.out.println("TC_UI_9.2.13 - Kiểm tra trạng thái hóa đơn cập nhật PAID");

        // 1. Đóng Tab hiện tại (Tab success) và quay lại Tab chính
        driver.close(); 
        driver.switchTo().window(originalWindow);
        
        // 2. Điều hướng trực tiếp lại trang hóa đơn để đảm bảo dữ liệu mới nhất
        driver.get("http://localhost:4200/tenant-profile");
        WebElement btnXemHoaDon = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//div[contains(text(), 'Xem hóa đơn')]]")));
        btnXemHoaDon.click();

        // 3. Tìm Badge (Sử dụng XPath linh hoạt hơn cho tháng 12)
        WebElement statusBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//tr[contains(., '12/2025') or contains(., 'T12')]//span[contains(@class, 'badge')]")));
        
        String statusText = statusBadge.getText().trim();
        System.out.println("-> Trạng thái hóa đơn: " + statusText);

        Assert.assertEquals(statusText, "Đã thanh toán");
        Assert.assertTrue(statusBadge.getAttribute("class").contains("bg-green-100"));
        System.out.println("✓ TC_9.2.13: PASSED!");
    }

    @AfterClass
    public void afterClass() {
        if (driver != null) driver.quit();
    }
}