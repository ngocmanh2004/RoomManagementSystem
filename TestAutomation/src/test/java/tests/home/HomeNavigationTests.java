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

        // Click phòng đầu tiên
        RoomDetailPage detail = home.clickFirstRoom();

        // Verify chuyển trang đúng
        Assert.assertTrue(detail.isAtRoomDetailPage(),
                "Không chuyển sang trang chi tiết phòng!");

        // Verify thông tin phòng không rỗng
        Assert.assertFalse(detail.getRoomName().isEmpty(), "Tên phòng bị rỗng!");
        Assert.assertFalse(detail.getRoomPrice().isEmpty(), "Giá phòng bị rỗng!");
        Assert.assertFalse(detail.getRoomArea().isEmpty(), "Diện tích bị rỗng!");
    }
}
