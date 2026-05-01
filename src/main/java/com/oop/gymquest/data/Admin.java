package com.oop.gymquest.data;

public class Admin extends User {
    public Admin(int id, String email, String pass, String firstname, String lastname) {
        super(id, email, pass, firstname, lastname);
    }
    @Override
    public String getType() { return "admin"; }
}