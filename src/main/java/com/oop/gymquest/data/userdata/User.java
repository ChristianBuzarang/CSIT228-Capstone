package com.oop.gymquest.data.userdata;
import java.io.Serializable;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int userid;
    protected String email;
    protected transient String password;
    protected String firstname;
    protected String lastname;
    protected String type;
    protected String avatar;

    public User(int userId, String email, String password, String firstname, String lastname, String type, String avatar) {
        this.userid = userId;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.type = type;
        this.avatar = avatar;
    }

    public int getUserId() { return userid; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstname; }
    public String getLastName() { return lastname; }
    public String getFullName() { return firstname + " " + lastname; }
    public String getType() { return type; };
    public String getAvatar() { return avatar; }

    public void setFirstName(String firstname) { this.firstname = firstname; }
    public void setLastName(String lastname) { this.lastname = lastname; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}