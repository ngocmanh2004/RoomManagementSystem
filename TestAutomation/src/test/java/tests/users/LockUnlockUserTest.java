package tests.users;

import base.BaseTest;
import base.LoginAs;
import base.NeedLogin;
import base.UserRole;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.UserManagementPage;

public class LockUnlockUserTest extends BaseTest {

    UserManagementPage page;

    @BeforeMethod
    public void setup() {
        page = new UserManagementPage(driver);
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void displayLockUnlockButton() {
        Assert.assertTrue(
                page.isLockButtonVisible(0),
                "Không hiển thị nút Khóa / Mở khóa"
        );
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void showConfirmWhenLock() {
        if (page.isUserBanned(3)) {
            page.clickLockUnlockButton(3);
            page.confirmAlert();
        }

        page.clickLockUnlockButton(3);

        Alert alert = page.waitForAlert(); 
        String alertText = alert.getText();
        System.out.println("Alert text: " + alertText);

        Assert.assertTrue(
            alertText.toLowerCase().contains("khóa"),
            "Không hiển thị confirm khi khóa user"
        );

        alert.dismiss(); 
    }



    @Test
    @LoginAs(UserRole.ADMIN)
    public void cancelLockUser() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        if (page.isUserBanned(3)) {
            page.clickLockUnlockButton(3);

            Alert unlockAlert = wait.until(ExpectedConditions.alertIsPresent());
            unlockAlert.accept();

            wait.until(d -> page.isUserActive(3));
        }

        String beforeStatus = page.getUserStatus(3);

        page.clickLockUnlockButton(3);

        Alert lockAlert = wait.until(ExpectedConditions.alertIsPresent());
        lockAlert.dismiss();

        Assert.assertEquals(
            page.getUserStatus(3),
            beforeStatus,
            "Status thay đổi dù đã hủy thao tác khóa"
        );
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void showConfirmWhenUnlock() {
        if (page.isUserActive(3)) {
            page.clickLockUnlockButton(3);
            page.confirmAlert(); 
        }

        page.clickLockUnlockButton(3);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        String alertText = alert.getText();
        System.out.println("Alert text: " + alertText);

        Assert.assertTrue(
            alertText.toLowerCase().contains("mở khóa"),
            "Không hiển thị confirm khi mở khóa user"
        );
        alert.dismiss();
    }


    @Test
    @LoginAs(UserRole.ADMIN)
    public void cancelUnlockUser() {
        if (page.isUserActive(3)) {
            page.clickLockUnlockButton(3);
            page.confirmAlert(); 
        }

        String beforeStatus = page.getUserStatus(3);

        page.clickLockUnlockButton(3);
        page.cancelAlert(); 

        Assert.assertEquals(
            page.getUserStatus(3),
            beforeStatus,
            "Status thay đổi dù đã hủy mở khóa"
        );
    }


    @Test
    @LoginAs(UserRole.ADMIN)
    public void lockUserSuccessfully() {
        String username = "chutrodn"; 

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        if (page.isUserBanned(username)) {
            page.clickLockUnlockButton(username);
            wait.until(ExpectedConditions.alertIsPresent()).accept();
            waitUntilStatus(username, "Hoạt động");
        }

        page.clickLockUnlockButton(username);

        wait.until(ExpectedConditions.alertIsPresent()).accept();
        try {
            Alert successAlert = wait.until(ExpectedConditions.alertIsPresent());
            successAlert.accept();
        } catch (TimeoutException e) {
        }

        waitUntilStatus(username, "Bị khóa");

        // 6️⃣ Verify
        Assert.assertEquals(page.getUserStatus(username), "Bị khóa");
    }

    @Test
    @LoginAs(UserRole.ADMIN)
    public void unlockUserSuccessfully() {
        String username = "chutrodn";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        if (page.isUserActive(username)) {
            page.clickLockUnlockButton(username);
            wait.until(ExpectedConditions.alertIsPresent()).accept(); 
            waitUntilStatus(username, "Bị khóa");
        }

        page.clickLockUnlockButton(username);

        wait.until(ExpectedConditions.alertIsPresent()).accept();

        try {
            Alert successAlert = wait.until(ExpectedConditions.alertIsPresent());
            successAlert.accept();
        } catch (TimeoutException e) {}

        waitUntilStatus(username, "Hoạt động");

        Assert.assertEquals(page.getUserStatus(username), "Hoạt động");
    }

    private void waitUntilStatus(String username, String expectedStatus) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> expectedStatus.equals(page.getUserStatus(username)));
    }
}