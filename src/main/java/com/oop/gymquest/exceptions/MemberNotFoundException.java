package com.oop.gymquest.exceptions;

public class MemberNotFoundException extends Exception {
    public MemberNotFoundException(String memberId) {
        super("Search Error: No gym member found with ID: " + memberId);
    }
}
