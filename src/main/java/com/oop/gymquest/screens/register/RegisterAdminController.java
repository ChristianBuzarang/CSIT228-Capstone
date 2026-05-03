package com.oop.gymquest.screens.register;

import com.oop.gymquest.data.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterAdminController {
    @FXML private TextField firstnameField, lastnameField, emailField;
    @FXML private PasswordField passField;
    @FXML private Label statusLabel;

    @FXML
    private void handleCreateAdmin() {
        String fname = firstnameField.getText().trim();
        String lname = lastnameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passField.getText();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showMessage("Please fill all fields", "#ef4444");
            return;
        }

        boolean success = UserDAO.create(email, pass, fname, lname, "admin");

        if (success) {
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.close();
        } else {
            showMessage("Account creation failed. Email might exist.", "#ef4444");
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    private void showMessage(String text, String color) {
        statusLabel.setText(text);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }
}