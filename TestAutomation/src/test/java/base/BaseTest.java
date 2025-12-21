package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import pages.HomePage;
import pages.LoginPage;

import java.lang.reflect.Method;
import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @Parameters("startUrl")
    @BeforeMethod
    public void setUp(
            @Optional("http://localhost:4200/") String startUrl,
            Method method
    ) {
        // Setup driver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Nếu test KHÔNG cần login
        if (!method.isAnnotationPresent(NeedLogin.class)) {
            driver.get(startUrl);
            return;
        }

        // Nếu test CẦN login
        driver.get("http://localhost:4200/login");
        LoginPage loginPage = new LoginPage(driver);

        // Login mặc định bằng tài khoản admin / user tùy bạn chỉnh
        HomePage home = loginPage.login("hoaine", "hoaine123");

        // Nếu test chạy trong khu vực admin → điều hướng đến trang Tenant
        home.goToTenantPage();
    }


    // ==========================================================
    //                    HÀM TIỆN ÍCH (Utility Methods)
    // ==========================================================

    protected void loginAs(String username, String password) {
        driver.get("http://localhost:4200/login");
        LoginPage login = new LoginPage(driver);
        login.login(username, password);
    }

    protected void loginAsAdmin() {
        loginAs("admin", "admin123");  // Thay bằng tài khoản admin thực tế
    }

    protected boolean isLoginSuccessful(String username, String password) {
        try {
            loginAs(username, password);
            return driver.getCurrentUrl().contains("/dashboard")
                    || driver.getCurrentUrl().contains("/home")
                    || driver.getCurrentUrl().contains("/rooms");
        } catch (Exception e) {
            return false;
        }
    }

    protected void navigateToRoom(int roomId) {
        driver.get("http://localhost:4200/room/" + roomId);
    }

    protected void logout() {
        driver.get("http://localhost:4200/logout");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
