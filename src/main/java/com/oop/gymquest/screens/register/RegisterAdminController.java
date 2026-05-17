package com.oop.gymquest.screens.register;

import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterAdminController {
    @FXML private TextField firstnameField, lastnameField, emailField;
    @FXML private PasswordField passField;
    @FXML private Label statusLabel;

    @FXML private void handleCreateAdmin() {
        String fname = firstnameField.getText().trim();
        String lname = lastnameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passField.getText();
        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please fill all fields");
            statusLabel.setStyle("-fx-text-fill: #ffd1d1;");
            return;
        }
        boolean success = DatabaseHandler.registerUser(email, pass, fname, lname, "admin");
        if (success) {
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.close();
        } else {
            statusLabel.setText("Creation failed. Email may exist.");
            statusLabel.setStyle("-fx-text-fill: #ffd1d1;");
        }
    }

    @FXML private void handleCancel() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}