package com.oop.gymquest.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInit {

    public static void initDatabase() {
        try (Connection c = MySQLConnection.getConnection();
             Statement statement = c.createStatement()) {

            String createTable =
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "  userid   INT PRIMARY KEY AUTO_INCREMENT, " +
                            "  username  VARCHAR(255) NOT NULL UNIQUE, " +
                            "  password  VARCHAR(255) NOT NULL, " +
                            "  firstname VARCHAR(255) NOT NULL," +
                            "  lastname VARCHAR(255) NOT NULL" +
                            ")";
            statement.execute(createTable);
            System.out.println("Table 'users' ready.");
            String createPostsTable =
                    "CREATE TABLE IF NOT EXISTS posts (" +
                            "  postid    INT PRIMARY KEY AUTO_INCREMENT, " +
                            "  username  VARCHAR(255), " +
                            "  post_type VARCHAR(50), " +
                            "  content   TEXT, " +
                            "  milestone VARCHAR(255), " +
                            "  reactions INT DEFAULT 0" +
                            ")";
            statement.execute(createPostsTable);
            System.out.println("Table 'posts' ready.");

            insertIfAbsent(c, "jdela_cruz", "password123", "Juan", "Dela Cruz");
            insertIfAbsent(c, "asmith",     "pass456",     "Alice", "Smith");

            System.out.println("Database initialized.");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void insertIfAbsent(Connection c, String username, String password, String firstname, String lastname) throws SQLException {
        String checkSql  = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password, firstname, lastname) VALUES (?, ?, ?, ?)";

        try (PreparedStatement check = c.prepareStatement(checkSql)) {
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insert = c.prepareStatement(insertSql)) {
                    insert.setString(1, username);
                    insert.setString(2, password);
                    insert.setString(3, firstname);
                    insert.setString(4, lastname);
                    insert.executeUpdate();
                    System.out.println("Inserted sample user: " + username);
                }
            }
        }
    }

    public static void main(String[] args) {
        initDatabase();
    }
}
