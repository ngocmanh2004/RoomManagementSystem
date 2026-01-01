package tests.users;

import base.BaseTest;
import utils.TestDataUtil;
import base.LoginAs;
import base.UserRole;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.UserFormModalPage;
import pages.UserManagementPage;

public class AddUserTests extends BaseTest {

   UserManagementPage page;

   @BeforeMethod
   @LoginAs(UserRole.ADMIN)
   public void setup() {
       driver.get("http://localhost:4200/admin/users");
       page = new UserManagementPage(driver);
   }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testOpenAddUserForm() {

        page.clickAddUserButton();
        UserFormModalPage modal = new UserFormModalPage(driver);

        Assert.assertTrue(modal.isModalDisplayed(), "Form thêm user không hiển thị");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testCreateUserSuccess() {
        page.clickAddUserButton();

        UserFormModalPage modal = new UserFormModalPage(driver);

        String username = TestDataUtil.randomName();
        String email = TestDataUtil.randomEmail();
        String phone = TestDataUtil.randomPhone();

        modal.enterUsername(username);
        modal.enterPassword("Abc@1234");
        modal.enterFullName("Nguyen Testt");
        modal.enterEmail(email);
        modal.enterPhone(phone);
        modal.selectRole("2");       
        modal.selectStatus("ACTIVE");
        modal.clickSave();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        String alertText = alert.getText();
        Assert.assertEquals(
            alertText,
            "Đã thêm người dùng mới thành công!",
            "Thông báo tạo user không đúng"
        );

        alert.accept();
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testValidationEmptyRequiredFields() {
        page.clickAddUserButton();

        UserFormModalPage modal = new UserFormModalPage(driver);
        modal.clickSave();

        Assert.assertTrue(modal.isRequiredErrorDisplayed(), "Không hiển thị lỗi khi bỏ trống các trường bắt buộc");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testValidationInvalidEmail() {
        page.clickAddUserButton();

        UserFormModalPage modal = new UserFormModalPage(driver);
        modal.enterEmail("invalidemail");
        modal.clickSave();

        Assert.assertTrue(modal.isEmailPatternErrorDisplayed(), "Không hiển thị lỗi email không đúng định dạng");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testValidationInvalidPhone() {
        page.clickAddUserButton();

        UserFormModalPage modal = new UserFormModalPage(driver);
        modal.enterPhone("123");
        modal.clickSave();

        Assert.assertTrue(modal.isPhonePatternErrorDisplayed(), "Không hiển thị lỗi phone không đúng định dạng");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testCancelAddUser() {
        page.clickAddUserButton();

        UserFormModalPage modal = new UserFormModalPage(driver);
        modal.clickCancel();

        Assert.assertFalse(modal.isModalDisplayed(), "Form không đóng khi nhấn Hủy");
    }
}
