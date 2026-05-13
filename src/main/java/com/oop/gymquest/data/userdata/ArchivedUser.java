package com.oop.gymquest.data.userdata;

public class ArchivedUser {
    private int id;
    private String fullName, email, type, status;
    public ArchivedUser(int id, String name, String email, String type, String status) {
        this.id = id; this.fullName = name; this.email = email; this.type = type; this.status = status;
    }
    public int getUserId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getType() { return type; }
    public String getStatus() { return status; }
}