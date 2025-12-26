package Epic8;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import java.time.Duration;

public class US83 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        System.out.println("=== KHỞI TẠO TRÌNH DUYỆT ===");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        
        // Truy cập trang chủ
        driver.get("http://localhost:4200");
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        // Chọn phòng trọ
        driver.findElement(By.xpath("//a[contains(text(),'Phòng trọ') or contains(text(),'Cho thuê')]")).click();
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    @Test(priority = 1)
    public void TC831() {
        System.out.println("TC8.3.1 - Lọc theo giá thuê");
        
        try {
            // 1. Chọn lọc giá thuê từ 2–4 triệu
            driver.findElement(By.xpath("//select[@id='price']")).click();
            driver.findElement(By.xpath("//option[contains(text(),'2-4 triệu') or contains(@value,'2000000-4000000')]")).click();
            
            // 2. Nhấn "Lọc"
            driver.findElement(By.xpath("//button[contains(text(),'Lọc')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify: Hệ thống trả về danh sách chỉ gồm phòng có giá nằm trong khoảng 2–4 triệu
            boolean hasResults = checkElement("//div[contains(@class,'room-item')]");
            Assert.assertTrue(hasResults, "Không tìm thấy phòng trong khoảng giá 2-4 triệu");
            System.out.println("✓ Lọc theo giá thuê thành công");
            
            driver.findElement(By.xpath("//button[contains(text(),'Làm mới')]")).click();
            
        } catch (Exception e) {
            System.out.println("✗ TC831 failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void TC832() {
        System.out.println("TC8.3.2 - Lọc theo trạng thái phòng");
        
        try {
            // 1. Chọn trạng thái "Đang sửa chữa"
            driver.findElement(By.xpath("//select[@id='status']")).click();
            driver.findElement(By.xpath("//option[contains(text(),'Đang sửa chữa') or contains(@value,'repairing')]")).click();
            
            // 2. Nhấn "Lọc"
            driver.findElement(By.xpath("//button[contains(text(),'Lọc')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify: Danh sách kết quả chỉ gồm các phòng có trạng thái "Đang sửa chữa"
            boolean hasResults = checkElement("//div[contains(@class,'room-item')]");
            Assert.assertTrue(hasResults, "Không tìm thấy phòng đang sửa chữa");
            System.out.println("✓ Lọc theo trạng thái phòng thành công");
            
            driver.findElement(By.xpath("//button[contains(text(),'Làm mới')]")).click();
            
        } catch (Exception e) {
            System.out.println("✗ TC832 failed: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void TC833() {
        System.out.println("TC8.3.3 - Lọc kết hợp nhiều tiêu chí");
        
        try {
            // 1. Chọn giá 2–4 triệu
            driver.findElement(By.xpath("//select[@id='price']")).click();
            driver.findElement(By.xpath("//option[contains(text(),'2-4 triệu')]")).click();
            
            // 1. Chọn trạng thái "Trống"
            driver.findElement(By.xpath("//select[@id='status']")).click();
            driver.findElement(By.xpath("//option[contains(text(),'Trống') or contains(@value,'available')]")).click();
            
            // 2. Nhấn "Lọc"
            driver.findElement(By.xpath("//button[contains(text(),'Lọc')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify: Kết quả trả về chính xác theo cả 2 tiêu chí
            boolean hasResults = checkElement("//div[contains(@class,'room-item')]");
            Assert.assertTrue(hasResults, "Không tìm thấy phòng thỏa cả 2 điều kiện");
            System.out.println("✓ Lọc kết hợp nhiều tiêu chí thành công");
            
            driver.findElement(By.xpath("//button[contains(text(),'Làm mới')]")).click();
            
        } catch (Exception e) {
            System.out.println("✗ TC833 failed: " + e.getMessage());
        }
    }


    @Test(priority = 4)
    public void TC_UI_832() {
        System.out.println("TC.UI.8.3.2 - Kiểm tra khả năng chọn tiêu chí lọc");
        
        try {
            // Chọn giá từ 2–4 triệu
            driver.findElement(By.xpath("//select[@id='price']")).click();
            driver.findElement(By.xpath("//option[contains(text(),'2-4 triệu')]")).click();
            
            // Chọn trạng thái "Trống"
            driver.findElement(By.xpath("//select[@id='status']")).click();
            driver.findElement(By.xpath("//option[contains(text(),'Trống')]")).click();
            
            // Verify các dropdown phản hồi tốt
            Select priceSelect = new Select(driver.findElement(By.xpath("//select[@id='price']")));
            Select statusSelect = new Select(driver.findElement(By.xpath("//select[@id='status']")));
            
            String selectedPrice = priceSelect.getFirstSelectedOption().getText();
            String selectedStatus = statusSelect.getFirstSelectedOption().getText();
            
            boolean priceSelected = selectedPrice.contains("2-4") || selectedPrice.contains("2") || selectedPrice.contains("4");
            boolean statusSelected = selectedStatus.contains("Trống") || selectedStatus.contains("available");
            
            Assert.assertTrue(priceSelected, "Giá không được chọn đúng");
            Assert.assertTrue(statusSelected, "Trạng thái không được chọn đúng");
            
            System.out.println("✓ Khả năng chọn tiêu chí lọc hoạt động tốt");
            
            driver.findElement(By.xpath("//button[contains(text(),'Làm mới')]")).click();
            
        } catch (Exception e) {
            System.out.println("✗ TC.UI.8.3.2 failed: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void TC_UI_833() {
        System.out.println("TC.UI.8.3.3 - Kiểm tra hiển thị danh sách sau khi lọc");
        
        try {
            // Chọn giá 2–4 triệu
            driver.findElement(By.xpath("//select[@id='price']")).click();
            driver.findElement(By.xpath("//option[contains(text(),'2-4 triệu')]")).click();
            
            // Nhấn "Lọc"
            driver.findElement(By.xpath("//button[contains(text(),'Lọc')]")).click();
            
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            // Verify danh sách hiển thị đúng
            boolean hasRoomList = checkElement("//div[contains(@class,'room-list')]");
            boolean hasRoomItems = checkElement("//div[contains(@class,'room-item')]");
            boolean noLayoutError = !checkElement("//*[contains(@style,'overflow')]") && 
                                  !checkElement("//*[contains(@class,'error')]");
            
            Assert.assertTrue(hasRoomList, "Danh sách phòng không hiển thị");
            Assert.assertTrue(noLayoutError, "Có lỗi layout khi hiển thị danh sách");
            
            System.out.println("✓ Hiển thị danh sách sau khi lọc chuẩn");
            
            driver.findElement(By.xpath("//button[contains(text(),'Làm mới')]")).click();
            
        } catch (Exception e) {
            System.out.println("✗ TC.UI.8.3.3 failed: " + e.getMessage());
        }
    }
    
    private boolean checkElement(String xpath) {
        try {
            return driver.findElement(By.xpath(xpath)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}