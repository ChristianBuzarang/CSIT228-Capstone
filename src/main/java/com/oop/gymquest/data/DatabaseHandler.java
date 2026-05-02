package com.oop.gymquest.data;

import com.oop.gymquest.data.userdata.Admin;
import com.oop.gymquest.data.userdata.Member;
import com.oop.gymquest.data.userdata.Trainer;
import com.oop.gymquest.data.userdata.User;

import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/dbgymquest";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 Statement stmt = conn.createStatement()) {

                // 1. Base Users Table
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "userid INT PRIMARY KEY AUTO_INCREMENT, " +
                        "email VARCHAR(255) UNIQUE, password VARCHAR(255), " +
                        "firstname VARCHAR(255), lastname VARCHAR(255), type VARCHAR(50))");

                // 2. Separate Role Tables (Linked via Foreign Key)
                stmt.execute("CREATE TABLE IF NOT EXISTS admins (userid INT PRIMARY KEY, " +
                        "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                stmt.execute("CREATE TABLE IF NOT EXISTS members (userid INT PRIMARY KEY, " +
                        "membership_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                stmt.execute("CREATE TABLE IF NOT EXISTS trainers (userid INT PRIMARY KEY, " +
                        "specialization VARCHAR(255) DEFAULT 'General Training', " +
                        "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                // Seed Admin
                registerUser("admin", "1234", "System", "Admin", "admin");

                System.out.println("Database tables (users, admins, members, trainers) initialized.");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static boolean registerUser(String email, String password, String fname, String lname, String type) {
        String insertUserSql = "INSERT IGNORE INTO users (email, password, firstname, lastname, type) VALUES (?, ?, ?, ?, ?)";
        String insertRoleSql = switch (type.toLowerCase()) {
            case "admin" -> "INSERT IGNORE INTO admins (userid) VALUES (?)";
            case "trainer" -> "INSERT IGNORE INTO trainers (userid) VALUES (?)";
            default -> "INSERT IGNORE INTO members (userid) VALUES (?)";
        };

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, email);
                psUser.setString(2, password);
                psUser.setString(3, fname);
                psUser.setString(4, lname);
                psUser.setString(5, type.toLowerCase());

                int affectedRows = psUser.executeUpdate();
                if (affectedRows == 0) return false;

                // Get the generated userid
                ResultSet rs = psUser.getGeneratedKeys();
                if (rs.next()) {
                    int newUserId = rs.getInt(1);
                    // Insert into specific role table
                    try (PreparedStatement psRole = conn.prepareStatement(insertRoleSql)) {
                        psRole.setInt(1, newUserId);
                        psRole.executeUpdate();
                    }
                }
                conn.commit(); // Save changes to both tables
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Undo if anything fails
                return false;
            }
        } catch (SQLException e) { return false; }
    }

    public static User authenticate(String email, String pass) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("userid");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String fname = rs.getString("firstname");
        String lname = rs.getString("lastname");
        String type = rs.getString("type").toLowerCase();

        return switch (type) {
            case "admin" -> new Admin(id, email, password, fname, lname);
            case "trainer" -> new Trainer(id, email, password, fname, lname);
            default -> new Member(id, email, password, fname, lname);
        };
    }
}