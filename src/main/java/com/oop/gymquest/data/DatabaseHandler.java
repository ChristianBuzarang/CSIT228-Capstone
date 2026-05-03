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
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS users (userid INT PRIMARY KEY AUTO_INCREMENT, email VARCHAR(255) UNIQUE, password VARCHAR(255), firstname VARCHAR(255), lastname VARCHAR(255), type VARCHAR(50))");
                stmt.execute("CREATE TABLE IF NOT EXISTS admins (userid INT PRIMARY KEY, FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");
                stmt.execute("CREATE TABLE IF NOT EXISTS members (userid INT PRIMARY KEY, FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");
                stmt.execute("CREATE TABLE IF NOT EXISTS trainers (userid INT PRIMARY KEY, FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");
                stmt.execute("CREATE TABLE IF NOT EXISTS trainer_slots (slot_id INT PRIMARY KEY AUTO_INCREMENT, trainer_id INT, member_id INT DEFAULT NULL, activity VARCHAR(255), slot_date DATE, slot_time VARCHAR(50), duration VARCHAR(50), status VARCHAR(50) DEFAULT 'Available', FOREIGN KEY (trainer_id) REFERENCES users(userid) ON DELETE CASCADE, FOREIGN KEY (member_id) REFERENCES users(userid) ON DELETE SET NULL)");
                stmt.execute("CREATE TABLE IF NOT EXISTS posts (postid INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255), post_type VARCHAR(50), content TEXT, milestone VARCHAR(255), reactions INT DEFAULT 0)");
                stmt.execute("CREATE TABLE IF NOT EXISTS workouts (id INT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(255), difficulty VARCHAR(50), duration VARCHAR(50), category VARCHAR(50), description VARCHAR(255), is_custom TINYINT(1), locked TINYINT(1))");
                stmt.execute("CREATE TABLE IF NOT EXISTS exercises (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), sets INTEGER, reps VARCHAR(255), emoji VARCHAR(255), category VARCHAR(50))");
                stmt.execute("CREATE TABLE IF NOT EXISTS users_archive (userid INT, email VARCHAR(255) UNIQUE, password VARCHAR(255), firstname VARCHAR(255), lastname VARCHAR(255), type VARCHAR(50), archive_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                registerUser("admin", "1234", "System", "Admin", "admin");
                System.out.println("Database Ready.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean saveBooking(int memberId, String coach, String date, String time) {
        String sql = "UPDATE trainer_slots s JOIN users u ON s.trainer_id = u.userid SET s.status = 'Booked', s.member_id = ? WHERE CONCAT(u.firstname,' ',u.lastname) = ? AND s.slot_date = ? AND s.slot_time = ? AND s.status = 'Available'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setString(2, coach);
            ps.setString(3, date);
            ps.setString(4, time);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean cancelBooking(int slotId) {
        String sql = "UPDATE trainer_slots SET status = 'Available', member_id = NULL WHERE slot_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static ResultSet getMemberBookings(int memberId) {
        String sql = "SELECT s.slot_id, s.activity, s.slot_date, s.slot_time, CONCAT(u.firstname, ' ', u.lastname) as coach_name FROM trainer_slots s JOIN users u ON s.trainer_id = u.userid WHERE s.member_id = ? AND s.status = 'Booked' ORDER BY s.slot_date ASC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, memberId);
            return ps.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }

    public static boolean registerUser(String email, String password, String fname, String lname, String type) {
        String insertUser = "INSERT IGNORE INTO users (email, password, firstname, lastname, type) VALUES (?,?,?,?,?)";
        String insertRole = type.equalsIgnoreCase("trainer") ? "INSERT INTO trainers (userid) VALUES (?)" : "INSERT INTO members (userid) VALUES (?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psUser = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, email);
                psUser.setString(2, password);
                psUser.setString(3, fname);
                psUser.setString(4, lname);
                psUser.setString(5, type.toLowerCase());
                if (psUser.executeUpdate() == 0) return false;
                ResultSet rs = psUser.getGeneratedKeys();
                if (rs.next()) {
                    PreparedStatement psRole = conn.prepareStatement(insertRole);
                    psRole.setInt(1, rs.getInt(1));
                    psRole.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) { conn.rollback(); return false; }
        } catch (SQLException e) { return false; }
    }

    public static User authenticate(String email, String pass) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("userid");
                String fn = rs.getString("firstname");
                String ln = rs.getString("lastname");
                String em = rs.getString("email");
                String pw = rs.getString("password");
                String type = rs.getString("type").toLowerCase();
                return switch (type) {
                    case "admin" -> new Admin(id, em, pw, fn, ln);
                    case "trainer" -> new Trainer(id, em, pw, fn, ln);
                    default -> new Member(id, em, pw, fn, ln);
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static ResultSet getTodaySessions(int trainerId) {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM trainer_slots WHERE trainer_id = ? AND slot_date = CURRENT_DATE ORDER BY slot_time ASC");
            ps.setInt(1, trainerId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static int getUniqueClientCount(int trainerId) {
        String sql = "SELECT COUNT(DISTINCT member_id) FROM trainer_slots WHERE trainer_id = ? AND status = 'Booked'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    public static boolean isSlotBooked(String coach, String date, String time) {
        String sql = "SELECT COUNT(*) FROM trainer_slots s JOIN users u ON s.trainer_id = u.userid WHERE CONCAT(u.firstname,' ',u.lastname) = ? AND s.slot_date = ? AND s.slot_time = ? AND s.status = 'Booked'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coach);
            ps.setString(2, date);
            ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean addTrainerSlot(int tid, String act, String dat, String tim, String dur) {
        String sql = "INSERT INTO trainer_slots (trainer_id, activity, slot_date, slot_time, duration) VALUES (?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tid);
            ps.setString(2, act);
            ps.setString(3, dat);
            ps.setString(4, tim);
            ps.setString(5, dur);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean deleteSlot(int sid) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM trainer_slots WHERE slot_id = ?")) {
            ps.setInt(1, sid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static ResultSet getTrainerSchedule(int tid) {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM trainer_slots WHERE trainer_id = ? ORDER BY slot_date ASC");
            ps.setInt(1, tid);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }
}