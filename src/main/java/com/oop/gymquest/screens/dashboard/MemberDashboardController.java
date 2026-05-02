package com.oop.gymquest.screens.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MemberDashboardController {
    @FXML private Label goalLabel, streakLabel;

    @FXML
    public void initialize() {
        // Member specific logic here (e.g. counting workouts)
        System.out.println("Member Dashboard Loaded successfully.");
    }
}