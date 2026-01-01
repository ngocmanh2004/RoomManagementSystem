package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import pages.HeaderComponent;
import pages.LoginPage;

import java.lang.reflect.Method;
import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp(Method method) {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("http://localhost:4200/login");

        LoginPage loginPage = new LoginPage(driver);

        if (method.isAnnotationPresent(LoginAs.class)) {
            LoginAs loginAs = method.getAnnotation(LoginAs.class);

            switch (loginAs.value()) {
            case ADMIN:
                loginPage.login("admin2", "hoaine12345");
                HeaderComponent header = new HeaderComponent(driver);
                header.openUserManagementPage();
                break;
                
            case LANDLORD:
                loginPage.login("chutro2", "hoaine12345");
                break;
            case TENANT:
                loginPage.login("khachthue1", "hoaine12345");
                break;
        }
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
