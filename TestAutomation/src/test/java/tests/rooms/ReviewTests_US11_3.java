package tests.rooms;

import base.BaseTest;
import base.NeedLogin;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RoomDetailPage;

public class ReviewTests_US11_3 extends BaseTest {

    @Test(priority = 1, description = "TC 11.3.1 - Chỉ hiển thị nút Chỉnh sửa cho đánh giá của người dùng hiện tại")
    @NeedLogin
    public void EditButtonVisibleForOwner() {
        navigateToRoom(1); // phòng có review của user hiện tại
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        Assert.assertTrue(roomPage.isEditButtonVisible(), "Nút Edit phải hiển thị với review của chính user");
    }

    @Test(priority = 2, description = "TC 11.3.2 - Mở form chỉnh sửa thành công")
    @NeedLogin
    public void OpenEditForm() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.clickEdit();
        Assert.assertTrue(roomPage.isEditMode(), "Form phải chuyển sang chế độ Edit");
        Assert.assertTrue(roomPage.isFormDisplayed(), "Form chỉnh sửa phải hiển thị");
    }

    @Test(priority = 3, description = "TC 11.3.3 - Chỉnh sửa đánh giá thành công")
    @NeedLogin
    public void EditReviewSuccess() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.clickEdit();
        roomPage.selectRating(4);
        String newComment = "Phòng đã sửa review thành công";
        roomPage.enterComment(newComment);
        roomPage.clickSubmit();

        String firstComment = roomPage.getFirstReviewComment();
        Assert.assertEquals(firstComment, newComment, "Review sau khi chỉnh sửa phải cập nhật");
    }

    @Test(priority = 4, description = "TC 11.3.4 - Hệ thống chặn rating không hợp lệ")
    @NeedLogin
    public void InvalidRatingBlocked() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.clickEdit();
        roomPage.selectRating(0); // rating không hợp lệ
        roomPage.clickSubmit();
        Assert.assertEquals(roomPage.getRatingError(), "Vui lòng chọn số sao", "Phải báo lỗi rating không hợp lệ");
    }

    @Test(priority = 5, description = "TC 11.3.5 - Comment không được để trống")
    @NeedLogin
    public void EmptyCommentBlocked() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.clickEdit();
        roomPage.enterComment(""); // comment trống
        roomPage.clickSubmit();
        Assert.assertEquals(roomPage.getCommentError(), "Bình luận không được để trống", "Phải báo lỗi khi comment trống");
    }
}
