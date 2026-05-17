package com.oop.gymquest.screens.login;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.userdata.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private Label statusLabel;
    @FXML private PasswordField passwordField;
    @FXML private TextField eyeFieldVisible;
    @FXML private ImageView toggleIcon;

    private boolean isPasswordVisible = false;

    private final Image SEE_IMG = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/see-password.png"));
    private final Image NOT_SEE_IMG = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/not-see-password.png"));

    @FXML private void togglePassword() {
        if (isPasswordVisible) {
            passwordField.setText(eyeFieldVisible.getText());
            passwordField.setVisible(true);
            eyeFieldVisible.setVisible(false);
            toggleIcon.setImage(SEE_IMG);
        } else {
            eyeFieldVisible.setText(passwordField.getText());
            eyeFieldVisible.setVisible(true);
            passwordField.setVisible(false);
            toggleIcon.setImage(NOT_SEE_IMG);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML private void handleLogin() {
        String username = emailField.getText();
        String password = isPasswordVisible ? eyeFieldVisible.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        User user = DatabaseHandler.authenticate(username, password);
        if (user != null) {
            MainApp.instance.currentUser = user;
            MainApp.instance.saveSession(user);
            MainApp.instance.changeScene("dashboard_shell.fxml", "GymQuest - Dashboard");
        } else {
            showError("Invalid Username or Password");
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
    }

    @FXML private void handleGoToRegister() {
        MainApp.instance.changeScene("register.fxml", "GymQuest - Create Account");
    }
}