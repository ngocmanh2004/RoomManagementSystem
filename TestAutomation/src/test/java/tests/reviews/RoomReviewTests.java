package tests.reviews;

import base.BaseTest;
import base.LoginAs;
import base.NeedLogin;
import base.UserRole;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ReviewFormPage;
import pages.ReviewListPage;

public class RoomReviewTests extends BaseTest {

    String roomUrl = "http://localhost:4200/rooms/1";


    @Test
    @LoginAs(UserRole.TENANT)
    public void selectStarFrom1To5() {
        driver.get(roomUrl);

        ReviewFormPage form = new ReviewFormPage(driver);
        form.selectRating(4);

        Assert.assertEquals(form.getSelectedRating(), 4);
 }

    @Test
    @LoginAs(UserRole.TENANT)
    public void enterCommentSuccessfully() {
        driver.get(roomUrl);

        ReviewFormPage form = new ReviewFormPage(driver);
        form.enterComment("Phòng sạch, thoáng mát");

        Assert.assertFalse(form.isCommentRequiredErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void validateMissingRating() {
        driver.get(roomUrl);

        ReviewFormPage form = new ReviewFormPage(driver);
        form.enterComment("Thiếu sao");
        form.submit();

        Assert.assertTrue(form.isRatingErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void validateMissingComment() {
        driver.get(roomUrl);

        ReviewFormPage form = new ReviewFormPage(driver);
        form.selectRating(5);
        form.submit();

        Assert.assertTrue(form.isCommentRequiredErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void validateCommentMinLength() {
        driver.get(roomUrl);

        ReviewFormPage form = new ReviewFormPage(driver);
        form.selectRating(5);
        form.enterComment("short");
        form.submit();

        Assert.assertTrue(form.isCommentMinLengthErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void submitReviewSuccessfully() {
        driver.get(roomUrl);

        ReviewFormPage form = new ReviewFormPage(driver);
        form.selectRating(5);
        form.enterComment("Phòng rất tốt, chủ trọ thân thiện");
        form.submit();

        ReviewListPage list = new ReviewListPage(driver);
        Assert.assertTrue(list.getReviewCount() > 0);
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void newReviewDisplayedFirst() {
        driver.get(roomUrl);

        String comment = "Review mới " + System.currentTimeMillis();

        ReviewFormPage form = new ReviewFormPage(driver);
        form.selectRating(5);
        form.enterComment(comment);
        form.submit();

        ReviewListPage list = new ReviewListPage(driver);
        Assert.assertEquals(list.getLatestReviewComment(), comment);
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void preventMultipleReviewSubmission() {
        driver.get(roomUrl);

        ReviewFormPage form = new ReviewFormPage(driver);
        form.selectRating(5);
        form.enterComment("Review lần 1");
        form.submit();

        form.enterComment("Review lần 2");
        form.submit();

        Assert.assertTrue(form.isRatingErrorDisplayed()
                || form.isCommentRequiredErrorDisplayed());
    }


    @Test
    @LoginAs(UserRole.TENANT)
    public void onlyOwnerCanSeeEditButton() {
        driver.get(roomUrl);

        ReviewListPage list = new ReviewListPage(driver);
        Assert.assertTrue(list.isEditButtonVisible());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void openEditFormSuccessfully() {
        driver.get(roomUrl);

        ReviewListPage list = new ReviewListPage(driver);
        list.clickEdit();

        ReviewFormPage form = new ReviewFormPage(driver);
        Assert.assertFalse(form.isCommentRequiredErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void editReviewSuccessfully() {
        driver.get(roomUrl);

        ReviewListPage list = new ReviewListPage(driver);
        list.clickEdit();

        ReviewFormPage form = new ReviewFormPage(driver);
        form.enterComment("Đã chỉnh sửa nội dung");
        form.submit();

        Assert.assertEquals(
                list.getLatestReviewComment(),
                "Đã chỉnh sửa nội dung"
        );
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void editReviewCommentCannotBeEmpty() {
        driver.get(roomUrl);

        ReviewListPage list = new ReviewListPage(driver);
        list.clickEdit();

        ReviewFormPage form = new ReviewFormPage(driver);
        form.enterComment("");
        form.submit();

        Assert.assertTrue(form.isCommentRequiredErrorDisplayed());
    }


    @Test
    @LoginAs(UserRole.TENANT)
    public void onlyOwnerCanSeeDeleteButton() {
        driver.get(roomUrl);

        ReviewListPage list = new ReviewListPage(driver);
        Assert.assertTrue(list.isDeleteButtonVisible());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void deleteReviewSuccessfully() {
        driver.get(roomUrl);

        ReviewListPage list = new ReviewListPage(driver);
        int before = list.getReviewCount();

        list.clickDelete();
        // assume confirm dialog auto accept

        int after = list.getReviewCount();
        Assert.assertTrue(after < before);
    }
}
