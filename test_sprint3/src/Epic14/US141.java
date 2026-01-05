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

public class US141 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TEST QUẢN LÝ BÁO CÁO (US 14.1) ===");
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();

        // 1. Đăng nhập
        driver.get("http://localhost:4200/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("nguyenngu");
        driver.findElement(By.id("password")).sendKeys("111111");
        driver.findElement(By.xpath("//button[contains(text(),'Đăng nhập')]")).click();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        // 2. Vào Dashboard Admin -> Quản lý báo cáo
        wait.until(ExpectedConditions.elementToBeClickable(By.className("user-btn"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/admin/dashboard')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/admin/report-management')]"))).click();
        
        System.out.println("✓ Đã vào màn hình Quản lý báo cáo.");
    }

    @Test(priority = 1)
    public void TC_UI_14_1_2_VerifyTableColumns() {
        System.out.println("\n--- TC_UI_14.1.2: Kiểm tra đầy đủ các cột ---");
        List<WebElement> headers = driver.findElements(By.xpath("//table//th"));
        System.out.print("Cột thực tế: ");
        headers.forEach(h -> System.out.print("[" + h.getText() + "] "));
        System.out.println();
        
        String[] expected = {"NGƯỜI GỬI", "ĐỐI TƯỢNG BỊ BÁO CÁO", "LÝ DO", "NGÀY GỬI", "TRẠNG THÁI"};
        String headerText = "";
        for(WebElement h : headers) headerText += h.getText().toUpperCase() + " ";
        
        for(String s : expected) {
            Assert.assertTrue(headerText.contains(s), "Thiếu cột: " + s);
        }
        System.out.println("✓ Các cột hiển thị đầy đủ.");
    }

    @Test(priority = 2)
    public void TC_UI_14_1_3_VerifyDataIntegrity() {
        System.out.println("\n--- TC_UI_14.1.3: Hiển thị đúng dữ liệu từng báo cáo ---");
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
        Assert.assertTrue(rows.size() > 0, "Bảng trống, không có dữ liệu để test!");

        // Lấy dòng đầu tiên kiểm tra xem có ô nào bị bỏ trống không
        WebElement firstRow = rows.get(0);
        List<WebElement> cells = firstRow.findElements(By.tagName("td"));
        
        System.out.println("Dữ liệu dòng 1: " + firstRow.getText());
        for (int i = 0; i < cells.size() - 1; i++) { // Bỏ qua cột thao tác
            String content = cells.get(i).getText().trim();
            Assert.assertFalse(content.isEmpty(), "Ô thứ " + (i+1) + " bị rỗng dữ liệu!");
        }
        System.out.println("✓ Dữ liệu hiển thị đầy đủ, không bị rỗng.");
    }

    @Test(priority = 3)
    public void TC_14_1_4_FilterByStatus() throws InterruptedException {
        System.out.println("\n--- TC_14.1.4: Lọc báo cáo theo trạng thái 'Mới' ---");
        WebElement statusSelect = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("(//select[contains(@class, 'filter-select')])[1]")));
        
        Select select = new Select(statusSelect);
        select.selectByVisibleText("Mới");
        System.out.println("-> Đã chọn trạng thái: Mới");
        Thread.sleep(2000);

        checkFilterResults("Mới", "");
    }

    @Test(priority = 4)
    public void TC_14_1_5_FilterByType() throws InterruptedException {
        System.out.println("\n--- TC_14.1.5: Lọc báo cáo theo loại 'Spam' ---");
        // Reset Status về Tất cả
        new Select(driver.findElement(By.xpath("(//select[contains(@class, 'filter-select')])[1]"))).selectByIndex(0);
        
        WebElement typeSelect = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("(//select[contains(@class, 'filter-select')])[2]")));
        
        Select select = new Select(typeSelect);
        select.selectByVisibleText("Spam");
        System.out.println("-> Đã chọn loại vi phạm: Spam");
        Thread.sleep(2000);

        checkFilterResults("", "Spam");
    }

    @Test(priority = 5)
    public void TC_14_1_6_CombinedFilter() throws InterruptedException {
        System.out.println("\n--- TC_14.1.6: Kết hợp lọc Trạng thái và Loại ---");
        Select status = new Select(driver.findElement(By.xpath("(//select[contains(@class, 'filter-select')])[1]")));
        Select type = new Select(driver.findElement(By.xpath("(//select[contains(@class, 'filter-select')])[2]")));
        
        status.selectByVisibleText("Mới");
        type.selectByVisibleText("Spam");
        System.out.println("-> Đã chọn: Mới + Spam");
        Thread.sleep(2000);

        checkFilterResults("Mới", "Spam");
    }

    @Test(priority = 6)
    public void TC_UI_14_1_7_and_14_1_8_ViewDetailAndVerifyContent() {
        System.out.println("\n--- TC_UI_14.1.7 & 14.1.8: Xem chi tiết báo cáo ---");
        driver.navigate().refresh(); // Reset về trạng thái ban đầu

        WebElement btnDetail = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//button[contains(text(), 'Xem chi tiết')])[1]")));
        btnDetail.click();

        // Kiểm tra Popup chi tiết
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'modal-content')] | //div[contains(@class, 'report-detail')]")));
        
        String modalContent = modal.getText();
        System.out.println("Nội dung Popup:\n" + modalContent);

        // Kiểm tra các trường bắt buộc (US 14.1.8)
        String[] fields = {"Người gửi", "Đối tượng bị báo cáo", "Lý do", "Nội dung đánh giá", "Ngày gửi", "Trạng thái"};
        for(String f : fields) {
            Assert.assertTrue(modalContent.contains(f), "Popup thiếu trường: " + f);
        }
        
        System.out.println("✓ Popup hiển thị đầy đủ thông tin chi tiết.");
        driver.findElement(By.xpath("//button[contains(text(), 'Đóng')]")).click();
    }

    // Hàm phụ dùng chung để kiểm tra dữ liệu sau khi lọc
    private void checkFilterResults(String expectedStatus, String expectedType) {
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
        System.out.println("-> Kết quả: " + rows.size() + " dòng.");
        
        for (int i = 0; i < rows.size(); i++) {
            String text = rows.get(i).getText();
            System.out.println("   Row " + (i+1) + ": " + text);
            if (!expectedStatus.isEmpty()) Assert.assertTrue(text.contains(expectedStatus));
            if (!expectedType.isEmpty()) Assert.assertTrue(text.contains(expectedType));
        }
    }

    @AfterClass
    public void afterClass() {
        if (driver != null) driver.quit();
        System.out.println("\n=== KẾT THÚC TEST US 14.1 ===");
    }
}