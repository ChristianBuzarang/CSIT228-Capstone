package com.oop.gymquest.data.userdata;
public class Trainer extends User {
    public Trainer(int id, String email, String password, String firstname, String lastname) {
        super(id, email, password, firstname, lastname);
    }
    @Override
    public String getType() { return "trainer"; }
}