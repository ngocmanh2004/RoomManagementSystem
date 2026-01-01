package tests.rooms;

import base.BaseTest;
import base.LoginAs;
import base.UserRole;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BookingModalPage;
import pages.RoomDetailPage;

public class RoomBookingTests extends BaseTest {

    String roomAvailableUrl = "http://localhost:4200/rooms/1";

    @Test
    @LoginAs(UserRole.TENANT)
    public void testAutoFillFromProfile() {
        driver.get(roomAvailableUrl);
        

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();

        Assert.assertFalse(modal.getFullName().isEmpty(),
                "Họ tên không được auto-fill");
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testAutoFillWithoutPhone() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();

        Assert.assertTrue(
                modal.getPhone() == null || modal.getPhone().isEmpty(),
                "SĐT phải rỗng khi profile không có"
        );
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testMissingFullName() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.clearFullName();
        modal.submit();

        Assert.assertTrue(modal.isErrorDisplayed(),
                "Không báo lỗi khi thiếu họ tên");
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testCccdTooShort() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.setCccd("1234");
        modal.submit();

        Assert.assertTrue(modal.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testCccdTooLong() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.setCccd("1234567890123456");
        modal.submit();

        Assert.assertTrue(modal.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testCccdInvalidCharacter() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.setCccd("1234@@");
        modal.submit();

        Assert.assertTrue(modal.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testInvalidPhone() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.setPhone("abcd123");
        modal.submit();

        Assert.assertTrue(modal.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testEndDateBeforeStartDate() {
        driver.get(roomAvailableUrl);
        
        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.setStartDate("10/12/2025");
        modal.setEndDate("09/12/2025");
        modal.submit();

        Assert.assertTrue(modal.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testStartDateInPast() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.setStartDate("20/11/2024");
        modal.submit();

        Assert.assertTrue(modal.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testStartDateTodayValid() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.submit();

        Assert.assertTrue(modal.isSuccessDisplayed(),
                "Không hiển thị thông báo thành công");
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testInvalidDate() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.setStartDate("31/02/2025");
        modal.submit();

        Assert.assertTrue(modal.isErrorDisplayed());
    }

    @Test
    @LoginAs(UserRole.TENANT)
    public void testBookingSuccessMessage() {
        driver.get(roomAvailableUrl);

        BookingModalPage modal = new RoomDetailPage(driver).openBookingModal();
        modal.submit();

        Assert.assertTrue(modal.isSuccessDisplayed());
    }
}
