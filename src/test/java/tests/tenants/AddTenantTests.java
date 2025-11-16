package tests.tenants;

import base.BaseTest;
import base.NeedLogin;

import java.time.Duration;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.TenantPage;
import utils.TestDataUtil;

public class AddTenantTests extends BaseTest {

    @Test
    @NeedLogin
    public void testAddTenant() {
        TenantPage tenant = new TenantPage(driver);
        
        String name = TestDataUtil.randomName();
        String phone = TestDataUtil.randomPhone();
        String email = TestDataUtil.randomEmail(); 
        String cccd = TestDataUtil.randomCCCD();
       
        tenant.clickAddTenant();
        tenant.fillTenantForm(name, phone, email, cccd, "01/01/2000", "AnNhon");
        tenant.submitForm();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Alert successAlert = wait.until(ExpectedConditions.alertIsPresent());

            String alertText = successAlert.getText();
            System.out.println("Nội dung Alert: " + alertText);

            Assert.assertTrue(
                alertText.contains("Thêm khách thuê thành công!"), 
                "Nội dung alert không phải là thông báo thành công. Nội dung là: " + alertText
            );
            successAlert.accept();

        } catch (Exception e) {
           Assert.fail("Test thất bại. Không tìm thấy alert thông báo sau khi submit.", e);
        }
    }
}