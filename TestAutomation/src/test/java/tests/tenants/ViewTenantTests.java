package tests.tenants;

import base.BaseTest;
import base.LoginAs;
import base.NeedLogin;
import base.UserRole;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.TenantPage;

public class ViewTenantTests extends BaseTest {

    @Test
    @LoginAs(UserRole.LANDLORD)
    public void testViewTenant() {

        TenantPage tenant = new TenantPage(driver);

        tenant.clickViewTenant(0);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(tenant.viewModalName
        ));

        String name = tenant.getModalName();
        String phone = tenant.getModalPhone();
        String cccd = tenant.getModalCCCD();

        Assert.assertFalse(name.isEmpty(), "Tên khách thuê bị rỗng!");
        Assert.assertFalse(phone.isEmpty(), "SĐT khách thuê bị rỗng!");
        Assert.assertFalse(cccd.isEmpty(), "CCCD khách thuê bị rỗng!");

        System.out.println("Tên: " + name);
        System.out.println("SĐT: " + phone);
        System.out.println("CCCD: " + cccd);

        driver.findElement(By.cssSelector(".modal-buttons .cancel")).click();
    }
}
