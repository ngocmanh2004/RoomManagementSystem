package tests.tenants;

import base.BaseTest;
import base.LoginAs;
import base.NeedLogin;
import base.UserRole;

import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.TenantPage;

import java.time.Duration;

public class TenantValidationTests extends BaseTest {

    @Test
    @LoginAs(UserRole.LANDLORD)
    public void testMissingRequiredFields() {
        TenantPage tenant = new TenantPage(driver);

        tenant.clickAddTenant();

        tenant.fillTenantForm("", "", "", "", "", "");

        tenant.submitForm();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        String alertText = alert.getText();
        alert.accept();

        Assert.assertTrue(
                alertText.contains("bắt buộc") || alertText.contains("không được để trống"),
                "Thông báo thiếu dữ liệu bắt buộc không đúng!"
        );
    }
}
