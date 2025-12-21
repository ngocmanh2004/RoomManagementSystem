package pages.admin;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class UserManagementPage {

    private WebDriver driver;

    // -------------------------
    // Constructor
    // -------------------------
    public UserManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    // -------------------------
    // Locators
    // -------------------------
    private By tableRows = By.cssSelector("table tbody tr");
    private By noDataMessage = By.cssSelector(".no-data, .empty-text");

    private By searchInput = By.cssSelector("input[placeholder='Tìm kiếm']");
    private By searchButton = By.cssSelector("button.btn-search");

    private By roleFilter = By.cssSelector("select#role-filter");
    private By statusFilter = By.cssSelector("select#status-filter");
    private By applyFilterBtn = By.cssSelector("button.btn-apply");

    private By nextPageBtn = By.cssSelector("button.next-page");
    private By prevPageBtn = By.cssSelector("button.prev-page");
    private By currentPage = By.cssSelector(".pagination .current");

    private By userDetailButton = By.cssSelector("button.btn-view, td.name");
    private By btnAddUser = By.cssSelector("button.btn-add-user");

    private By deleteButtonByRow = By.cssSelector("button.btn-delete");
    private By confirmDialog = By.cssSelector(".modal-confirm");
    private By confirmYes = By.cssSelector("button.btn-yes");
    private By confirmNo = By.cssSelector("button.btn-no");
    private By toastMessage = By.cssSelector(".toast-message");

    private By lockButtonByRow = By.cssSelector("button.btn-lock");
    private By unlockButtonByRow = By.cssSelector("button.btn-unlock");
    
    
    // -------------------------
    // Helpers
    // -------------------------
    public int getUserCount() {
        try {
            return driver.findElements(tableRows).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isNoDataShown() {
        return driver.findElements(noDataMessage).size() > 0;
    }

    public WebElement getRowByUsername(String username) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            String name = row.findElements(By.tagName("td")).get(1).getText().trim();
            if (name.equals(username)) {
                return row;
            }
        }
        return null;
    }

    // -------------------------
    // Pagination
    // -------------------------
    public void nextPage() {
        driver.findElement(nextPageBtn).click();
        sleep(500);
    }

    public void prevPage() {
        driver.findElement(prevPageBtn).click();
        sleep(500);
    }

    public String getCurrentPageText() {
        return driver.findElement(currentPage).getText().trim();
    }

    // -------------------------
    // Add User
    // -------------------------
    public AddUserPage clickAddUser() {
        driver.findElement(btnAddUser).click();
        return new AddUserPage(driver);
    }

    // -------------------------
    // Delete User
    // -------------------------
    public boolean isDeleteButtonVisible(int rowIndex) {
        return driver.findElements(deleteButtonByRow).size() > rowIndex;
    }

    public void clickDeleteButton(int rowIndex) {
        driver.findElements(deleteButtonByRow).get(rowIndex).click();
        sleep(500);
    }

    public void clickDeleteButtonByUsername(String username) {
        WebElement row = getRowByUsername(username);
        if (row != null) {
            row.findElement(deleteButtonByRow).click();
            sleep(500);
        } else {
            throw new RuntimeException("User " + username + " not found");
        }
    }

    public void confirmDelete() {
        driver.findElement(confirmYes).click();
        sleep(700);
    }

    public void cancelDelete() {
        driver.findElement(confirmNo).click();
        sleep(500);
    }

    public boolean isDeleteConfirmationShown() {
        return driver.findElements(confirmDialog).size() > 0;
    }

    // -------------------------
    // Alerts / Toast
    // -------------------------
    public boolean isAlertShown(String message) {
        try {
            String text = driver.findElement(toastMessage).getText().trim();
            return text.contains(message);
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // -------------------------
    // Search
    // -------------------------
    public void searchUser(String keyword) {
        driver.findElement(searchInput).clear();
        driver.findElement(searchInput).sendKeys(keyword);
        driver.findElement(searchButton).click();
        sleep(700);
    }

    // -------------------------
    // Filter Role / Status
    // -------------------------
    public void selectRole(String roleValue) {
        Select select = new Select(driver.findElement(roleFilter));
        select.selectByValue(roleValue);
        driver.findElement(applyFilterBtn).click();
        sleep(800);
    }

    public void selectStatus(String status) {
        Select select = new Select(driver.findElement(statusFilter));
        select.selectByValue(status);
        driver.findElement(applyFilterBtn).click();
        sleep(800);
    }

    // -------------------------
    // Read Data by Row
    // -------------------------
    public String getUserFullName(int rowIndex) {
        return getCellText(rowIndex, 1);
    }

    public String getUserEmail(int rowIndex) {
        return getCellText(rowIndex, 2);
    }

    public String getUserRole(int rowIndex) {
        return getCellText(rowIndex, 3);
    }

    public String getUserStatus(int rowIndex) {
        return getCellText(rowIndex, 4);
    }

    private String getCellText(int row, int col) {
        WebElement r = driver.findElements(tableRows).get(row);
        return r.findElements(By.tagName("td")).get(col).getText().trim();
    }

    // -------------------------
    // Edit User (Mở modal)
    // -------------------------
    public EditUserPage openEditUser(int rowIndex) {
        WebElement row = driver.findElements(tableRows).get(rowIndex);
        row.findElement(userDetailButton).click();
        sleep(700);
        return new EditUserPage(driver);
    }

    public EditUserPage openEditUserByUsername(String username) {
        WebElement row = getRowByUsername(username);
        if (row != null) {
            row.findElement(userDetailButton).click();
            sleep(700);
            return new EditUserPage(driver);
        } else {
            throw new RuntimeException("User " + username + " not found");
        }
    }

	 // -------------------------
	 // Lock/Unlock User
	 // -------------------------
    
    public void clickLockByUsername(String username) {
        WebElement row = getRowByUsername(username);
        if (row != null) {
            row.findElement(lockButtonByRow).click();
            sleep(500);
        } else throw new RuntimeException("User " + username + " not found");
    }

    public void clickUnlockByUsername(String username) {
        WebElement row = getRowByUsername(username);
        if (row != null) {
            row.findElement(unlockButtonByRow).click();
            sleep(500);
        } else throw new RuntimeException("User " + username + " not found");
    }
	    
	 // -------------------------
	 // Get User Status by Username
	 // -------------------------
	 public String getUserStatusByUsername(String username) {
	     WebElement row = getRowByUsername(username);
	     if (row != null) {
	         // cột 4 là cột Status (theo table hiện tại)
	         return row.findElements(By.tagName("td")).get(4).getText().trim();
	     } else {
	         throw new RuntimeException("User " + username + " not found");
	     }
	 }
    
	// -------------------------
	// Check Lock/Unlock button visibility by username
	// -------------------------
	private By lockButton = By.cssSelector("button.btn-lock"); // giả sử class btn-lock cho nút khóa

	public boolean isLockButtonVisible(String username) {
	    WebElement row = getRowByUsername(username);
	    if (row != null) {
	        try {
	            WebElement btn = row.findElement(lockButton);
	            return btn.isDisplayed();
	        } catch (NoSuchElementException e) {
	            return false;
	        }
	    } else {
	        throw new RuntimeException("User " + username + " not found");
	    }
	}

    // -------------------------
    // Utility
    // -------------------------
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {}
    }
}
