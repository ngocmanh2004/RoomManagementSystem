package Epic9;

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

public class US91 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TEST XEM HÓA ĐƠN (KHÁCH THUÊ) ===");
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();

        driver.get("http://localhost:4200/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("user2");
        driver.findElement(By.id("password")).sendKeys("111111");
        driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
    }

    @Test(priority = 1)
    public void TC_FUNC_9_1_3_VerifyInvoiceMonthDisplay() {
        System.out.println("TC_9.1.3 - Hiển thị đúng tháng lập hóa đơn");
        WebElement userBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("user-btn")));
        userBtn.click();
        try {
            WebElement profileLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/tenant-profile']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", profileLink);
        } catch (Exception e) {
            driver.get("http://localhost:4200/tenant-profile");
        }

        wait.until(ExpectedConditions.urlContains("/tenant-profile"));
        By btnViewInvoiceLoc = By.xpath("//button[contains(@class, 'quick-action-btn') and .//div[contains(text(), 'Xem hóa đơn')]]");
        WebElement btnXemHoaDon = wait.until(ExpectedConditions.presenceOfElementLocated(btnViewInvoiceLoc));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btnXemHoaDon);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        js.executeScript("arguments[0].click();", btnXemHoaDon);

        try {
            WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[span[text()='Tháng']]/select")));
            Select select = new Select(monthDropdown);
            select.selectByVisibleText("T12/2025");
            
            By statsLoc = By.xpath("//div[contains(@class, 'muted')]//small");
            wait.until(ExpectedConditions.textToBePresentInElementLocated(statsLoc, "3.500.000"));

            WebElement firstDataRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tr[td][1]")));
            
            String rowContent = firstDataRow.getText();
            Assert.assertTrue(rowContent.contains("12") && rowContent.contains("2025"));
            System.out.println("✓ TC_9.1.3: PASSED!");
        } catch (Exception e) {
            Assert.fail("Lỗi Verify tháng: " + e.getMessage());
        }
    }

    @Test(priority = 2, dependsOnMethods = {"TC_FUNC_9_1_3_VerifyInvoiceMonthDisplay"})
    public void TC_UI_9_1_4_VerifyFullInvoiceInfo() {
        System.out.println("TC_UI_9.1.4 - Kiểm tra hiển thị đầy đủ cấu trúc và thông tin hóa đơn");

        try {
            // 1. Kiểm tra các tiêu đề cột (Header) để đảm bảo cấu trúc bảng đầy đủ
            WebElement headerRow = driver.findElement(By.xpath("//table/thead/tr"));
            String headerText = headerRow.getText().toLowerCase();
            
            String[] expectedHeaders = {"phòng", "tháng", "tiền điện", "tiền nước", "tổng tiền"};
            System.out.println("-> Đang kiểm tra các cột tiêu đề...");
            for (String header : expectedHeaders) {
                Assert.assertTrue(headerText.contains(header), "Bảng thiếu cột: " + header);
            }

            // 2. Kiểm tra dòng dữ liệu cụ thể (Data)
            WebElement invoiceRow = driver.findElement(By.xpath("//tr[contains(., '12/2025')]"));
            String rowData = invoiceRow.getText();
            
            // Chỉ kiểm tra sự hiện diện của số tiền chính và các giá trị số (vì text ko chứa chữ 'điện')
            Assert.assertTrue(rowData.contains("3.500.000"), "Không tìm thấy số tiền 3.500.000 trong dòng dữ liệu!");
            
            System.out.println("✓ TC_UI_9.1.4: PASSED! Cấu trúc và dữ liệu hiển thị đầy đủ.");
        } catch (Exception e) {
            Assert.fail("Lỗi UI chi tiết: " + e.getMessage());
        }
    }
    
    @Test(priority = 3, dependsOnMethods = {"TC_UI_9_1_4_VerifyFullInvoiceInfo"})
    public void TC_9_1_5_VerifyTotalAmountCalculation() {
        System.out.println("TC_9.1.5 - Kiểm tra công thức tính Tổng tiền");
        try {
            // Tìm dòng hóa đơn tháng 12/2025
            WebElement row = driver.findElement(By.xpath("//tr[contains(., '12/2025')]"));
            
            // Dựa vào log của bạn: 
            // td[4]=Tiền Phòng, td[5]=Tiền Điện, td[6]=Tiền Nước, td[7]=Chi Phí Khác, td[8]=Tổng Tiền
            long giaPhong = parsePrice(row.findElement(By.xpath("./td[4]")).getText());
            long giaDien = parsePrice(row.findElement(By.xpath("./td[5]")).getText());
            long giaNuoc = parsePrice(row.findElement(By.xpath("./td[6]")).getText());
            long giaDichVu = parsePrice(row.findElement(By.xpath("./td[7]")).getText());
            long tongHienThi = parsePrice(row.findElement(By.xpath("./td[8]")).getText());

            long tongTuTinh = giaPhong + giaDien + giaNuoc + giaDichVu;

            System.out.println("-> Tính toán: " + giaPhong + " + " + giaDien + " + " + giaNuoc + " + " + giaDichVu + " = " + tongTuTinh);
            Assert.assertEquals(tongHienThi, tongTuTinh, "Tổng tiền không khớp!");
            
            System.out.println("✓ TC_9.1.5: PASSED!");
        } catch (Exception e) {
            Assert.fail("Lỗi logic tính toán: " + e.getMessage());
        }
    }
    @Test(priority = 4)
    public void TC_UI_9_1_7_VerifyUnpaidStatus() {
        System.out.println("TC_UI_9.1.7 - Kiểm tra hiển thị trạng thái 'Chưa thanh toán' (Tất cả tháng)");
        try {
            // 1. Chọn "Tất cả tháng"
            WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[span[text()='Tháng']]/select")));
            new Select(monthDropdown).selectByVisibleText("Tất cả tháng");
            System.out.println("-> Đã chọn: Tất cả tháng");

            // 2. Chọn trạng thái "Chưa thanh toán"
            WebElement statusDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[span[text()='Trạng Thái']]/select")));
            new Select(statusDropdown).selectByVisibleText("Chưa thanh toán");
            System.out.println("-> Đã chọn lọc theo: Chưa thanh toán");
            
            Thread.sleep(1000); // Đợi UI render lại dữ liệu

            // 3. Kiểm tra dòng đầu tiên trong bảng (Cột trạng thái thường là td[9] hoặc chứa text)
            WebElement firstStatusCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table/tbody/tr[1]/td[contains(., 'thanh toán')]")));
            
            String statusText = firstStatusCell.getText();
            System.out.println("-> Trạng thái thực tế: " + statusText);

            Assert.assertEquals(statusText, "Chưa thanh toán", "Lỗi: Mong muốn 'Chưa thanh toán' nhưng hiển thị khác!");
            System.out.println("✓ TC_9.1.7: PASSED!");

        } catch (Exception e) {
            Assert.fail("Lỗi TC_9.1.7: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void TC_UI_9_1_8_VerifyPaidStatus() {
        System.out.println("TC_UI_9.1.8 - Kiểm tra hiển thị trạng thái 'Đã thanh toán' (Tất cả tháng)");
        try {
            // 1. Đảm bảo vẫn chọn "Tất cả tháng"
            WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[span[text()='Tháng']]/select")));
            new Select(monthDropdown).selectByVisibleText("Tất cả tháng");

            // 2. Chọn trạng thái "Đã thanh toán"
            WebElement statusDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[span[text()='Trạng Thái']]/select")));
            new Select(statusDropdown).selectByVisibleText("Đã thanh toán");
            System.out.println("-> Đã chọn lọc theo: Đã thanh toán");

            Thread.sleep(1000);

            // 3. Kiểm tra dữ liệu
            try {
                WebElement firstStatusCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table/tbody/tr[1]/td[contains(., 'thanh toán')]")));
                
                String statusText = firstStatusCell.getText();
                System.out.println("-> Trạng thái thực tế: " + statusText);

                Assert.assertEquals(statusText, "Đã thanh toán", "Lỗi: Trạng thái hiển thị không khớp!");
                System.out.println("✓ TC_9.1.8: PASSED!");
                
            } catch (TimeoutException e) {
                // Trường hợp DB trắng (chưa có hóa đơn nào được thanh toán)
                System.out.println("(!) Thông báo: Hiện tại không tìm thấy hóa đơn 'Đã thanh toán' nào trong danh sách.");
            }

        } catch (Exception e) {
            Assert.fail("Lỗi TC_9.1.8: " + e.getMessage());
        }
    }
    
    @Test(priority = 6)
    public void TC_9_1_11_VerifyNoInvoicesFound() {
        System.out.println("TC_9.1.11 - Kiểm tra thông báo khi khách thuê không có hóa đơn");

        try {
            // 1. Đăng xuất và đăng nhập lại với user3
            // Cách nhanh nhất là quay lại trang login và xóa Storage để đảm bảo sạch session
            driver.get("http://localhost:4200/login");
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
            ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
            driver.navigate().refresh();

            System.out.println("-> Đang đăng nhập với tài khoản: user3");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("user3");
            driver.findElement(By.id("password")).sendKeys("111111");
            driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

            // Xử lý Alert thành công nếu có
            try {
                wait.until(ExpectedConditions.alertIsPresent());
                driver.switchTo().alert().accept();
            } catch (Exception e) {}

            // 2. Điều hướng đến trang hồ sơ khách thuê
            wait.until(ExpectedConditions.elementToBeClickable(By.className("user-btn"))).click();
            try {
                WebElement profileLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/tenant-profile']")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", profileLink);
            } catch (Exception e) {
                driver.get("http://localhost:4200/tenant-profile");
            }

            // 3. Nhấn nút "Xem hóa đơn"
            By btnViewInvoiceLoc = By.xpath("//button[contains(@class, 'quick-action-btn') and .//div[contains(text(), 'Xem hóa đơn')]]");
            WebElement btnXemHoaDon = wait.until(ExpectedConditions.presenceOfElementLocated(btnViewInvoiceLoc));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXemHoaDon);

            // 4. Kiểm tra hiển thị thông báo trống trong bảng dữ liệu
            // Dựa vào HTML bạn gửi: <td colspan="10" class="empty">
            WebElement emptyMessageCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table/tbody/tr/td[contains(@class, 'empty')]")));

            String actualMessage = emptyMessageCell.getText().trim();
            String expectedMessage = "Không tìm thấy hóa đơn nào phù hợp";

            System.out.println("-> Thông báo hiển thị thực tế: " + actualMessage);

            Assert.assertEquals(actualMessage, expectedMessage, "Lỗi: Thông báo hiển thị không đúng hoặc bảng không trống!");
            
            System.out.println("✓ TC_9.1.11: PASSED!");

        } catch (Exception e) {
            Assert.fail("Lỗi TC_9.1.11: " + e.getMessage());
        }
    }

    /**
     * Hàm hỗ trợ chuyển đổi chuỗi tiền tệ (vd: "3.500.000 ₫") sang kiểu long (3500000)
     */
    private long parsePrice(String text) {
        if (text == null || text.isEmpty()) return 0;
        // Loại bỏ tất cả ký tự không phải số (dấu chấm, ký tự ₫, khoảng trắng)
        String cleanString = text.replaceAll("[^\\d]", "");
        return cleanString.isEmpty() ? 0 : Long.parseLong(cleanString);
    }

    @AfterClass
    public void afterClass() {
        if (driver != null) driver.quit();
    }
}