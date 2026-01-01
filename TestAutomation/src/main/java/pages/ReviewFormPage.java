package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReviewFormPage {

    WebDriver driver;
    private StarRatingComponent starRating;

    By reviewForm = By.cssSelector(".review-form");

    By commentTextarea = By.id("comment");

    By submitBtn = By.cssSelector(".review-form .btn-submit");
    By cancelBtn = By.cssSelector(".review-form .btn-cancel");

    By ratingError = By.cssSelector(".form-group .error-message");
    By commentRequiredError = By.xpath("//span[contains(text(),'không được để trống')]");
    By commentMinLengthError = By.xpath("//span[contains(text(),'tối thiểu')]");

    public ReviewFormPage(WebDriver driver) {
        this.driver = driver;
        this.starRating = new StarRatingComponent(driver);
    }

    public void selectRating(int star) {
        starRating.selectStar(star);
    }

    public int getSelectedRating() {
        return starRating.getSelectedRating();
    }
    public void enterComment(String text) {
        driver.findElement(commentTextarea).clear();
        driver.findElement(commentTextarea).sendKeys(text);
    }

    public void submit() {
        driver.findElement(submitBtn).click();
    }

    public void cancel() {
        driver.findElement(cancelBtn).click();
    }


    public boolean isRatingErrorDisplayed() {
        return driver.findElements(ratingError).size() > 0;
    }

    public boolean isCommentRequiredErrorDisplayed() {
        return driver.findElements(commentRequiredError).size() > 0;
    }

    public boolean isCommentMinLengthErrorDisplayed() {
        return driver.findElements(commentMinLengthError).size() > 0;
    }
}
