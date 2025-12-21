package tests.usermanagement;

import base.BaseTest;
import base.NeedLogin;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pages.admin.AddUserPage;
import pages.admin.UserManagementPage;
import utils.TestDataUtil;

public class UserManagementTests_US12_3 extends BaseTest {

    private UserManagementPage userPage;

    @BeforeMethod
    public void setUp() {
        loginAsAdmin();
        driver.get("http://localhost:4200/admin/users");
        userPage = new UserManagementPage(driver);
    }

    // ---------------------------------------------------------
    // TC 12.3.1 – Xác minh hiển thị nút Xóa trên mỗi user
    // ---------------------------------------------------------
    @Test(priority = 1)
    @NeedLogin
    public void TC_12_3_1_verifyDeleteButtonDisplayed() {
        Assert.assertTrue(userPage.isDeleteButtonVisible(0), "Nút Xóa phải hiển thị trên mỗi tài khoản.");
    }

    // ---------------------------------------------------------
    // TC 12.3.2 – Xác minh hiển thị hộp thoại xác nhận
    // ---------------------------------------------------------
    @Test(priority = 2)
    @NeedLogin
    public void TC_12_3_2_verifyDeleteConfirmationDialog() {
        userPage.clickDeleteButton(0);
        Assert.assertTrue(userPage.isDeleteConfirmationShown(), "Hộp thoại xác nhận Xóa phải hiển thị.");
    }

    // ---------------------------------------------------------
    // TC 12.3.3 – Hủy thao tác xóa
    // ---------------------------------------------------------
    @Test(priority = 3)
    @NeedLogin
    public void TC_12_3_3_cancelDeleteUser() {
        userPage.clickDeleteButton(0);
        userPage.cancelDelete();
        Assert.assertFalse(userPage.isDeleteConfirmationShown(), "Form xác nhận Xóa phải đóng khi hủy.");
    }

    // ---------------------------------------------------------
    // TC 12.3.4 – Xóa user thành công nếu không có dữ liệu liên quan
    // ---------------------------------------------------------
    @Test(priority = 4)
    @NeedLogin
    public void TC_12_3_4_deleteUserWithoutRelations() {
        // 1. Tạo user mới
        String username = TestDataUtil.randomName();
        String email = username + "@gmail.com";
        String password = "123456Aa@";

        AddUserPage addUserPage = userPage.clickAddUser();
        addUserPage.fillForm(username, email, password, "Tenant");
        addUserPage.submit();

        // 2. Kiểm tra số lượng user trước khi xoá
        int countBefore = userPage.getUserCount();

        // 3. Xoá user vừa tạo
        userPage.clickDeleteButtonByUsername(username);
        userPage.confirmDelete();

        // 4. Kiểm tra số lượng user sau khi xoá
        int countAfter = userPage.getUserCount();
        Assert.assertEquals(countAfter, countBefore - 1, "User phải bị xóa thành công.");
    }


    // ---------------------------------------------------------
    // TC 12.3.5 & 12.3.6 – Không xóa user có ràng buộc
    // ---------------------------------------------------------
    @Test(priority = 5)
    @NeedLogin
    public void TC_12_3_5_6_cannotDeleteUserWithRelations() {
        userPage.clickDeleteButtonByUsername("tenantWithContract");
        userPage.confirmDelete();
        Assert.assertTrue(userPage.isAlertShown("Không thể xóa user có hợp đồng"), "Hệ thống không cho phép xóa user có ràng buộc.");
    }

    // ---------------------------------------------------------
    // TC 12.3.7 – User bị xoá không thể login
    // ---------------------------------------------------------
    @Test(priority = 6)
    @NeedLogin
    public void TC_12_3_7_deletedUserCannotLogin() {
        String username = "deletedUser";
        userPage.clickDeleteButtonByUsername(username);
        userPage.confirmDelete();

        boolean loginResult = isLoginSuccessful(username, "123456Aa@");
        Assert.assertFalse(loginResult, "User bị xóa không thể đăng nhập lại.");
    }

    // ---------------------------------------------------------
    // TC 12.3.8 – Không thể xoá Admin khác nếu policy hạn chế
    // ---------------------------------------------------------
    @Test(priority = 7)
    @NeedLogin
    public void TC_12_3_8_cannotDeleteOtherAdmin() {
        userPage.clickDeleteButtonByUsername("otherAdmin");
        userPage.confirmDelete();
        Assert.assertTrue(userPage.isAlertShown("Không thể xóa Admin khác"), "Hệ thống không cho phép xoá Admin khác nếu policy hạn chế.");
    }
}
