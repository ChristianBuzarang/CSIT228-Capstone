package com.oop.gymquest.screens.workouts;

import com.oop.gymquest.app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class WorkoutsViewController {

    @FXML
    public void initialize() {
        // Here you would eventually load your exercises from a database
        System.out.println("Workout Library Initialized");
    }

    @FXML
    private void handleViewGuide() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exercise Guide");
        alert.setHeaderText("Barbell Squat Instructions");
        alert.setContentText("1. Stand with feet shoulder-width apart.\n2. Lower hips back.\n3. Keep chest up.\n4. Return to starting position.");
        alert.showAndWait();
    }

    @FXML
    public static void handleAction() {
        MainApp.instance.changeScene("/com/oop/gymquest/fxml/workouts.fxml", "GymQuest - Workout Library");
    }
}