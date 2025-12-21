package tests.usermanagement;

import base.BaseTest;
import base.NeedLogin;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.admin.AddUserPage;
import pages.admin.UserManagementPage;
import utils.TestDataUtil;

public class UserManagementTests_US12_5 extends BaseTest {

    private UserManagementPage userPage;
    private final String username = "testUserLock";

    @BeforeMethod
    public void setUp() {
        loginAsAdmin();
        driver.get("http://localhost:4200/admin/users");
        userPage = new UserManagementPage(driver);

        // tạo user nếu chưa tồn tại
        if (userPage.getRowByUsername(username) == null) {
            AddUserPage addUserPage = userPage.clickAddUser();
            addUserPage.fillForm(username, username + "@gmail.com", "Abc@12345", "Tenant");
            addUserPage.submit();
        }
    }

    @Test(priority = 1)
    @NeedLogin
    public void TC_12_5_1_verifyLockUnlockButtonDisplayed() {
        Assert.assertTrue(userPage.isLockButtonVisible(username), "Nút Khóa/Mở khóa phải hiển thị.");
    }

    @Test(priority = 2)
    @NeedLogin
    public void TC_12_5_2_confirmLockPopup() {
        userPage.clickLockByUsername(username);
        Assert.assertTrue(userPage.isDeleteConfirmationShown(), "Popup xác nhận khóa phải hiển thị.");
    }

    @Test(priority = 3)
    @NeedLogin
    public void TC_12_5_3_cancelLock() {
        userPage.clickLockByUsername(username);
        userPage.cancelDelete(); // dùng lại cancelDelete cho popup
        Assert.assertFalse(userPage.isDeleteConfirmationShown(), "Popup phải đóng khi hủy.");
    }

    @Test(priority = 4)
    @NeedLogin
    public void TC_12_5_4_lockUserSuccess() {
        userPage.clickLockByUsername(username);
        userPage.confirmDelete(); // xác nhận khóa
        Assert.assertTrue(userPage.isAlertShown("Khóa tài khoản thành công"), "Thông báo khóa thành công không hiển thị.");
    }

    @Test(priority = 5)
    public void TC_12_5_5_lockedUserCannotLogin() {
        // Kiểm tra user bị khóa không thể đăng nhập
        boolean loginResult = isLoginSuccessful(username, "Abc@12345");
        Assert.assertFalse(loginResult, "User bị khóa không thể đăng nhập.");
    }


    @Test(priority = 6)
    @NeedLogin
    public void TC_12_5_6_confirmUnlockPopup() {
        userPage.clickUnlockByUsername(username);
        Assert.assertTrue(userPage.isDeleteConfirmationShown(), "Popup xác nhận mở khóa phải hiển thị.");
    }

    @Test(priority = 7)
    @NeedLogin
    public void TC_12_5_7_cancelUnlock() {
        userPage.clickUnlockByUsername(username);
        userPage.cancelDelete();
        Assert.assertFalse(userPage.isDeleteConfirmationShown(), "Popup phải đóng khi hủy mở khóa.");
    }

    @Test(priority = 8)
    @NeedLogin
    public void TC_12_5_8_unlockUserSuccess() {
        userPage.clickUnlockByUsername(username);
        userPage.confirmDelete();
        Assert.assertTrue(userPage.isAlertShown("Mở khóa tài khoản thành công"), "Thông báo mở khóa thành công không hiển thị.");
    }

    @Test(priority = 9)
    @NeedLogin
    public void TC_12_5_9_checkUserStatusAfterUpdate() {
        String status = userPage.getUserStatusByUsername(username);
        Assert.assertEquals(status, "ACTIVE", "Trạng thái user phải là ACTIVE sau khi mở khóa.");
    }
}
