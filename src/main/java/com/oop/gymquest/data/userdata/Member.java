package com.oop.gymquest.data.userdata;

public class Member extends User {
    public Member(int id, String email, String password, String firstname, String lastname, String type, String avatar) {
        super(id, email, password, firstname, lastname, type, avatar);
    }
    @Override
    public String getType() { return "member"; }
}