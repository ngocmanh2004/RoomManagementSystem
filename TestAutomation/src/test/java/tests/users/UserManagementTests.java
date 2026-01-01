package tests.users;

import base.BaseTest;

import base.LoginAs;
import base.NeedLogin;
import base.UserRole;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.UserManagementPage;
import org.openqa.selenium.JavascriptExecutor;

public class UserManagementTests extends BaseTest {

	 UserManagementPage page;
	 JavascriptExecutor js = (JavascriptExecutor) driver;

	    @BeforeMethod
	    public void setupPage() {
	        page = new UserManagementPage(driver);
	    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testDefaultUserListDisplayed() {

        Assert.assertTrue(page.getAllRows().size() > 0, "Danh sách người dùng không hiển thị mặc định");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testPagination() {

        page.scrollToPagination();


        int firstPage = page.getCurrentPageNumber();

        page.clickNextPage();
        page.waitForPageChange(firstPage);

        Assert.assertEquals(
            page.getCurrentPageNumber(),
            firstPage + 1
        );

        page.clickPrevPage();
        page.waitForPageChange(firstPage + 1);

        Assert.assertEquals(
            page.getCurrentPageNumber(),
            firstPage
        );
    }


    @Test
    @LoginAs(UserRole.ADMIN)
    public void testSearchByNameOrEmail() {

        String keyword = "hoai";
        page.searchUser(keyword);

        Assert.assertTrue(page.isUserInList(keyword), "Không tìm thấy user theo keyword: " + keyword);
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testFilterByRole() {
        page.selectRoleByValue("1"); 

        Assert.assertTrue(
            page.getAllRows().stream()
                .allMatch(row -> row.getText().contains("Chủ trọ")),
            "Không lọc đúng role Chủ trọ"
        );
    }
    
    @Test
    @LoginAs(UserRole.ADMIN)
    public void testFilterByStatus() {
        page.selectStatusByValue("ACTIVE");

        Assert.assertTrue(
            page.getAllRows().stream()
                .allMatch(row -> row.getText().contains("Hoạt động")),
            "Không lọc đúng trạng thái ACTIVE"
        );
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void testSearchAndFilterCombined() {
        page.searchUser("hoai");
        page.selectRoleByValue("1");
        page.selectStatusByValue("ACTIVE");

        Assert.assertTrue(
            page.getAllRows().stream().allMatch(row ->
                row.getText().toLowerCase().contains("hoai") &&
                row.getText().contains("Chủ trọ") &&
                row.getText().contains("Hoạt động")
            ),
            "Kết hợp tìm kiếm + lọc role + lọc status không đúng"
        );
    }

}
