package com.oop.gymquest.data;

import com.oop.gymquest.data.userdata.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHandler
 * Handles JDBC operations, database initialization, and data retrieval.
 * Consolidates all dashboard, community, and user management logic.
 */
public class DatabaseHandler {
    private static final String URL      = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME  = "dbgymquest";
    private static final String FULL_URL = URL + DB_NAME;
    private static final String USER     = "root";
    private static final String PASS     = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(FULL_URL, USER, PASS);
    }

    public static void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

                // ── Users & Roles ──
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "userid INT PRIMARY KEY AUTO_INCREMENT, " +
                        "email VARCHAR(255) UNIQUE, " +
                        "password VARCHAR(255), " +
                        "firstname VARCHAR(255), " +
                        "lastname VARCHAR(255), " +
                        "type VARCHAR(50), " +
                        "avatar VARCHAR(255) DEFAULT 'user.png'," +
                        "is_active TINYINT(1) DEFAULT 1)");

                stmt.execute("CREATE TABLE IF NOT EXISTS users_archive (" +
                        "archive_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "userid INT, " +
                        "full_name VARCHAR(255), " +
                        "email VARCHAR(255), " +
                        "type VARCHAR(50), " +
                        "status VARCHAR(20) DEFAULT 'INACTIVE', " +
                        "archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                stmt.execute("CREATE TABLE IF NOT EXISTS admins (userid INT PRIMARY KEY, FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");
                stmt.execute("CREATE TABLE IF NOT EXISTS members (userid INT PRIMARY KEY, FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");
                stmt.execute("CREATE TABLE IF NOT EXISTS trainers (userid INT PRIMARY KEY, FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                // ── Trainer slots ──
                stmt.execute("CREATE TABLE IF NOT EXISTS trainer_slots (" +
                        "slot_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "trainer_id INT, " +
                        "member_id INT DEFAULT NULL, " +
                        "activity VARCHAR(255) DEFAULT 'Workout', " +
                        "slot_date DATE, " +
                        "slot_time TIME, " +
                        "duration VARCHAR(50) DEFAULT '1 Hour', " +
                        "status VARCHAR(50) DEFAULT 'Available', " +
                        "booked_by_name VARCHAR(255) DEFAULT NULL, " +
                        "FOREIGN KEY (trainer_id) REFERENCES users(userid) ON DELETE CASCADE, " +
                        "FOREIGN KEY (member_id) REFERENCES users(userid) ON DELETE SET NULL)");

                // ── Community ──
                stmt.execute("CREATE TABLE IF NOT EXISTS posts (" +
                        "postid INT PRIMARY KEY AUTO_INCREMENT, " +
                        "userid INT, " +
                        "content TEXT, " +
                        "milestone_text VARCHAR(255), " +
                        "reactions INT DEFAULT 0, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                // ── Workouts (Required by WorkoutDAO) ──
                stmt.execute("CREATE TABLE IF NOT EXISTS workouts (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "title VARCHAR(255), " +
                        "difficulty VARCHAR(50), " +
                        "duration VARCHAR(50), " +
                        "category VARCHAR(50), " +
                        "description TEXT, " +
                        "locked TINYINT(1) DEFAULT 0, " +
                        "is_custom TINYINT(1) DEFAULT 1)");

                stmt.execute("CREATE TABLE IF NOT EXISTS exercises (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "sets INT DEFAULT 3, " +
                        "reps VARCHAR(50) DEFAULT '10', " +
                        "emoji VARCHAR(20) DEFAULT '💪', " +
                        "category VARCHAR(50) DEFAULT 'strength')");

                stmt.execute("CREATE TABLE IF NOT EXISTS workout_exercises (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "workout_id INT NOT NULL, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "sets INT DEFAULT 3, " +
                        "reps VARCHAR(50) DEFAULT '10', " +
                        "emoji VARCHAR(20) DEFAULT '💪', " +
                        "category VARCHAR(50) DEFAULT 'strength', " +
                        "sort_order INT DEFAULT 0, " +
                        "FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE)");

                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

                seedExerciseLibraryIfEmpty(conn);
                registerUser("admin", "1234", "System", "Admin", "admin");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── AUTH & USER MANAGEMENT ──

    public static User authenticate(String email, String pass) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND is_active = 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return UserDAO.mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static boolean registerUser(String email, String password, String fname, String lname, String type) {
        String iu = "INSERT IGNORE INTO users (email, password, firstname, lastname, type) VALUES (?,?,?,?,?)";
        String ir = switch (type.toLowerCase()) {
            case "admin" -> "INSERT INTO admins (userid) VALUES (?)";
            case "trainer" -> "INSERT INTO trainers (userid) VALUES (?)";
            default -> "INSERT INTO members (userid) VALUES (?)";
        };
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psUser = conn.prepareStatement(iu, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, email); psUser.setString(2, password);
                psUser.setString(3, fname); psUser.setString(4, lname);
                psUser.setString(5, type.toLowerCase());
                if (psUser.executeUpdate() == 0) { conn.rollback(); return false; }
                ResultSet rs = psUser.getGeneratedKeys();
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    try (PreparedStatement psRole = conn.prepareStatement(ir)) {
                        psRole.setInt(1, newId); psRole.executeUpdate();
                    }
                }
                conn.commit(); return true;
            } catch (SQLException e) { conn.rollback(); return false; }
        } catch (SQLException e) { return false; }
    }

    public static List<User> fetchUsersByStatus(String type, boolean active) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE type = ? AND is_active = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type); ps.setInt(2, active ? 1 : 0);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(UserDAO.mapUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean archiveUser(int userId) {
        String ins = "INSERT INTO users_archive (userid, full_name, email, type) " +
                "SELECT userid, CONCAT(firstname, ' ', lastname), email, type FROM users WHERE userid = ?";
        String upd = "UPDATE users SET is_active = 0 WHERE userid = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(ins); PreparedStatement ps2 = conn.prepareStatement(upd)) {
                ps1.setInt(1, userId); ps1.executeUpdate();
                ps2.setInt(1, userId); ps2.executeUpdate();
                conn.commit(); return true;
            } catch (SQLException e) { conn.rollback(); return false; }
        } catch (SQLException e) { return false; }
    }

    public static boolean restoreUser(int userId) {
        String del = "DELETE FROM users_archive WHERE userid = ?";
        String upd = "UPDATE users SET is_active = 1 WHERE userid = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(del); PreparedStatement ps2 = conn.prepareStatement(upd)) {
                ps1.setInt(1, userId); ps1.executeUpdate();
                ps2.setInt(1, userId); ps2.executeUpdate();
                conn.commit(); return true;
            } catch (SQLException e) { conn.rollback(); return false; }
        } catch (SQLException e) { return false; }
    }

    // ── DASHBOARD STATISTICS ──

    public static int getCountByType(String type) {
        String sql = "SELECT COUNT(*) FROM users WHERE type = ? AND is_active = 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    public static int getActiveMemberCount() {
        String sql = "SELECT COUNT(DISTINCT member_id) FROM trainer_slots WHERE status = 'Booked'";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    // ── SESSIONS & BOOKING ──

    public static ResultSet getTop3ActiveSessions(int memberId) {
        String sql = "SELECT s.*, u.firstname, u.lastname FROM trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "WHERE s.member_id = ? AND s.slot_date >= CURDATE() AND s.status = 'Booked' " +
                "ORDER BY s.slot_date ASC, s.slot_time ASC LIMIT 3";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, memberId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static ResultSet getTop3HistorySessions(int memberId) {
        String sql = "SELECT s.*, u.firstname, u.lastname FROM trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "WHERE s.member_id = ? AND s.slot_date < CURDATE() " +
                "ORDER BY s.slot_date DESC, s.slot_time DESC LIMIT 3";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, memberId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static boolean saveBooking(int memberId, int slotId) {
        String sql = "UPDATE trainer_slots SET status = 'Booked', member_id = ?, " +
                "booked_by_name = (SELECT CONCAT(firstname, ' ', lastname) FROM users WHERE userid = ?) " +
                "WHERE slot_id = ? AND status = 'Available'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId); ps.setInt(2, memberId); ps.setInt(3, slotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean cancelBooking(int slotId) {
        String sql = "UPDATE trainer_slots SET status = 'Available', member_id = NULL, booked_by_name = NULL WHERE slot_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // ── ADDITIONAL HELPER LOGIC ──

    public static ResultSet getTodaySessions(int trainerId) {
        String sql = "SELECT * FROM trainer_slots WHERE trainer_id = ? AND slot_date = CURDATE() ORDER BY slot_time ASC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, trainerId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static void createPost(int userId, String content, String milestone) {
        String sql = "INSERT INTO posts (userid, content, milestone_text) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setString(2, content); ps.setString(3, milestone);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static ResultSet fetchPosts() {
        String sql = "SELECT p.*, u.firstname, u.lastname, u.avatar FROM posts p " +
                "JOIN users u ON p.userid = u.userid ORDER BY p.created_at DESC";
        try {
            Connection conn = getConnection();
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e) { return null; }
    }

    private static void seedExerciseLibraryIfEmpty(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM exercises")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        String sql = "INSERT INTO exercises (name, sets, reps, emoji, category) VALUES (?, ?, ?, ?, ?)";
        Object[][] data = {{"Push-ups", 3, "12", "💪", "strength"}, {"Squats", 3, "15", "🦵", "strength"}, {"Plank", 3, "30s", "🧘", "core"}};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] r : data) {
                ps.setString(1, (String)r[0]); ps.setInt(2, (int)r[1]); ps.setString(3, (String)r[2]);
                ps.setString(4, (String)r[3]); ps.setString(5, (String)r[4]); ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    public static boolean updateUser(int userId, String email, String fname, String lname, String type) {
        String updateUsersTable = "UPDATE users SET email = ?, firstname = ?, lastname = ?, type = ? WHERE userid = ?";

        // SQL to manage role-specific table entries during a role change
        String deleteOldRole = "DELETE FROM %s WHERE userid = ?";
        String insertNewRole = "INSERT IGNORE INTO %s (userid) VALUES (?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement ps = conn.prepareStatement(updateUsersTable)) {
                // 1. Get current role to check for changes
                String currentType = "";
                try (PreparedStatement checkPs = conn.prepareStatement("SELECT type FROM users WHERE userid = ?")) {
                    checkPs.setInt(1, userId);
                    ResultSet rs = checkPs.executeQuery();
                    if (rs.next()) currentType = rs.getString("type").toLowerCase();
                }

                // 2. Update the main users table
                ps.setString(1, email);
                ps.setString(2, fname);
                ps.setString(3, lname);
                ps.setString(4, type.toLowerCase());
                ps.setInt(5, userId);

                int rowsAffected = ps.executeUpdate();

                // 3. If type changed, sync role-specific tables
                if (rowsAffected > 0 && !currentType.equals(type.toLowerCase())) {
                    // Remove from old role table (e.g., members)
                    String oldTable = getTableNameFromRole(currentType);
                    if (oldTable != null) {
                        try (PreparedStatement delPs = conn.prepareStatement(String.format(deleteOldRole, oldTable))) {
                            delPs.setInt(1, userId);
                            delPs.executeUpdate();
                        }
                    }

                    // Add to new role table (e.g., trainers)
                    String newTable = getTableNameFromRole(type.toLowerCase());
                    if (newTable != null) {
                        try (PreparedStatement insPs = conn.prepareStatement(String.format(insertNewRole, newTable))) {
                            insPs.setInt(1, userId);
                            insPs.executeUpdate();
                        }
                    }
                }

                conn.commit(); // Finalize all changes
                return rowsAffected > 0;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper to map user types to their respective database table names.
     */
    private static String getTableNameFromRole(String type) {
        return switch (type.toLowerCase()) {
            case "admin" -> "admins";
            case "trainer" -> "trainers";
            case "member" -> "members";
            default -> null;
        };
    }

    public static List<String> fetchTrainersNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT CONCAT(firstname, ' ', lastname) as full_name FROM users WHERE type = 'trainer' AND is_active = 1";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                names.add(rs.getString("full_name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return names;
    }

    public static ResultSet getTrainerSchedule(int trainerId) {
        String sql = "SELECT * FROM trainer_slots WHERE trainer_id = ? ORDER BY slot_date ASC, slot_time ASC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, trainerId);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean deleteSlot(int slotId) {
        String sql = "DELETE FROM trainer_slots WHERE slot_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static ResultSet getMemberSessionsToday(int memberId) {
        String sql = "SELECT s.*, u.firstname, u.lastname, " +
                "TIME_FORMAT(s.slot_time, '%h:%i %p') as formatted_time " +
                "FROM trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "WHERE s.member_id = ? AND s.slot_date = CURDATE() " +
                "ORDER BY s.slot_time ASC";
        try {
            // Note: Connection remains open so the ResultSet can be read by the caller.
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, memberId);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static int getUniqueClientCount(int trainerId) {
        String sql = "SELECT COUNT(DISTINCT member_id) FROM trainer_slots " +
                "WHERE trainer_id = ? AND member_id IS NOT NULL";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean addTrainerSlot(int tid, String type, String date, String time, String dur) {
        String sql = "INSERT INTO trainer_slots (trainer_id, activity, slot_date, slot_time, duration, status) " +
                "VALUES (?, ?, ?, ?, ?, 'Available')";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tid);
            ps.setString(2, type);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, dur);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUserName(int userId, String firstName, String lastName) {
        String sql = "UPDATE users SET firstname = ?, lastname = ? WHERE userid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUserAvatar(int userId, String avatarName) {
        String sql = "UPDATE users SET avatar = ? WHERE userid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, avatarName);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updatePostReactionCount(int postId, int newCount) {
        String sql = "UPDATE posts SET reactions = ? WHERE postid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newCount);
            ps.setInt(2, postId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getAvailableSlots(String date) {
        String sql = "SELECT s.*, u.firstname, u.lastname, u.avatar " +
                "FROM trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "WHERE s.slot_date = ? AND s.status = 'Available' " +
                "ORDER BY s.slot_time ASC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, date);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error fetching slots for date: " + date);
            e.printStackTrace();
            return null;
        }
    }

    public static List<ArchivedUser> fetchArchivedUsers() {
        List<ArchivedUser> list = new ArrayList<>();
        String sql = "SELECT * FROM users_archive ORDER BY archived_at DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ArchivedUser(
                        rs.getInt("userid"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("type"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static ResultSet getTrainerFullSchedule(int trainerId) {
        String sql = "SELECT s.*, u.firstname, u.lastname, u.email, u.avatar " +
                "FROM trainer_slots s " +
                "LEFT JOIN users u ON s.member_id = u.userid " +
                "WHERE s.trainer_id = ? " +
                "ORDER BY s.slot_date DESC, s.slot_time DESC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, trainerId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static ResultSet getMemberBookings(int memberId) {
        String sql = "SELECT s.*, u.firstname, u.lastname, u.avatar " +
                "FROM trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "WHERE s.member_id = ? " +
                "AND s.status = 'Booked' " +
                "AND s.slot_date >= CURRENT_DATE " + // <--- THE FILTER
                "ORDER BY s.slot_date ASC, s.slot_time ASC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, memberId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }
}