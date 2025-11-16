package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import pages.HomePage;
import pages.LoginPage;

import java.lang.reflect.Method;
import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;

    @Parameters("startUrl")
    @BeforeMethod
    public void setUp(@Optional("http://localhost:4200/") String startUrl, Method method) {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        if (!method.isAnnotationPresent(NeedLogin.class)) {
            driver.get(startUrl);
        } else {
            driver.get("http://localhost:4200/login");
            LoginPage login = new LoginPage(driver);
            HomePage home = login.login("hoaine", "hoaine123");
            home.goToTenantPage();
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
