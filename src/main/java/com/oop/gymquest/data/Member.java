package com.oop.gymquest.data;
public class Member extends User {
    public Member(int id, String email, String password, String firstname, String lastname) {
        super(id, email, password, firstname, lastname);
    }
    @Override
    public String getType() { return "member"; }
}