package utils;

import java.util.UUID;

public class TestDataUtil {

    public static String randomName() {
        return "Hoai" + UUID.randomUUID().toString().substring(0, 5);
    }
    
    public static String randomEmail() {
        return randomName().toLowerCase() + "@gmail.com";
    }

    public static String randomPhone() {
        return "03" + ((int)(Math.random() * 90000000) + 10000000);
    }

    public static String randomCCCD() {
        long cccdNum = (long)(Math.random() * 90000000000L) + 10000000000L;
        return "0" + cccdNum;
    }
}
