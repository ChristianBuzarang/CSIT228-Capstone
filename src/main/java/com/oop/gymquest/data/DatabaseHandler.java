package com.oop.gymquest.data;

import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/dbgymquest";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void init() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = conn.createStatement()) {
            // Create Users Table
            statement.execute("CREATE TABLE IF NOT EXISTS users (userid INT PRIMARY KEY AUTO_INCREMENT, " +
                    "email VARCHAR(255) UNIQUE, password VARCHAR(255), " +
                    "firstname VARCHAR(255), lastname VARCHAR(255), type VARCHAR(50))");

            // Seed a default admin if empty
            statement.execute("INSERT IGNORE INTO users (email, password, firstname, lastname, type) " +
                    "VALUES ('admin', '1234', 'Chan', 'Admin', 'admin')");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static com.oop.gymquest.data.User authenticate(String user, String pass) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, pass);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("userid"), rs.getString("email"),
                        rs.getString("password"), rs.getString("firstname"),
                        rs.getString("lastname"), rs.getString("type"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static boolean registerUser(String email, String password, String firstName, String lastName, String type) {
        String sql = "INSERT INTO users (email, password, firstname, lastname, type) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, type);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            // This will fail if the email is already in the database due to the UNIQUE constraint
            System.err.println("Registration Error: " + e.getMessage());
            return false;
        }
    }
}