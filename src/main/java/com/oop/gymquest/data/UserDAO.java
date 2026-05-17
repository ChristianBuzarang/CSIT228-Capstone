package com.oop.gymquest.data;

import com.oop.gymquest.data.userdata.Admin;
import com.oop.gymquest.data.userdata.Member;
import com.oop.gymquest.data.userdata.Trainer;
import com.oop.gymquest.data.userdata.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("userid");
        String email = rs.getString("email");
        String pass = rs.getString("password");
        String fname = rs.getString("firstname");
        String lname = rs.getString("lastname");
        String type = rs.getString("type").toLowerCase();
        String avatar = rs.getString("avatar");
        boolean is_active = rs.getBoolean("is_active");

        return switch (type) {
            case "admin" -> new Admin(id, email, pass, fname, lname, type, avatar, is_active);
            case "trainer" -> new Trainer(id, email, pass, fname, lname, type, avatar, is_active);
            default -> new Member(id, email, pass, fname, lname, type, avatar, is_active);
        };
    }

    public static List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection c = MySQLConnection.getConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean create(String email, String password, String fname, String lname, String type) {
        String sql = "INSERT INTO users (email, password, firstname, lastname, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, fname);
            ps.setString(4, lname);
            ps.setString(5, type.toLowerCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}