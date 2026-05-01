package com.oop.gymquest.data;

import java.io.Serializable;

public class User implements Serializable {
//    private int userId;
//    private String username;
//    private transient String password; // Marked transient for security
//    private String firstName;
//    private String lastName;

    private static final long serialVersionUID = 1L;
    private int userid;
    private String email;
    private transient String password;
    private String firstname;
    private String lastname;
    private String type; // member, trainer, admin

    public User(int userId, String email, String password, String firstname, String lastname, String type) {
        this.userid = userId;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.type = type;
    }

    public int getUserId() { return userid; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getType() { return type; }
}
