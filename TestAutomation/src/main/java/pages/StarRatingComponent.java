package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class StarRatingComponent {

    WebDriver driver;

    By starContainer = By.cssSelector(".star-rating-container");
    By stars = By.cssSelector(".star-rating-container .stars span");
    By ratingText = By.cssSelector(".star-rating-container .rating-text");

    public StarRatingComponent(WebDriver driver) {
        this.driver = driver;
    }

    public void selectStar(int star) {
        List<WebElement> starList = driver.findElements(stars);
        starList.get(star - 1).click();
    }

    public int getSelectedRating() {
        String text = driver.findElement(ratingText).getText();
        return Integer.parseInt(text.split("/")[0]);
    }

    public int getStarCount() {
        return driver.findElements(stars).size();
    }
}
