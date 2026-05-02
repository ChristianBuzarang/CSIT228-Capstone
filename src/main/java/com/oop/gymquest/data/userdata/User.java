package com.oop.gymquest.data.userdata;
import java.io.Serializable;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int userid;
    protected String email;
    protected transient String password;
    protected String firstname;
    protected String lastname;

    public User(int userId, String email, String password, String firstname, String lastname) {
        this.userid = userId;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public abstract String getType(); // Abstract method to force subclasses to identify themselves

    // Getters
    public int getUserId() { return userid; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
}