package tests.rooms;

import base.BaseTest;
import base.NeedLogin;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RoomDetailPage;

public class ReviewTests_US11_4 extends BaseTest {

    @Test(priority = 1, description = "TC 11.4.1 - Chỉ hiển thị nút Xóa cho đánh giá của user hiện tại")
    @NeedLogin
    public void TC_11_4_1_DeleteButtonVisibleForOwner() {
        navigateToRoom(1); // phòng có review của user
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        Assert.assertTrue(roomPage.isDeleteButtonVisible(), "Nút Delete chỉ hiển thị với review của user");
    }

    @Test(priority = 2, description = "TC 11.4.2 - Hiển thị hộp thoại xác nhận trước khi xóa")
    @NeedLogin
    public void TC_11_4_2_ConfirmDialogShown() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.clickDelete();
        Assert.assertTrue(roomPage.isDeleteConfirmationShown(), "Phải hiển thị dialog xác nhận trước khi xóa");
    }

    @Test(priority = 3, description = "TC 11.4.3 - Xóa đánh giá thành công")
    @NeedLogin
    public void TC_11_4_3_DeleteReviewSuccess() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.clickDelete();
        roomPage.confirmDelete();
        Assert.assertFalse(roomPage.isReviewPresentForCurrentUser(), "Review của user phải bị xóa thành công");
    }

    @Test(priority = 4, description = "TC 11.4.4 - Không xóa khi chọn Hủy")
    @NeedLogin
    public void TC_11_4_4_CancelDelete() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.clickDelete();
        roomPage.cancelDelete();
        Assert.assertTrue(roomPage.isReviewPresentForCurrentUser(), "Review vẫn tồn tại khi user hủy xóa");
    }

    @Test(priority = 5, description = "TC 11.4.5 - Admin có thể xóa bất kỳ review nào")
    public void TC_11_4_5_AdminCanDeleteAnyReview() {
        loginAsAdmin();
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        Assert.assertTrue(roomPage.isDeleteButtonVisibleForAnyReview(), "Admin phải thấy nút Delete cho mọi review");
        roomPage.clickDeleteForReview(0); // xóa review đầu tiên
        roomPage.confirmDelete();
        Assert.assertFalse(roomPage.isReviewPresent(0), "Review đầu tiên phải bị xóa");
    }

    @Test(priority = 6, description = "TC 11.4.6 - Danh sách review tự cập nhật khi Admin xóa")
    public void TC_11_4_6_ReviewListUpdatesAfterDelete() {
        loginAsAdmin();
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        int beforeCount = roomPage.getReviewCount();
        roomPage.clickDeleteForReview(0);
        roomPage.confirmDelete();
        int afterCount = roomPage.getReviewCount();
        Assert.assertEquals(afterCount, beforeCount - 1, "Số lượng review phải giảm 1 sau khi xóa");
    }
}
