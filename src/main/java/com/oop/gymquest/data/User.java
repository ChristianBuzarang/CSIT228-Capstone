package com.oop.gymquest.data;

import java.io.Serializable;

public class User implements Serializable {
    private int userId;
    private String username;
    private transient String password; // Marked transient for security
    private String firstName;
    private String lastName;

    public User(int userId, String username, String password, String firstName, String lastName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
