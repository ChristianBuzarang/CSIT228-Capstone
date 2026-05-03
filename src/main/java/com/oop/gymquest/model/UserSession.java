package com.oop.gymquest.model;


public class UserSession {
    private static User currentUser;

    public static void login(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    public static void logout() { currentUser = null; }
}