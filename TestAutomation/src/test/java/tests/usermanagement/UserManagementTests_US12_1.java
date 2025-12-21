package tests.usermanagement;

import base.BaseTest;
import base.NeedLogin;
import pages.admin.UserManagementPage;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserManagementTests_US12_1 extends BaseTest {

    private UserManagementPage userPage;

    @BeforeMethod
    public void setUp() {
        loginAsAdmin();
        driver.get("http://localhost:4200/admin/users");
        userPage = new UserManagementPage(driver);
    }

    // ---------------------------------------------------------
    // TC 12.1.1 – Xác minh hiển thị danh sách người dùng mặc định
    // ---------------------------------------------------------
    @Test(priority = 1, description = "TC 12.1.1 - Xác minh danh sách user mặc định hiển thị")
    public void TC_12_1_1_verifyDefaultUserListDisplayed() {
        int count = userPage.getUserCount();
        Assert.assertTrue(count > 0, "Danh sách user phải hiển thị ít nhất 1 bản ghi.");
    }

    // ---------------------------------------------------------
    // TC 12.1.2 – Kiểm tra phân trang
    // ---------------------------------------------------------
    @Test(priority = 2, description = "TC 12.1.2 - Kiểm tra phân trang user")
    public void TC_12_1_2_verifyPagination() {
        String page1 = userPage.getCurrentPageText();
        userPage.nextPage();
        String page2 = userPage.getCurrentPageText();
        Assert.assertNotEquals(page1, page2, "Next Page phải chuyển sang trang khác.");

        userPage.prevPage();
        String pageBack = userPage.getCurrentPageText();
        Assert.assertEquals(page1, pageBack, "Previous Page phải quay lại trang cũ.");
    }

    // ---------------------------------------------------------
    // TC 12.1.3 – Tìm kiếm user theo tên hoặc email
    // ---------------------------------------------------------
    @Test(priority = 3, description = "TC 12.1.3 - Tìm kiếm user theo tên hoặc email")
    public void TC_12_1_3_verifySearchByNameOrEmail() {
        userPage.searchUser("hoai"); // keyword mẫu
        int count = userPage.getUserCount();
        Assert.assertTrue(count > 0, "Kết quả tìm kiếm phải trả về user liên quan.");
    }

    // ---------------------------------------------------------
    // TC 12.1.4 – Lọc user theo Role
    // ---------------------------------------------------------
    @Test(priority = 4, description = "TC 12.1.4 - Lọc user theo Role (Tenant / Host / Admin)")
    public void TC_12_1_4_verifyFilterRole() {
        userPage.selectRole("0");
        Assert.assertEquals(userPage.getUserRole(0), "Tenant", "Kết quả role phải là Tenant.");

        userPage.selectRole("1");
        Assert.assertEquals(userPage.getUserRole(0), "Host", "Kết quả role phải là Host.");

        userPage.selectRole("2");
        Assert.assertEquals(userPage.getUserRole(0), "Admin", "Kết quả role phải là Admin.");
    }

    // ---------------------------------------------------------
    // TC 12.1.5 – Lọc theo trạng thái ACTIVE/BANNED
    // ---------------------------------------------------------
    @Test(priority = 5, description = "TC 12.1.5 - Lọc user theo trạng thái ACTIVE/BANNED")
    public void TC_12_1_5_verifyFilterStatus() {
        userPage.selectStatus("ACTIVE");
        Assert.assertEquals(userPage.getUserStatus(0), "ACTIVE", "Kết quả phải toàn user ACTIVE.");

        userPage.selectStatus("BANNED");
        Assert.assertEquals(userPage.getUserStatus(0), "BANNED", "Kết quả phải toàn user BANNED.");
    }

    // ---------------------------------------------------------
    // TC 12.1.6 – Kết hợp search + filter role + filter status
    // ---------------------------------------------------------
    @Test(priority = 6, description = "TC 12.1.6 - Kết hợp search + filter role + filter status")
    public void TC_12_1_6_verifySearchAndFilterCombination() {
        userPage.searchUser("hoai");
        userPage.selectRole("0");
        userPage.selectStatus("ACTIVE");

        Assert.assertTrue(userPage.getUserFullName(0).toLowerCase().contains("hoai"), 
                          "Tên user phải chứa keyword tìm kiếm.");
        Assert.assertEquals(userPage.getUserRole(0), "Tenant", "Role phải là Tenant.");
        Assert.assertEquals(userPage.getUserStatus(0), "ACTIVE", "Trạng thái phải là ACTIVE.");
    }

    // ---------------------------------------------------------
    // TC 12.1.7 – Xem chi tiết user
    // ---------------------------------------------------------
    @Test(priority = 7, description = "TC 12.1.7 - Xem chi tiết user")
    public void TC_12_1_7_verifyViewUserDetail() {
        String selectedUser = userPage.getUserFullName(0);
        userPage.openEditUser(0);

        String modalTitle = driver.findElement(By.cssSelector(".modal-title")).getText();
        Assert.assertTrue(modalTitle.contains(selectedUser), 
                          "Màn hình chi tiết phải hiển thị đúng user đã chọn.");
    }
}
