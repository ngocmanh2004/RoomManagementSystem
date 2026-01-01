package tests.tenants;

import base.BaseTest;
import base.LoginAs;
import base.NeedLogin;
import base.UserRole;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.TenantPage;

public class DeleteTenantTests extends BaseTest {

    @Test
    @LoginAs(UserRole.LANDLORD)
    public void testDeleteTenant() {

        TenantPage tenant = new TenantPage(driver);

        int beforeCount = tenant.getTenantCount();
        Assert.assertTrue(beforeCount > 0, "Không có khách thuê nào để test xóa!");

        String status = tenant.getTenantStatus(0);
        System.out.println("Trạng thái khách thuê đầu tiên: " + status);

        tenant.clickDeleteTenant(0);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        String alertText = alert.getText();
        System.out.println("Alert khi xóa: " + alertText);

        if (status.equals("ACTIVE")) {

            // Trường hợp KHÔNG ĐƯỢC XÓA
            Assert.assertTrue(
                alertText.contains("Không thể xóa") || alertText.contains("đang thuê"),
                "Alert không đúng thông báo lỗi khi khách đang thuê! Alert: " + alertText
            );

            alert.accept();

            int afterCount = tenant.getTenantCount();
            Assert.assertEquals(afterCount, beforeCount,
                    "Lỗi! Khách đang thuê mà số lượng lại thay đổi!");

        } else {

            // Trường hợp ĐƯỢC XÓA
            Assert.assertTrue(
                alertText.contains("Xóa khách thuê thành công") ||
                alertText.contains("Đã xóa"),
                "Alert không đúng thông báo thành công! Alert: " + alertText
            );

            alert.accept();

            try { Thread.sleep(1200); } catch (Exception ignored) {}

            int afterCount = tenant.getTenantCount();
            Assert.assertEquals(afterCount, beforeCount - 1,
                    "Xóa thất bại! Số lượng không giảm.");
        }
    }
}
