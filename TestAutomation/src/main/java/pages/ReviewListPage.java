package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReviewListPage {

    WebDriver driver;

    By reviewCards = By.cssSelector(".review-card");
    By firstReview = By.cssSelector(".review-card:first-child");
    By reviewComment = By.cssSelector(".review-card:first-child .review-comment p");
    By reviewAuthor = By.cssSelector(".review-card:first-child .reviewer-name");
    By editBtn = By.cssSelector(".review-card:first-child .btn-edit");
    By deleteBtn = By.cssSelector(".review-card:first-child .btn-delete");
    By reportBtn = By.cssSelector(".review-card:first-child .btn-report");

    public ReviewListPage(WebDriver driver) {
        this.driver = driver;
    }


    public String getLatestReviewComment() {
        return driver.findElement(reviewComment).getText();
    }

    public String getLatestReviewAuthor() {
        return driver.findElement(reviewAuthor).getText();
    }

    public int getReviewCount() {
        return driver.findElements(reviewCards).size();
    }


    public boolean isEditButtonVisible() {
        return driver.findElements(editBtn).size() > 0;
    }

    public boolean isDeleteButtonVisible() {
        return driver.findElements(deleteBtn).size() > 0;
    }

    public boolean isReportButtonVisible() {
        return driver.findElements(reportBtn).size() > 0;
    }


    public void clickEdit() {
        driver.findElement(editBtn).click();
    }

    public void clickDelete() {
        driver.findElement(deleteBtn).click();
    }
}
