package com.oop.gymquest.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // 1. CREATE (Register)
    public static boolean create(String email, String password, String fname, String lname, String type) {
        String sql = "INSERT INTO users (email, password, firstname, lastname, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, fname);
            ps.setString(4, lname);
            ps.setString(5, type);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. READ (Get all users)
    public static List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection c = MySQLConnection.getConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("userid"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("type")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 3. READ (Find by Username - used for Login)
    public static User getByUsername(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("userid"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("type")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // 4. UPDATE
    public static boolean update(int userId, String fname, String lname) {
        String sql = "UPDATE users SET firstname = ?, lastname = ? WHERE userid = ?";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. DELETE
    public static boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE userid = ?";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
