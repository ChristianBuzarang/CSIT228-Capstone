package com.oop.gymquest.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    // --- CHANGE THESE TO MATCH YOUR XAMPP SETUP ---
    public static final String URL      = "jdbc:mysql://localhost:3306/dbgymquest";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "jc@020814";          // leave blank if no password
    // -----------------------------------------------

    public static Connection getConnection() {
        Connection c = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return c;
    }
}
