package com.oop.gymquest.screens.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class TrainerDashboardController {
    @FXML private Label clientCountLabel;
    @FXML private ListView<String> sessionListView;

    @FXML
    public void initialize() {
        // Add your trainer-specific code here
        System.out.println("Trainer Dashboard Logic Started.");
    }
}