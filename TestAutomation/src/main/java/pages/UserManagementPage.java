package pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class UserManagementPage {

    private WebDriver driver;

    private By tableRows = By.cssSelector("table tbody tr:not(.no-data)");
    private By deleteBtnInRow = By.cssSelector(".btn-icon.delete");
    private By editBtnInRow = By.cssSelector(".btn-icon.edit");
    private By lockBtnInRow = By.cssSelector(".btn-icon.lock");

    private By searchInput = By.cssSelector(".search-input");
    private By roleSelect = By.xpath("//select[@class='filter-select'][1]");
    private By statusSelect = By.xpath("//select[@class='filter-select'][2]");
 
    private By prevPageBtn = By.xpath("//div[@class='pagination-controls']//button[1]");
    private By nextPageBtn = By.xpath("//div[@class='pagination-controls']//button[2]");
    private By currentPageLabel = By.xpath("//span[contains(@class,'current-page')]");

    private By addUserBtn = By.cssSelector(".page-header .btn.btn-primary");

    private By confirmModal = By.cssSelector(".modal-overlay .modal-content");
    private By confirmBtn = By.cssSelector(".modal-footer .btn.btn-danger");
    private By cancelBtn = By.cssSelector(".modal-footer .btn.btn-secondary");
    
    private By statusBadge = By.cssSelector("td span.badge");

    public UserManagementPage(WebDriver driver) {
        this.driver = driver;
    }


    public List<WebElement> getAllRows() {
        return driver.findElements(tableRows);
    }

    public int getUserCount() {
        return getAllRows().size();
    }

    public boolean isUserInList(String keyword) {
        return getAllRows()
                .stream()
                .anyMatch(row -> row.getText().contains(keyword));
    }

    public boolean isDeleteButtonVisible(int rowIndex) {
        return getAllRows().get(rowIndex)
                .findElement(deleteBtnInRow)
                .isDisplayed();
    }

    public void clickDeleteButton(int rowIndex) {
        getAllRows().get(rowIndex)
                .findElement(deleteBtnInRow)
                .click();
    }

    public boolean isEditButtonVisible(int rowIndex) {
        return getAllRows().get(rowIndex)
                .findElement(editBtnInRow)
                .isDisplayed();
    }

    public void clickEditButton(int rowIndex) {
        getAllRows().get(rowIndex)
                .findElement(editBtnInRow)
                .click();
    }

    public boolean isLockButtonVisible(int rowIndex) {
        return getAllRows().get(rowIndex)
                .findElements(lockBtnInRow).size() > 0;
    }

    public void clickLockUnlockButton(int rowIndex) {
        getAllRows().get(rowIndex)
                .findElement(lockBtnInRow)
                .click();
    }
    public Alert waitForAlert() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    public String getAlertText() {
        return waitForAlert().getText();
    }

    public void confirmAlert() {
        waitForAlert().accept();
    }

    public void cancelAlert() {
        waitForAlert().dismiss();
    }

    public String getUserStatus(String username) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement statusEl = wait.until(driver -> 
            driver.findElement(By.xpath(
                "//tr[td[text()='" + username + "']]//td/span[contains(@class,'badge status')]"
            ))
        );
        return statusEl.getText().trim();
    }


    public boolean isUserActive(String username) {
        return "Hoạt động".equals(getUserStatus(username));
    }

    public boolean isUserBanned(String username) {
        return "Bị khóa".equals(getUserStatus(username));
    }

    public void clickLockUnlockButton(String username) {
        WebElement btn = driver.findElement(By.xpath(
            "//tr[td[text()='" + username + "']]//button[contains(@class,'lock')]"
        ));
        btn.click();
    }


    public void searchUser(String keyword) {
        WebElement input = driver.findElement(searchInput);
        input.clear();
        input.sendKeys(keyword);
        input.sendKeys(Keys.ENTER);

        waitForTableReload();
    }


    public void selectRoleByValue(String value) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        JavascriptExecutor js = (JavascriptExecutor) driver;

        By filtersContainer = By.xpath("//div[@class='filters-container']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(filtersContainer));

        By roleSelect = By.xpath("//select[@class='filter-select ng-valid ng-touched ng-dirty']");
        WebElement roleDropdown = wait.until(ExpectedConditions.elementToBeClickable(roleSelect));

        js.executeScript("arguments[0].scrollIntoView(true);", roleDropdown);

        try {
            roleDropdown.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", roleDropdown);
        }

        By roleOption = By.xpath("//select[@class='filter-select ng-valid ng-touched ng-dirty']/option[@value='1']");
        WebElement roleOptElem = wait.until(ExpectedConditions.visibilityOfElementLocated(roleOption));
        try {
            roleOptElem.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", roleOptElem);
        }

        js.executeScript("arguments[0].dispatchEvent(new Event('change'));", roleDropdown);

        waitForTableReload();
    }


    public void selectStatusByValue(String value) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        By filtersContainer = By.xpath("//div[@class='filters-container']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(filtersContainer));
        
        By statusSelect = By.xpath("//select[@class='filter-select ng-pristine ng-valid ng-touched']");
        WebElement statusDropdown = wait.until(ExpectedConditions.elementToBeClickable(statusSelect));

        js.executeScript("arguments[0].scrollIntoView(true);", statusDropdown);

        try {
            statusDropdown.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", statusDropdown);
        }
        
        By statusOption = By.xpath("//select[@class='filter-select ng-pristine ng-valid ng-touched']/option[@value='ACTIVE']");
        WebElement statusOptElem = wait.until(ExpectedConditions.visibilityOfElementLocated(statusOption));
        try {
            statusOptElem.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", statusOptElem);
        }

        js.executeScript("arguments[0].dispatchEvent(new Event('change'));", statusDropdown);
    }
   
    
    public void waitForTableReload() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.cssSelector(".loading-spinner")
        ));
    }
    
    public void scrollToPagination() {
        WebElement label = driver.findElement(currentPageLabel);
        ((JavascriptExecutor) driver)
            .executeScript("arguments[0].scrollIntoView({block:'center'});", label);
    }

    public void clickNextPage() {
        WebElement btn = driver.findElement(nextPageBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void clickPrevPage() {
        WebElement btn = driver.findElement(prevPageBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public int getCurrentPageNumber() {
        String text = driver.findElement(currentPageLabel).getText();
        return Integer.parseInt(text.replaceAll("\\D+", " ").trim().split(" ")[0]);
    }

    
    public void waitForPageChange(int oldPage) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(driver -> getCurrentPageNumber() != oldPage);
    }


    public void clickAddUserButton() {
        driver.findElement(addUserBtn).click();
    }

    public boolean isConfirmModalDisplayed() {
        return driver.findElements(confirmModal).size() > 0;
    }

    public void confirmAction() {
        driver.findElement(confirmBtn).click();
    }

    public void cancelAction() {
        driver.findElement(cancelBtn).click();
    }


    public String getUserStatus(int rowIndex) {
        return getAllRows().get(rowIndex)
                .findElement(statusBadge)
                .getText();
    }

    public boolean isUserActive(int rowIndex) {
        return getUserStatus(rowIndex).equalsIgnoreCase("Hoạt động");
    }

    public boolean isUserBanned(int rowIndex) {
        return getUserStatus(rowIndex).equalsIgnoreCase("Bị khóa");
    }
    
    public boolean waitUntilUserInList(String username) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(d ->
            d.findElements(By.cssSelector("tbody tr"))
             .stream()
             .anyMatch(row -> row.getText().contains(username))
        );
    }

}
