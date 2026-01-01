package tests.home;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import java.util.List;

public class HomeSortTests extends BaseTest {

    @Test
    public void testSortPriceLowToHigh() {
        HomePage home = new HomePage(driver);

        home.sortBy("Giá tăng dần");
        System.out.println("Đã chọn sort: Giá tăng dần");

        List<Integer> prices = home.getRoomPrices();
        home.printRoomList();

        for (int i = 0; i < prices.size() - 1; i++) {
            Assert.assertTrue(prices.get(i) <= prices.get(i + 1),
                    "Danh sách giá không sắp xếp tăng dần!");
        }
        System.out.println("Danh sách giá đã được sắp xếp tăng dần đúng.");
    }
}
