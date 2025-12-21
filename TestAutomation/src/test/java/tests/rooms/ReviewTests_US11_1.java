package tests.rooms;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RoomDetailPage;

public class ReviewTests_US11_1 extends BaseTest {

    @Test(priority = 1, description = "TC 11.1.1 - Xác minh danh sách đánh giá hiển thị khi phòng có đánh giá")
    public void verifyReviewListDisplayed() {
        driver.get("http://localhost:4200/room/1"); // phòng có review

        RoomDetailPage page = new RoomDetailPage(driver);

        int count = page.getReviewCount();
        System.out.println("Số lượng review: " + count);

        Assert.assertTrue(count > 0, "Danh sách review phải hiển thị > 0");
    }

    @Test(priority = 2, description = "TC 11.1.2 - Xác minh tên người thuê hiển thị đúng")
    public void verifyReviewerName() {
        driver.get("http://localhost:4200/room/1");

        RoomDetailPage page = new RoomDetailPage(driver);
        String name = page.getReviewerName(0);

        System.out.println("Tên reviewer: " + name);

        Assert.assertEquals(
                name,
                "Nguyễn Văn A",
                "Tên người thuê không đúng"
        );
    }

    @Test(priority = 3, description = "TC 11.1.3 - Xác minh số sao hiển thị đúng theo rating")
    public void verifyReviewStars() {
        driver.get("http://localhost:4200/room/1");

        RoomDetailPage page = new RoomDetailPage(driver);
        int stars = page.getReviewerStars(0);

        System.out.println("Số sao: " + stars);

        Assert.assertEquals(stars, 4, "Rating hiển thị sai");
    }

    @Test(priority = 4, description = "TC 11.1.4 - Xác minh nội dung bình luận đúng theo DB")
    public void verifyReviewComment() {
        driver.get("http://localhost:4200/room/1");

        RoomDetailPage page = new RoomDetailPage(driver);
        String comment = page.getReviewerComment(0);

        Assert.assertEquals(
                comment,
                "Phòng sạch, chủ thân thiện",
                "Comment hiển thị sai"
        );
    }

    @Test(priority = 5, description = "TC 11.1.5 - Xác minh ngày đăng hiển thị đúng định dạng")
    public void verifyReviewDateFormat() {
        driver.get("http://localhost:4200/room/1");

        RoomDetailPage page = new RoomDetailPage(driver);

        String date = page.getReviewDate(0);
        System.out.println("Ngày đăng: " + date);

        Assert.assertTrue(
                date.matches("^\\d{2}/\\d{2}/\\d{4}$"),
                "Ngày đăng không đúng format dd/MM/yyyy"
        );
    }

    @Test(priority = 6, description = "TC 11.1.6 - Hiển thị thông báo khi phòng KHÔNG có đánh giá")
    public void verifyNoReviewMessage() {
        driver.get("http://localhost:4200/room/999"); // phòng không có review

        RoomDetailPage page = new RoomDetailPage(driver);

        Assert.assertTrue(
                page.isNoReviewShown(),
                "Không hiển thị thông báo 'Chưa có đánh giá'"
        );
    }
}
