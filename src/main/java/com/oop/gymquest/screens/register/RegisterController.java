package com.oop.gymquest.screens.register;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {
    @FXML private TextField firstnameField, lastnameField, emailField;
    @FXML private PasswordField passField;
    @FXML private ComboBox<String> typeBox;
    @FXML private Label statusLabel;

    public static RegisterController instance;

    @FXML
    public void initialize() {
        typeBox.getItems().addAll("Member", "Trainer", "Admin");
        typeBox.setValue("Member");
    }

    @FXML
    public void handleRegister() {
        String firstname = firstnameField.getText();
        String lastname = lastnameField.getText();
        String user = emailField.getText();
        String pass = passField.getText();
        String type = typeBox.getValue().toLowerCase();

        if (firstname.isEmpty() || lastname.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please fill all fields");
            return;
        }

        if (DatabaseHandler.registerUser(user, pass, firstname, lastname, type)) {
            statusLabel.setText("Account created! You can now log in.");
            statusLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;"); // Green
            MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
            clearForm();
        } else {
            statusLabel.setText("Email already exists");
            showError("Registration failed. Email might already exist.");
        }
    }

    @FXML
    private void goToLogin() {
        MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;"); // Red
    }

    private void clearForm() {
        firstnameField.clear();
        lastnameField.clear();
        emailField.clear();
        passField.clear();
    }
}