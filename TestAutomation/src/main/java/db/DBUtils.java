package db;

import java.sql.*;

public class DBUtils {

    public static String getSingleValue(String query) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getString(1);
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Query error: " + query, e);
        }
    }

    public static int executeUpdate(String query) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            return stmt.executeUpdate(query);

        } catch (Exception e) {
            throw new RuntimeException("Update error: " + query, e);
        }
    }
}
