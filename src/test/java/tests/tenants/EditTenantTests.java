package tests.tenants;

import base.BaseTest;
import base.NeedLogin;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.TenantPage;
import utils.TestDataUtil;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EditTenantTests extends BaseTest {

    @Test
    @NeedLogin
    public void testEditTenant() {
        TenantPage tenant = new TenantPage(driver);

        String newName = TestDataUtil.randomName();
        String newPhone = TestDataUtil.randomPhone();

        tenant.clickEditTenant(0);

        tenant.fillTenantForm(
                newName,
                newPhone,
                "",
                "",
                "",
                ""
        );

        tenant.submitForm();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        String alertText = alert.getText();
        System.out.println("Alert: " + alertText);

        Assert.assertTrue(
                alertText.contains("Cập nhật"),
                "Thông báo không đúng định dạng!"
        );

        alert.accept();
    }
}
