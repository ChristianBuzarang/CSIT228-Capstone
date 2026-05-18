package com.oop.gymquest.data;

import com.oop.gymquest.data.userdata.*;
import com.oop.gymquest.exceptions.BookingConflictException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL      = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME  = "dbgymquest";
    private static final String FULL_URL = URL + DB_NAME;
    private static final String USER     = "root";
    private static final String PASS     = "";

    private static DatabaseHandler instance;

    private DatabaseHandler() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    public static synchronized DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    public Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(FULL_URL, USER, PASS);
    }

    public static Connection getConnection() throws SQLException {
        return getInstance().getDbConnection();
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

                // Tracks User Likes to retain state across sessions
                stmt.execute("CREATE TABLE IF NOT EXISTS post_likes (" +
                        "userid INT, " +
                        "postid INT, " +
                        "PRIMARY KEY (userid, postid), " +
                        "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE, " +
                        "FOREIGN KEY (postid) REFERENCES posts(postid) ON DELETE CASCADE)");

                // ── Workouts ──────────────────────────────────────────────────
                stmt.execute("CREATE TABLE IF NOT EXISTS workouts (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "title VARCHAR(255), " +
                        "difficulty VARCHAR(50), " +
                        "duration VARCHAR(50), " +
                        "category VARCHAR(50), " +
                        "description TEXT, " +
                        "locked TINYINT(1) DEFAULT 0, " +
                        "is_custom TINYINT(1) DEFAULT 1, " +
                        "created_by INT DEFAULT NULL, " +
                        "FOREIGN KEY (created_by) REFERENCES users(userid) ON DELETE SET NULL)");

                stmt.execute("CREATE TABLE IF NOT EXISTS exercises (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "sets INT DEFAULT 3, " +
                        "reps VARCHAR(50) DEFAULT '10', " +
                        "emoji VARCHAR(20) DEFAULT '', " +
                        "category VARCHAR(50) DEFAULT 'strength')");

                stmt.execute("CREATE TABLE IF NOT EXISTS workout_exercises (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "workout_id INT NOT NULL, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "sets INT DEFAULT 3, " +
                        "reps VARCHAR(50) DEFAULT '10', " +
                        "emoji VARCHAR(20) DEFAULT '', " +
                        "category VARCHAR(50) DEFAULT 'strength', " +
                        "sort_order INT DEFAULT 0, " +
                        "FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE)");

                stmt.execute("CREATE TABLE IF NOT EXISTS slot_workouts (" +
                        "slot_id INT NOT NULL, " +
                        "workout_id INT NOT NULL, " +
                        "attached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "PRIMARY KEY (slot_id, workout_id), " +
                        "FOREIGN KEY (slot_id) REFERENCES trainer_slots(slot_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE)");

                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

                try {
                    stmt.execute(
                        "ALTER TABLE workouts " +
                        "ADD COLUMN created_by INT DEFAULT NULL, " +
                        "ADD CONSTRAINT fk_workout_creator " +
                        "FOREIGN KEY (created_by) REFERENCES users(userid) ON DELETE SET NULL"
                    );
                    System.out.println("[DatabaseHandler] Migration: added created_by to workouts.");
                } catch (SQLException ignored) { }

                seedExerciseLibraryIfEmpty(conn);
                registerUser("admin", "1234", "System", "Admin", "admin");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

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
        boolean isArchive = "archive".equalsIgnoreCase(type);
        String sql = isArchive
                ? "SELECT * FROM users WHERE is_active = 0"
                : "SELECT * FROM users WHERE type = ? AND is_active = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!isArchive) {
                ps.setString(1, type);
                ps.setInt(2, active ? 1 : 0);
            }
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

    // ── SESSIONS & BOOKING ──

    public static ResultSet getTop3ActiveSessions(int memberId) {
        String sql = "SELECT s.*, u.firstname, u.lastname FROM trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "WHERE s.member_id = ? AND s.status = 'Booked' " +
                "AND (s.slot_date > CURDATE() OR (s.slot_date = CURDATE() AND ADDTIME(s.slot_time, '01:00:00') >= CURTIME())) " +
                "ORDER BY s.slot_date ASC, s.slot_time ASC";
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
                "WHERE s.member_id = ? AND s.status = 'Booked' " +
                "AND (s.slot_date < CURDATE() OR (s.slot_date = CURDATE() AND ADDTIME(s.slot_time, '01:00:00') < CURTIME())) " +
                "ORDER BY s.slot_date DESC, s.slot_time DESC LIMIT 3";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, memberId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static ResultSet getAllHistorySessions(int memberId) {
        String sql = "SELECT s.*, u.firstname, u.lastname FROM trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "WHERE s.member_id = ? AND s.status = 'Booked' " +
                "AND (s.slot_date < CURDATE() OR (s.slot_date = CURDATE() AND ADDTIME(s.slot_time, '01:00:00') < CURTIME())) " +
                "ORDER BY s.slot_date DESC, s.slot_time DESC";
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

    public static ResultSet getTodaySessions(int trainerId) {
        String sql = "SELECT ts.*, u.avatar FROM trainer_slots ts " +
                "LEFT JOIN users u ON ts.member_id = u.userid " +
                "WHERE ts.trainer_id = ? " +
                "ORDER BY ts.slot_date DESC, ts.slot_time ASC LIMIT 3";
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

    public static void createPost(int userId, String content, String milestone) {
        String sql = "INSERT INTO posts (userid, content, milestone_text) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setString(2, content); ps.setString(3, milestone);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static ResultSet fetchPosts(int currentUserId) {
        String sql = "SELECT p.*, u.firstname, u.lastname, u.avatar, " +
                "(SELECT COUNT(*) FROM post_likes pl WHERE pl.postid = p.postid AND pl.userid = ?) AS is_liked " +
                "FROM posts p " +
                "JOIN users u ON p.userid = u.userid ORDER BY p.created_at DESC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static boolean togglePostLike(int userId, int postId) {
        String checkSql = "SELECT * FROM post_likes WHERE userid = ? AND postid = ?";
        boolean isLiked = false;
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, userId);
                checkPs.setInt(2, postId);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next()) {
                    // Already Liked -> Unlike
                    try (PreparedStatement delPs = conn.prepareStatement("DELETE FROM post_likes WHERE userid = ? AND postid = ?");
                         PreparedStatement updatePs = conn.prepareStatement("UPDATE posts SET reactions = GREATEST(0, reactions - 1) WHERE postid = ?")) {
                        delPs.setInt(1, userId); delPs.setInt(2, postId); delPs.executeUpdate();
                        updatePs.setInt(1, postId); updatePs.executeUpdate();
                    }
                    isLiked = false;
                } else {
                    // Not Liked -> Like
                    try (PreparedStatement insPs = conn.prepareStatement("INSERT INTO post_likes (userid, postid) VALUES (?, ?)");
                         PreparedStatement updatePs = conn.prepareStatement("UPDATE posts SET reactions = reactions + 1 WHERE postid = ?")) {
                        insPs.setInt(1, userId); insPs.setInt(2, postId); insPs.executeUpdate();
                        updatePs.setInt(1, postId); updatePs.executeUpdate();
                    }
                    isLiked = true;
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return isLiked;
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
        String deleteOldRole = "DELETE FROM %s WHERE userid = ?";
        String insertNewRole = "INSERT IGNORE INTO %s (userid) VALUES (?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(updateUsersTable)) {
                String currentType = "";
                try (PreparedStatement checkPs = conn.prepareStatement("SELECT type FROM users WHERE userid = ?")) {
                    checkPs.setInt(1, userId);
                    ResultSet rs = checkPs.executeQuery();
                    if (rs.next()) currentType = rs.getString("type").toLowerCase();
                }

                ps.setString(1, email);
                ps.setString(2, fname);
                ps.setString(3, lname);
                ps.setString(4, type.toLowerCase());
                ps.setInt(5, userId);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0 && !currentType.equals(type.toLowerCase())) {
                    String oldTable = getTableNameFromRole(currentType);
                    if (oldTable != null) {
                        try (PreparedStatement delPs = conn.prepareStatement(String.format(deleteOldRole, oldTable))) {
                            delPs.setInt(1, userId);
                            delPs.executeUpdate();
                        }
                    }
                    String newTable = getTableNameFromRole(type.toLowerCase());
                    if (newTable != null) {
                        try (PreparedStatement insPs = conn.prepareStatement(String.format(insertNewRole, newTable))) {
                            insPs.setInt(1, userId);
                            insPs.executeUpdate();
                        }
                    }
                }

                conn.commit();
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

    public static boolean addTrainerSlot(int tid, String type, String date, String time, String dur, String trainerName) throws BookingConflictException {
        String checkSql = "SELECT COUNT(*) FROM trainer_slots WHERE trainer_id = ? AND slot_date = ? AND slot_time = ?";
        try (Connection conn = getConnection(); PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, tid);
            psCheck.setString(2, date);
            psCheck.setString(3, time);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new BookingConflictException(trainerName, time + " on " + date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO trainer_slots (trainer_id, activity, slot_date, slot_time, duration, status) VALUES (?, ?, ?, ?, ?, 'Available')";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

    public static ResultSet getTrainerFullSchedule(int trainerId) {
        String sql = "SELECT s.*, u.firstname, u.lastname, u.email, u.avatar " +
                "FROM trainer_slots s " +
                "LEFT JOIN users u ON s.member_id = u.userid " +
                "WHERE s.trainer_id = ? " +
                "ORDER BY s.status ASC, s.slot_date DESC, s.slot_time ASC";
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

    public static ResultSet getMemberBookingsForTrainer(int trainerId) {
        String sql = "SELECT DISTINCT u.firstname, u.lastname, u.avatar FROM users u " +
                "JOIN trainer_slots ts ON u.userid = ts.member_id " +
                "WHERE ts.trainer_id = ? AND ts.status = 'Booked' " +
                "ORDER BY ts.slot_id DESC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, trainerId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static int getTrainerTotalSlotsCount(int trainerId) {
        String query = "SELECT COUNT(*) FROM trainer_slots WHERE trainer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, trainerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total trainer slots: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public static ResultSet getAdminAllSchedulesByDate(String date, String trainerFilter) {
        String sql = "SELECT ts.*, u.firstname AS t_fname, u.lastname AS t_lname, " +
                "m.firstname AS m_fname, m.lastname AS m_lname " +
                "FROM trainer_slots ts " +
                "JOIN users u ON ts.trainer_id = u.userid " +
                "LEFT JOIN users m ON ts.member_id = m.userid " +
                "WHERE ts.slot_date = ? ";

        if (trainerFilter != null && !trainerFilter.equals("All Trainers")) {
            sql += " AND CONCAT(u.firstname, ' ', u.lastname) = ?";
        }

        sql += " ORDER BY ts.slot_time ASC";

        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, date);
            if (trainerFilter != null && !trainerFilter.equals("All Trainers")) {
                ps.setString(2, trainerFilter);
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getSessionsDoneCount(int userId, String role) {
        String column = role.equalsIgnoreCase("trainer") ? "trainer_id" : "member_id";
        String sql = "SELECT COUNT(*) FROM trainer_slots WHERE " + column + " = ? AND status = 'Booked' " +
                "AND (slot_date < CURDATE() OR (slot_date = CURDATE() AND ADDTIME(slot_time, '01:00:00') < CURTIME()))";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int getCurrentStreak(int userId, String role) {
        String column = role.equalsIgnoreCase("trainer") ? "trainer_id" : "member_id";
        String sql = "SELECT DISTINCT slot_date FROM trainer_slots WHERE " + column + " = ? AND status = 'Booked' " +
                "AND (slot_date < CURDATE() OR (slot_date = CURDATE() AND ADDTIME(slot_time, '01:00:00') < CURTIME())) " +
                "ORDER BY slot_date DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate expectedDate = today;
            int streak = 0;
            boolean first = true;

            while (rs.next()) {
                java.time.LocalDate dbDate = rs.getDate(1).toLocalDate();
                if (first) {
                    if (dbDate.equals(today) || dbDate.equals(today.minusDays(1))) {
                        streak = 1;
                        expectedDate = dbDate.minusDays(1);
                        first = false;
                    } else {
                        break;
                    }
                } else {
                    if (dbDate.equals(expectedDate)) {
                        streak++;
                        expectedDate = expectedDate.minusDays(1);
                    } else {
                        break;
                    }
                }
            }
            return streak;
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static boolean updateTrainerSlot(int slotId, String type, String date, String time, String dur) {
        String sql = "UPDATE trainer_slots SET activity = ?, slot_date = ?, slot_time = ?, duration = ? WHERE slot_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setString(4, dur);
            ps.setInt(5, slotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
