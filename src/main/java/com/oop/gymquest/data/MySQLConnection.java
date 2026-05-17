package com.oop.gymquest.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    public static final String URL      = "jdbc:mysql://localhost:3306/dbgymquest";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    private static MySQLConnection instance;

    private MySQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized MySQLConnection getInstance() {
        if (instance == null) {
            instance = new MySQLConnection();
            System.out.println("MySQLConnection Singleton Initialized.");
        }
        return instance;
    }

    public Connection getDbConnection() {
        Connection c = null;
        try {
            c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static Connection getConnection() {
        return getInstance().getDbConnection();
    }
}