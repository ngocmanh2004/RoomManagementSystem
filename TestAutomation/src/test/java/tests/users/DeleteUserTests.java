package tests.users;

import base.BaseTest;
import base.LoginAs;
import base.NeedLogin;
import base.UserRole;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.UserManagementPage;

public class DeleteUserTests extends BaseTest {

	 UserManagementPage page;

	    @BeforeMethod
	    public void setup() {
	        page = new UserManagementPage(driver);
	    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testDeleteButtonVisible() {

        Assert.assertTrue(page.isDeleteButtonVisible(0), "Nút Xóa không hiển thị cho user đầu tiên");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testDeleteModalDisplayed() {

        page.clickDeleteButton(7);
        Assert.assertTrue(page.isConfirmModalDisplayed(), "Hộp thoại xác nhận xóa không hiển thị");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testCancelDeleteUser() {

        page.clickDeleteButton(7);
        page.cancelAction();
        Assert.assertFalse(page.isConfirmModalDisplayed(), "Hộp thoại không đóng khi nhấn Hủy");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testDeleteUserSuccess() {

        String usernameToDelete = "NguoyenH";
        Assert.assertTrue(page.isUserInList(usernameToDelete), "User không tồn tại trước khi xóa");

        int countBefore = page.getUserCount();
        page.clickDeleteButton(12);
        page.confirmAction();

        int countAfter = page.getUserCount();
        Assert.assertEquals(countAfter, countBefore - 1, "Số lượng user sau khi xóa không giảm");
        Assert.assertFalse(page.isUserInList(usernameToDelete), "User vẫn còn trong danh sách sau khi xóa");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testCannotDeleteAdmin() {

        Assert.assertFalse(page.isDeleteButtonVisible(0), "Cho phép xóa Admin");
    }
}
