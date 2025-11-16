package tests.home;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.RoomDetailPage;

public class HomeNavigationTests extends BaseTest {

    @Test
    public void testClickRoomOpensDetail() {
        HomePage home = new HomePage(driver);

        home.clickFirstRoom();

        RoomDetailPage detail = new RoomDetailPage(driver);

        detail.printRoomDetail();

        Assert.assertTrue(driver.getCurrentUrl().contains("/rooms/1"),
                "Không chuyển sang trang chi tiết phòng!");

        Assert.assertFalse(detail.getRoomName().isEmpty(), "Tên phòng bị rỗng!");
        Assert.assertFalse(detail.getRoomPrice().isEmpty(), "Giá phòng bị rỗng!");
        Assert.assertFalse(detail.getRoomArea().isEmpty(), "Diện tích bị rỗng!");
    }
}
