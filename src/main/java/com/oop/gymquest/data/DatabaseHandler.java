package com.oop.gymquest.data;

import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/dbgymquest";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void init() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            // Create Users Table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (userid INT PRIMARY KEY AUTO_INCREMENT, " +
                    "email VARCHAR(255) UNIQUE, password VARCHAR(255), " +
                    "firstname VARCHAR(255), lastname VARCHAR(255), type VARCHAR(50))");

            // Seed a default admin if empty
            stmt.execute("INSERT IGNORE INTO users (email, password, firstname, lastname, type) " +
                    "VALUES ('admin', '1234', 'Chan', 'Admin', 'admin')");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static com.oop.gymquest.data.User authenticate(String user, String pass) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("userid"), rs.getString("email"),
                        rs.getString("password"), rs.getString("firstname"),
                        rs.getString("lastname"), rs.getString("type"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}