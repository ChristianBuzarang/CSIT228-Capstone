package com.oop.gymquest;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class SessionsViewController {

    @FXML
    public void initialize() {
        System.out.println("Classes Catalog Initialized");
    }

    @FXML
    private void handleEnroll() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Enrollment");
        alert.setHeaderText("Confirm Class Enrollment");
        alert.setContentText("Would you like to enroll in this program? This will use 1 Class Credit.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Member enrolled successfully!");
            }
        });
    }
}