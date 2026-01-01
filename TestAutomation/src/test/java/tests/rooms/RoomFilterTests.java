package tests.rooms;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.RoomsPage;

public class RoomFilterTests extends BaseTest {

    @Test
    public void testFilterProvinceDistrictPriceAcreage() {

        HomePage home = new HomePage(driver);
        RoomsPage roomsPage = home.goToRoomsPage();

        roomsPage.filterByProvince("Bình Định");
        roomsPage.filterByDistrict("An Nhơn");
        roomsPage.filterByPrice("2 - 4 triệu");
        roomsPage.filterByAcreage("Dưới 15 m²");

        roomsPage.applyFilter();

        Assert.assertTrue(
            driver.getPageSource().contains("phòng"), "Không lọc được danh sách phòng!"
        );
    }
}
