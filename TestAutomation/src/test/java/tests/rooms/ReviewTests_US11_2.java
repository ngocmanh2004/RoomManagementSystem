package tests.rooms;

import base.BaseTest;
import base.NeedLogin;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RoomDetailPage;

public class ReviewTests_US11_2 extends BaseTest {

    @Test(priority = 1, description = "TC 11.2.1 - Người đã thuê có thể đánh giá")
    @NeedLogin
    public void TC_11_2_1_UserWithRentalCanReview() {
        navigateToRoom(1); // phòng đã thuê
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        Assert.assertTrue(roomPage.isSubmitEnabled(), "Người đã thuê nên có thể gửi review");
    }

    @Test(priority = 2, description = "TC 11.2.2 - Người chưa thuê không được đánh giá")
    @NeedLogin
    public void TC_11_2_2_UserNeverRentedCannotReview() {
        navigateToRoom(2); // phòng chưa thuê
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        Assert.assertFalse(roomPage.isSubmitEnabled(), "Người chưa thuê không nên có nút submit");
    }

    @Test(priority = 3, description = "TC 11.2.3 - Yêu cầu đăng nhập trước khi đánh giá")
    public void TC_11_2_3_RequireLoginBeforeReview() {
        logout();
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.clickSubmit(); // thử submit khi chưa login
        Assert.assertTrue(driver.getCurrentUrl().contains("login"), "Phải redirect tới login nếu chưa đăng nhập");
    }

    @Test(priority = 4, description = "TC 11.2.4 - Người dùng chọn số sao 1–5")
    @NeedLogin
    public void TC_11_2_4_SelectStarRating() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.selectRating(4);
        Assert.assertTrue(roomPage.isSubmitEnabled(), "Sau khi chọn rating, submit phải enable");
    }

    @Test(priority = 5, description = "TC 11.2.5 - Trường bình luận cho phép nhập")
    @NeedLogin
    public void TC_11_2_5_CommentInputWorks() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        String comment = "Phòng sạch, rộng rãi";
        roomPage.enterComment(comment);
        Assert.assertEquals(roomPage.getCommentValue(), comment, "Comment nhập vào phải đúng");
    }

    @Test(priority = 6, description = "TC 11.2.6 - Validate bỏ trống số sao")
    @NeedLogin
    public void TC_11_2_6_EmptyRatingValidation() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.selectRating(0);
        roomPage.clickSubmit();
        Assert.assertEquals(roomPage.getRatingError(), "Vui lòng chọn số sao", "Phải báo lỗi khi bỏ trống rating");
    }

    @Test(priority = 7, description = "TC 11.2.7 - Validate bỏ trống bình luận")
    @NeedLogin
    public void TC_11_2_7_EmptyCommentValidation() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.enterComment("");
        roomPage.clickSubmit();
        Assert.assertEquals(roomPage.getCommentError(), "Bình luận không được để trống", "Phải báo lỗi khi comment trống");
    }

    @Test(priority = 8, description = "TC 11.2.8 - Giới hạn độ dài bình luận")
    @NeedLogin
    public void TC_11_2_8_CommentMaxLength() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        String longComment = "a".repeat(1100);
        roomPage.enterComment(longComment);
        Assert.assertEquals(roomPage.getCommentValue().length(), 1000, "Comment không được vượt quá 1000 ký tự");
    }

    @Test(priority = 9, description = "TC 11.2.9 - Gửi đánh giá thành công")
    @NeedLogin
    public void TC_11_2_9_SubmitReviewSuccess() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        roomPage.selectRating(5);
        roomPage.enterComment("Phòng tuyệt vời");
        roomPage.clickSubmit();

        String firstComment = roomPage.getFirstReviewComment();
        Assert.assertTrue(firstComment.contains("Phòng tuyệt vời"), "Review mới phải xuất hiện đầu danh sách");
    }

    @Test(priority = 10, description = "TC 11.2.10 - Review mới hiển thị đầu danh sách")
    @NeedLogin
    public void TC_11_2_10_NewReviewOnTop() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        String firstComment = roomPage.getFirstReviewComment();
        Assert.assertEquals(firstComment, "Phòng tuyệt vời", "Review mới phải đứng đầu danh sách");
    }

    @Test(priority = 11, description = "TC 11.2.11 - Người dùng không thể gửi nhiều review liên tiếp")
    @NeedLogin
    public void TC_11_2_11_UserCannotSubmitManyReviews() {
        navigateToRoom(1);
        RoomDetailPage roomPage = new RoomDetailPage(driver);
        Assert.assertTrue(roomPage.isEditMode(), "Nếu user đã gửi review, form phải ở chế độ Edit");
    }
}
