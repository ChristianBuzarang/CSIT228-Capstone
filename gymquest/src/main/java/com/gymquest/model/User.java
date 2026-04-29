package com.gymquest.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String type; // member, trainer
    private String avatar;
    private String status; // active, inactive
    private String joinDate;

    public User(int id, String name, String email, String type, String avatar, String status, String joinDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.avatar = avatar;
        this.status = status;
        this.joinDate = joinDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getType() { return type; }
    public String getAvatar() { return avatar; }
    public String getStatus() { return status; }
    public String getJoinDate() { return joinDate; }
}
