package com.oop.gymquest.data;

import com.oop.gymquest.data.userdata.*;
import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/dbgymquest";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                // Core User Tables
                stmt.execute("CREATE TABLE IF NOT EXISTS users (userid INT PRIMARY KEY AUTO_INCREMENT, email VARCHAR(255) UNIQUE, password VARCHAR(255), firstname VARCHAR(255), lastname VARCHAR(255), type VARCHAR(50))");
                stmt.execute("CREATE TABLE IF NOT EXISTS members (userid INT PRIMARY KEY, FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                // TRAINER SLOTS (The actual schedule items)
                stmt.execute("CREATE TABLE IF NOT EXISTS trainer_slots (" +
                        "slot_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "trainer_id INT, " +
                        "activity VARCHAR(255), " +
                        "slot_date DATE, " +
                        "slot_time VARCHAR(50), " +
                        "duration VARCHAR(50), " +
                        "status VARCHAR(50) DEFAULT 'Available', " +
                        "booked_by_name VARCHAR(255) DEFAULT NULL, " +
                        "FOREIGN KEY (trainer_id) REFERENCES users(userid) ON DELETE CASCADE)");

                registerUser("admin", "1234", "System", "Admin", "admin");
                System.out.println("Database Initialized.");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * MISSING METHOD 1: Checks if a slot is already booked.
     * Uses a JOIN to match the Coach's Full Name (e.g., "Coach Alex") to their ID.
     */
    public static boolean isSlotBooked(String coachFullName, String date, String time) {
        String sql = "SELECT COUNT(*) FROM trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "WHERE CONCAT(u.firstname, ' ', u.lastname) = ? " +
                "AND s.slot_date = ? AND s.slot_time = ? AND s.status = 'Booked'";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, coachFullName);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * MISSING METHOD 2: Saves a booking by updating an available slot.
     * Sets status to 'Booked' and records the member's name.
     */
    public static boolean saveBooking(int memberId, String coachFullName, String date, String time) {
        // Query to update the slot status and set the client's name
        String sql = "UPDATE trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "SET s.status = 'Booked', " +
                "    s.booked_by_name = (SELECT CONCAT(firstname, ' ', lastname) FROM users WHERE userid = ?) " +
                "WHERE CONCAT(u.firstname, ' ', u.lastname) = ? " +
                "AND s.slot_date = ? AND s.slot_time = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setString(2, coachFullName);
            pstmt.setString(3, date);
            pstmt.setString(4, time);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addTrainerSlot(int trainerId, String activity, String date, String time, String duration) {
        String sql = "INSERT INTO trainer_slots (trainer_id, activity, slot_date, slot_time, duration) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainerId);
            pstmt.setString(2, activity);
            pstmt.setString(3, date);
            pstmt.setString(4, time);
            pstmt.setString(5, duration);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static ResultSet getTrainerSchedule(int trainerId) {
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM trainer_slots WHERE trainer_id = ? ORDER BY slot_date ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, trainerId);
            return pstmt.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static User authenticate(String email, String pass) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, pass);
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

    public static boolean registerUser(String email, String password, String fname, String lname, String type) {
        String sql = "INSERT IGNORE INTO users (email, password, firstname, lastname, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, password); ps.setString(3, fname); ps.setString(4, lname); ps.setString(5, type.toLowerCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}