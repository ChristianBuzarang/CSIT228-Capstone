package com.oop.gymquest.data.userdata;

public class Admin extends User {
    public Admin(int id, String email, String pass, String firstname, String lastname, String type, String avatar, boolean isActive) {
        super(id, email, pass, firstname, lastname, type, avatar, isActive);
    }
    @Override
    public String getType() { return "admin"; }
}