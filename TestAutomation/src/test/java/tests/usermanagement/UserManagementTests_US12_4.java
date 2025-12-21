package tests.usermanagement;

import base.BaseTest;
import base.NeedLogin;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pages.admin.AddUserPage;
import pages.admin.EditUserPage;
import pages.admin.UserManagementPage;
import utils.TestDataUtil;

public class UserManagementTests_US12_4 extends BaseTest {

    private UserManagementPage userPage;
    private EditUserPage editPage;
    private String username;
    private String email;

    @BeforeMethod
    public void setUp() {
        loginAsAdmin();
        driver.get("http://localhost:4200/admin/users");
        userPage = new UserManagementPage(driver);

        // tạo user cố định cho edit test
        username = "testUserForEdit";
        email = username + "@gmail.com";

        if (userPage.getRowByUsername(username) == null) {
            AddUserPage addUserPage = userPage.clickAddUser();
            addUserPage.fillForm(username, email, "Abc@12345", "Tenant");
            addUserPage.submit();
        }

        // mở modal chỉnh sửa
        userPage.openEditUserByUsername(username);
        editPage = new EditUserPage(driver);
    }

	@Test(priority = 1)
    @NeedLogin
    public void TC_12_4_1_verifyEditFormDisplayed() {
        Assert.assertTrue(editPage.isFormDisplayed(), "Form chỉnh sửa phải hiển thị.");
    }

    @Test(priority = 2)
    @NeedLogin
    public void TC_12_4_2_requiredFieldValidation() {
        editPage.fillForm("", "", null, null, null);
        editPage.save();
        Assert.assertTrue(editPage.getErrorMessage().contains("bắt buộc"),
                "Thông báo các trường bắt buộc không hiển thị.");
    }

    @Test(priority = 3)
    @NeedLogin
    public void TC_12_4_3_invalidEmailValidation() {
        editPage.fillForm(null, "invalidEmail", null, null, null);
        editPage.save();
        Assert.assertTrue(editPage.getErrorMessage().contains("email không hợp lệ"),
                "Thông báo email không hợp lệ không hiển thị.");
    }

    @Test(priority = 4)
    @NeedLogin
    public void TC_12_4_4_duplicateEmailValidation() {
        // tạo 1 user khác để dùng email duplicate
        String otherUsername = TestDataUtil.randomName();
        String duplicateEmail = "existing@gmail.com";

        if (userPage.getRowByUsername(otherUsername) == null) {
            AddUserPage addUserPage = userPage.clickAddUser();
            addUserPage.fillForm(otherUsername, duplicateEmail, "Abc@12345", "Tenant");
            addUserPage.submit();
        }

        // dùng user testUserForEdit để update email trùng
        editPage.fillForm(null, duplicateEmail, null, null, null);
        editPage.save();

        Assert.assertTrue(editPage.getErrorMessage().contains("email đã tồn tại"),
                "Thông báo trùng email không hiển thị.");
    }

    @Test(priority = 5)
    @NeedLogin
    public void TC_12_4_5_updateValidInfoSuccess() {
        String newName = "Nguyen Van Updated";
        String newEmail = TestDataUtil.randomEmail();
        editPage.fillForm(newName, newEmail, null, null, null);
        editPage.save();
        Assert.assertTrue(editPage.getSuccessMessage().contains("Cập nhật thành công"),
                "Thông báo cập nhật thành công không hiển thị.");
    }

    @Test(priority = 6)
    @NeedLogin
    public void TC_12_4_6_passwordCannotUpdate() {
        editPage.fillForm(null, null, null, null, "NewPass123!");
        editPage.save();
        Assert.assertTrue(editPage.getErrorMessage().isEmpty(),
                "Password không thể cập nhật từ form này.");
    }

    @Test(priority = 7)
    @NeedLogin
    public void TC_12_4_7_updateRoleSuccess() {
        editPage.fillForm(null, null, "Admin", null, null);
        editPage.save();
        Assert.assertTrue(editPage.getSuccessMessage().contains("Cập nhật thành công"),
                "Cập nhật role thành công không hiển thị.");
    }

    @Test(priority = 8)
    @NeedLogin
    public void TC_12_4_8_updateStatusSuccess() {
        editPage.fillForm(null, null, null, "BANNED", null);
        editPage.save();
        Assert.assertTrue(editPage.getSuccessMessage().contains("Cập nhật thành công"),
                "Cập nhật status thành công không hiển thị.");
    }

    @Test(priority = 9)
    @NeedLogin
    public void TC_12_4_9_cancelEditForm() {
        editPage.cancel();
        Assert.assertFalse(editPage.isFormDisplayed(),
                "Form chỉnh sửa phải đóng khi nhấn Hủy.");
    }
}
