package tests.users;

import pages.EditUserPage;
import base.BaseTest;
import base.LoginAs;
import base.NeedLogin;
import base.UserRole;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.UserManagementPage;

public class UpdateUserTest extends BaseTest {
	
	 UserManagementPage page;

	  @BeforeMethod
	    public void setup() {
	        page = new UserManagementPage(driver);
	    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void openEditFormSuccessfully() {
        UserManagementPage userPage = new UserManagementPage(driver);
        userPage.clickEditButton(0);

        EditUserPage editPage = new EditUserPage(driver);
        Assert.assertTrue(editPage.isEditFormDisplayed());
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void requiredFieldValidation() {
        new UserManagementPage(driver).clickEditButton(0);
        EditUserPage editPage = new EditUserPage(driver);

        editPage.clearFullName();
        editPage.clickSave();

        Assert.assertTrue(editPage.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void invalidEmailFormat() {
        new UserManagementPage(driver).clickEditButton(0);
        EditUserPage editPage = new EditUserPage(driver);

        editPage.setEmail("abc@@gmail");
        editPage.clickSave();

        Assert.assertTrue(editPage.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void duplicateEmailValidation() {
        new UserManagementPage(driver).clickEditButton(0);
        EditUserPage editPage = new EditUserPage(driver);

        editPage.setEmail("dungpham@gmail.com");
        editPage.clickSave();

        Assert.assertTrue(editPage.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void updateUserSuccessfully() {
        new UserManagementPage(driver).clickEditButton(0);
        EditUserPage editPage = new EditUserPage(driver);

        editPage.setFullName("Hoai ne");
        editPage.setPhone("0987652321");
        
        String alertText = editPage.clickSave();

        Assert.assertTrue(
            alertText.toLowerCase().contains("thành công"),
            "Không hiển thị alert thành công"
        );
        }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void updateRoleSuccessfully() {
        page.clickEditButton(0);
        EditUserPage editPage = new EditUserPage(driver);

        editPage.selectRole("1");
        String alertText = editPage.clickSave();

        Assert.assertTrue(alertText.contains("thành công"));
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void updateStatusSuccessfully() {
        page.clickEditButton(0);
        EditUserPage editPage = new EditUserPage(driver);

        editPage.selectStatus("BANNED"); 

        String alertText = editPage.clickSave();

        Assert.assertTrue(alertText.contains("thành công"));
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void cancelEditForm() {
        new UserManagementPage(driver).clickEditButton(0);
        EditUserPage editPage = new EditUserPage(driver);

        editPage.clickCancel();

    }
}
