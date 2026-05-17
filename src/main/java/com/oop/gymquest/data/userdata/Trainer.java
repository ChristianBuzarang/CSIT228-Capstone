package com.oop.gymquest.data.userdata;
public class Trainer extends User {
    public Trainer(int id, String email, String password, String firstname, String lastname, String type, String avatar, boolean isActive) {
        super(id, email, password, firstname, lastname, type, avatar, isActive);
    }
    @Override
    public String getType() { return "trainer"; }

    @Override
    public void userInfo(){
        System.out.println(getType().toUpperCase() + " - " + getFirstName() + " " + getLastName() + " ");
    }
}