package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RoomDetailPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ===============================
    // ROOM INFO
    // ===============================
    @FindBy(css = ".header-section h1")
    private WebElement lblRoomName;

    @FindBy(css = ".info-bar div:nth-child(1) strong")
    private WebElement lblRoomPrice;

    @FindBy(xpath = "//p[@class='address']")
    private WebElement lblRoomArea;


    // ===============================
    // REVIEW LIST
    // ===============================
    @FindBy(css = ".review-item")
    private List<WebElement> reviewList;

    @FindBy(css = ".review-item .reviewer-name")
    private List<WebElement> reviewerNames;

    @FindBy(css = ".review-item .stars i.filled")
    private List<WebElement> reviewerStarsAll;

    @FindBy(css = ".review-item .comment")
    private List<WebElement> reviewerComments;

    @FindBy(css = ".review-item .date")
    private List<WebElement> reviewDates;


    @FindBy(xpath = "//*[contains(text(),'Chưa có đánh giá')]")
    private WebElement noReviewMsg;


    // ===============================
    // REVIEW FORM
    // ===============================
    @FindBy(css = ".rating-star")
    private List<WebElement> starButtons;

    @FindBy(css = "textarea.review-comment")
    private WebElement commentInput;

    @FindBy(css = "button.submit-review")
    private WebElement submitBtn;

    @FindBy(css = ".rating-error")
    private WebElement ratingError;

    @FindBy(css = ".comment-error")
    private WebElement commentError;


    // ===============================
    // EDIT MODE
    // ===============================
    @FindBy(xpath = "//*[contains(text(), 'Bạn đã đánh giá')]")
    private WebElement editLabel;

    @FindBy(css = ".review-item .btn-edit")
    private List<WebElement> editButtons;

    @FindBy(css = ".review-form")
    private WebElement reviewForm;

	 // ===============================
	 // DELETE REVIEW ACTIONS (US 11.4)
	 // ===============================
	
	 // Nút delete chỉ dành cho review của user hiện tại
	 @FindBy(css = ".review-item .btn-delete")
	 private List<WebElement> deleteButtons;
	
	 // Popup confirm delete
	 @FindBy(css = ".confirm-dialog")
	 private WebElement confirmDialog;
	
	 @FindBy(css = ".confirm-dialog .btn-confirm")
	 private WebElement btnConfirmDelete;
	
	 @FindBy(css = ".confirm-dialog .btn-cancel")
	 private WebElement btnCancelDelete;
	 
    // ===============================
    // CONSTRUCTOR
    // ===============================
    public RoomDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }


    // ===============================
    // ROOM INFO GETTERS
    // ===============================
    public String getRoomName() {
        return lblRoomName.getText();
    }

    public String getRoomPrice() {
        return lblRoomPrice.getText();
    }

    public String getRoomArea() {
        return lblRoomArea.getText();
    }


    // ===============================
    // REVIEW -- READING EXISTING
    // ===============================
    public int getReviewCount() {
        return reviewList.size();
    }

    public String getReviewerName(int index) {
        return reviewerNames.get(index).getText();
    }

    public int getReviewerStars(int index) {
        WebElement review = reviewList.get(index);
        return review.findElements(By.cssSelector(".stars i.filled")).size();
    }

    public String getReviewerComment(int index) {
        return reviewerComments.get(index).getText();
    }

    public String getReviewDate(int index) {
        return reviewDates.get(index).getText();
    }

    public boolean isNoReviewShown() {
        try {
            return noReviewMsg.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    // ===============================
    // REVIEW FORM ACTIONS
    // ===============================
    public boolean isSubmitEnabled() {
        return submitBtn.isEnabled();
    }

    public void clickSubmit() {
        submitBtn.click();
    }

    public void selectRating(int stars) {
        if (stars >= 1 && stars <= starButtons.size()) {
            starButtons.get(stars - 1).click();
        }
    }

    public void enterComment(String text) {
        commentInput.clear();
        commentInput.sendKeys(text);
    }

    public String getCommentValue() {
        return commentInput.getAttribute("value");
    }


    // ===============================
    // VALIDATION MESSAGES
    // ===============================
    public String getRatingError() {
        return ratingError.getText();
    }

    public String getCommentError() {
        return commentError.getText();
    }


    // ===============================
    // EDIT REVIEW
    // ===============================
    public boolean isEditMode() {
        try {
            return editLabel.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEditButtonVisible() {
        return !editButtons.isEmpty();
    }

    public void clickEdit() {
        if (!editButtons.isEmpty()) {
            editButtons.get(0).click();
        }
    }

    public boolean isFormDisplayed() {
        return reviewForm.isDisplayed();
    }

    public String getFirstReviewComment() {
        if (reviewList.isEmpty()) return "";
        return reviewerComments.get(0).getText();
    }
	    
	 // ===============================
	 // Kiểm tra nút delete có hiển thị cho user hiện tại?
	 // ===============================
	 public boolean isDeleteButtonVisible() {
	     return !deleteButtons.isEmpty();
	 }
	
	 // ===============================
	 // Click delete cho review của user
	 // ===============================
	 public void clickDelete() {
	     if (!deleteButtons.isEmpty()) {
	         deleteButtons.get(0).click();
	     }
	 }
	
	 // ===============================
	 // Dialog xác nhận có hiển thị không
	 // ===============================
	 public boolean isDeleteConfirmationShown() {
	     try {
	         return confirmDialog.isDisplayed();
	     } catch (Exception e) {
	         return false;
	     }
	 }
	
	 // ===============================
	 // Xác nhận xóa
	 // ===============================
	 public void confirmDelete() {
	     btnConfirmDelete.click();
	 }
	
	 // ===============================
	 // Hủy xóa
	 // ===============================
	 public void cancelDelete() {
	     btnCancelDelete.click();
	 }
	
	 // ===============================
	 // Kiểm tra review của current user còn tồn tại không
	 // ===============================
	 // Quy ước: review của user hiện tại luôn có nút Edit/Delete
	 public boolean isReviewPresentForCurrentUser() {
	     return !deleteButtons.isEmpty();
	 }
	
	 // ===============================
	 // ADMIN – kiểm tra delete hiển thị cho tất cả review
	 // ===============================
	 public boolean isDeleteButtonVisibleForAnyReview() {
	     return deleteButtons.size() == reviewList.size();
	 }
	
	 // ===============================
	 // ADMIN – Click delete theo index
	 // ===============================
	 public void clickDeleteForReview(int index) {
	     deleteButtons.get(index).click();
	 }
	
	 // ===============================
	 // Kiểm tra review theo index còn tồn tại hay không
	 // ===============================
	 public boolean isReviewPresent(int index) {
	     try {
	         return reviewList.get(index).isDisplayed();
	     } catch (Exception e) {
	         return false;
	     }
	 }
}
