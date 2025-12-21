package tests.usermanagement;

import base.BaseTest;
import base.NeedLogin;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pages.admin.AddUserPage;
import pages.admin.UserManagementPage;
import utils.TestDataUtil;

public class UserManagementTests_US12_2 extends BaseTest {

    private UserManagementPage userPage;
    private AddUserPage addUser;

    @BeforeMethod
    public void setUp() {
        loginAsAdmin();
        driver.get("http://localhost:4200/admin/users");
        userPage = new UserManagementPage(driver);
    }

    @Test(priority = 1)
    @NeedLogin
    public void TC_12_2_1_DisplayAddUserForm() {
        addUser = userPage.clickAddUser();
        Assert.assertTrue(addUser.isFormDisplayed(), "Form thêm user phải hiển thị.");
    }

    @Test(priority = 2)
    @NeedLogin
    public void TC_12_2_2_CreateUser_Success() {
        addUser = userPage.clickAddUser();

        String username = TestDataUtil.randomName();
        String email = TestDataUtil.randomEmail();
        String password = "Abc@12345";

        addUser.fillForm(username, email, password, "Admin");
        addUser.submit();

        Assert.assertTrue(addUser.getSuccessMessage().contains("Tạo user thành công"),
                "Thông báo tạo user thành công không hiển thị.");
    }

    @Test(priority = 3)
    @NeedLogin
    public void TC_12_2_3_RequiredFieldValidation() {
        addUser = userPage.clickAddUser();

        addUser.fillForm("", "", "", "Admin");
        addUser.submit();

        Assert.assertTrue(addUser.getErrorMessage().contains("bắt buộc"),
                "Thông báo validation trường bắt buộc không hiển thị.");
    }

    @Test(priority = 4)
    @NeedLogin
    public void TC_12_2_4_InvalidEmailValidation() {
        addUser = userPage.clickAddUser();

        addUser.fillForm("hoai123", "invalidEmail", "Abc@12345", "Admin");
        addUser.submit();

        Assert.assertTrue(addUser.getErrorMessage().contains("email không hợp lệ"),
                "Thông báo email không hợp lệ không hiển thị.");
    }

    @Test(priority = 5)
    @NeedLogin
    public void TC_12_2_5_InvalidPasswordValidation() {
        addUser = userPage.clickAddUser();

        addUser.fillForm("hoai123", TestDataUtil.randomEmail(), "123456", "Admin");
        addUser.submit();

        Assert.assertTrue(addUser.getErrorMessage().contains("password không đúng chuẩn"),
                "Thông báo password không đúng chuẩn không hiển thị.");
    }

    @Test(priority = 6)
    @NeedLogin
    public void TC_12_2_6_DuplicateEmailValidation() {
        addUser = userPage.clickAddUser();

        addUser.fillForm("newuser", "hoai@gmail.com", "Abc@12345", "Admin");
        addUser.submit();

        Assert.assertTrue(addUser.getErrorMessage().contains("email đã tồn tại"),
                "Thông báo trùng email không hiển thị.");
    }

    @Test(priority = 7)
    @NeedLogin
    public void TC_12_2_7_DuplicateUsernameValidation() {
        addUser = userPage.clickAddUser();

        addUser.fillForm("hoaine", TestDataUtil.randomEmail(), "Abc@12345", "Admin");
        addUser.submit();

        Assert.assertTrue(addUser.getErrorMessage().contains("username đã tồn tại"),
                "Thông báo trùng username không hiển thị.");
    }

    @Test(priority = 8)
    @NeedLogin
    public void TC_12_2_9_CancelAddUser() {
        addUser = userPage.clickAddUser();

        addUser.cancel();

        // kiểm tra form đã đóng
        Assert.assertTrue(driver.findElements(addUser.getInputUsername()).isEmpty(),
                "Form thêm user phải đóng sau khi hủy.");
    }
}
