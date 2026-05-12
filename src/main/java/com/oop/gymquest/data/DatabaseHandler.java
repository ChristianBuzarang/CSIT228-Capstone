package com.oop.gymquest.data;

import com.oop.gymquest.data.userdata.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "dbgymquest";
    private static final String FULL_URL = URL + DB_NAME;
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(FULL_URL, USER, PASS);
    }

    public static void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
                Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                System.out.println("Database " + DB_NAME + " created.");
            }

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
//                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
//                stmt.execute("DROP TABLE IF EXISTS users, users_archive, admins, members, trainers, trainer_slots, posts, workouts");
//                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "userid INT PRIMARY KEY AUTO_INCREMENT, " +
                        "email VARCHAR(255) UNIQUE, " +
                        "password VARCHAR(255), " +
                        "firstname VARCHAR(255), " +
                        "lastname VARCHAR(255), " +
                        "type VARCHAR(50), " +
                        "avatar VARCHAR(255) DEFAULT 'user.png')");

                stmt.execute("CREATE TABLE IF NOT EXISTS users_archive (" +
                        "userid INT, " +
                        "email VARCHAR(255), " +
                        "password VARCHAR(255), " +
                        "firstname VARCHAR(255), " +
                        "lastname VARCHAR(255), " +
                        "type VARCHAR(50), " +
                        "avatar VARCHAR(255) DEFAULT 'user.png', " +
                        "archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                stmt.execute("CREATE TABLE IF NOT EXISTS admins (userid INT PRIMARY KEY, " +
                        "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                stmt.execute("CREATE TABLE IF NOT EXISTS members (userid INT PRIMARY KEY, " +
                        "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                stmt.execute("CREATE TABLE IF NOT EXISTS trainers (userid INT PRIMARY KEY, " +
                        "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                stmt.execute("CREATE TABLE IF NOT EXISTS trainer_slots (" +
                        "slot_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "trainer_id INT, " +
                        "member_id INT DEFAULT NULL, " +
                        "activity VARCHAR(255), " +
                        "slot_date DATE, " +
                        "slot_time VARCHAR(50), " +
                        "duration VARCHAR(50), " +
                        "status VARCHAR(50) DEFAULT 'Available', " +
                        "booked_by_name VARCHAR(255) DEFAULT NULL, " +
                        "FOREIGN KEY (trainer_id) REFERENCES users(userid) ON DELETE CASCADE, " +
                        "FOREIGN KEY (member_id) REFERENCES users(userid) ON DELETE SET NULL)");

                stmt.execute("CREATE TABLE IF NOT EXISTS posts (" +
                        "postid INT PRIMARY KEY AUTO_INCREMENT, " +
                        "userid INT, " +
                        "content TEXT, " +
                        "milestone_text VARCHAR(255), " +
                        "reactions INT DEFAULT 0, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE)");

                stmt.execute("CREATE TABLE IF NOT EXISTS workouts (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "title VARCHAR(255), " +
                        "difficulty VARCHAR(50), " +
                        "duration VARCHAR(50), " +
                        "category VARCHAR(50), " +
                        "description TEXT, " +
                        "is_custom TINYINT(1))");

                registerUser("admin", "1234", "System", "Admin", "admin");
                System.out.println("✅ Database Ready: All functions and tables restored.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User authenticate(String email, String pass) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("userid");
                String type = rs.getString("type").toLowerCase();
                String fn = rs.getString("firstname");
                String ln = rs.getString("lastname");
                String em = rs.getString("email");
                String pw = rs.getString("password");
                String avatar = rs.getString("avatar");
                if (avatar == null || avatar.isEmpty()) avatar = "user.png";
                return switch (type) {
                    case "admin" -> new Admin(id, em, pw, fn, ln, type, avatar);
                    case "trainer" -> new Trainer(id, em, pw, fn, ln, type, avatar);
                    default -> new Member(id, em, pw, fn, ln, type, avatar);
                };
            }
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
                psUser.setString(1, email);
                psUser.setString(2, password);
                psUser.setString(3, fname);
                psUser.setString(4, lname);
                psUser.setString(5, type.toLowerCase());
                if (psUser.executeUpdate() == 0) return false;
                ResultSet rs = psUser.getGeneratedKeys();
                if (rs.next()) {
                    PreparedStatement pr = conn.prepareStatement(ir);
                    pr.setInt(1, rs.getInt(1));
                    pr.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) { conn.rollback(); return false; }
        } catch (SQLException e) { return false; }
    }

    public static boolean updateUser(int userId, String email, String fname, String lname, String type) {
        String sql = "UPDATE users SET email = ?, firstname = ?, lastname = ?, type = ? WHERE userid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, fname);
            ps.setString(3, lname);
            ps.setString(4, type);
            ps.setInt(5, userId);
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

    public static boolean archiveUser(int userId) {
        String insertArchive = "INSERT INTO users_archive (userid, email, password, firstname, lastname, type) " +
                "SELECT userid, email, password, firstname, lastname, type FROM users WHERE userid = ?";
        String deleteActive = "DELETE FROM users WHERE userid = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(insertArchive);
                 PreparedStatement ps2 = conn.prepareStatement(deleteActive)) {

                ps1.setInt(1, userId);
                ps1.executeUpdate();

                ps2.setInt(1, userId);
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) { return false; }
    }

    // --- TRAINER DASHBOARD METHODS ---

    public static ResultSet getTodaySessions(int trainerId) {
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM trainer_slots WHERE trainer_id = ? AND slot_date = CURRENT_DATE ORDER BY slot_time ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, trainerId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static int getUniqueClientCount(int trainerId) {
        String sql = "SELECT COUNT(DISTINCT member_id) FROM trainer_slots WHERE trainer_id = ? AND status = 'Booked'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    public static ResultSet getTrainerSchedule(int tid) {
        try {
            Connection conn = getConnection();
            String sql = (tid == 0) ?
                    "SELECT s.*, CONCAT(u.firstname, ' ', u.lastname) as coach_name FROM trainer_slots s JOIN users u ON s.trainer_id = u.userid" :
                    "SELECT s.* FROM trainer_slots s WHERE s.trainer_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql + " ORDER BY slot_date ASC");
            if (tid != 0) ps.setInt(1, tid);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
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
            ps.setInt(1, sid); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // --- MEMBER DASHBOARD METHODS ---

    public static ResultSet getMemberBookings(int memberId) {
        String sql = "SELECT s.*, CONCAT(u.firstname, ' ', u.lastname) as coach_name " +
                "FROM trainer_slots s JOIN users u ON s.trainer_id = u.userid " +
                "WHERE s.member_id = ? AND s.status = 'Booked' ORDER BY s.slot_date ASC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, memberId);
            return ps.executeQuery();
        } catch (SQLException e) { return null; }
    }

    public static boolean saveBooking(int uid, String coach, String date, String time) {
        String sql = "UPDATE trainer_slots s JOIN users u ON s.trainer_id = u.userid " +
                "SET s.status='Booked', s.member_id=?, s.booked_by_name=(SELECT CONCAT(firstname,' ',lastname) FROM users WHERE userid=?) " +
                "WHERE CONCAT(u.firstname,' ',u.lastname)=? AND s.slot_date=? AND s.slot_time=? AND s.status='Available'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, uid);
            ps.setInt(2, uid);
            ps.setString(3, coach);
            ps.setString(4, date);
            ps.setString(5, time);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean isSlotBooked(String coach, String date, String time) {
        String sql = "SELECT COUNT(*) FROM trainer_slots s JOIN users u ON s.trainer_id = u.userid " +
                "WHERE CONCAT(u.firstname,' ',u.lastname) = ? AND s.slot_date = ? AND s.slot_time = ? AND s.status = 'Booked'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coach);
            ps.setString(2, date);
            ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean cancelBooking(int sid) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE trainer_slots SET status='Available', member_id=NULL, booked_by_name=NULL WHERE slot_id=?")) {
            ps.setInt(1, sid); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // --- ADMIN STATS ---

    public static int getCountByType(String type) {
        String sql = "SELECT COUNT(*) FROM users WHERE type = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    public static int getActiveMemberCount() {
        try (Connection conn = getConnection(); Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT COUNT(DISTINCT member_id) FROM trainer_slots WHERE status = 'Booked'");
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    public static List<User> fetchUsersByRole(String type) {
        List<User> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE type = ?")) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("userid");
                String fn = rs.getString("firstname");
                String ln = rs.getString("lastname");
                String em = rs.getString("email");
                String pw = rs.getString("password");
                String avatar = rs.getString("avatar");
                if (type.equalsIgnoreCase("admin")) list.add(new Admin(id, em, pw, fn, ln, type, avatar));
                else if (type.equalsIgnoreCase("trainer")) list.add(new Trainer(id, em, pw, fn, ln, type, avatar));
                else list.add(new Member(id, em, pw, fn, ln, type, avatar));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
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

    public static void createPost(int userId, String content, String milestone) {
        String sql = "INSERT INTO posts (userid, content, milestone_text) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, content);
            ps.setString(3, milestone);
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
}