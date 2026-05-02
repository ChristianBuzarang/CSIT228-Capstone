package com.oop.gymquest.screens.login;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private Label statusLabel;

    // Password Toggle Fields
    @FXML private PasswordField passwordField;
    @FXML private TextField eyeFieldVisible;
    @FXML private ImageView toggleIcon;

    private boolean isPasswordVisible = false;

    // Load icons from resources
    private final Image SEE_IMG = new Image(getClass().getResourceAsStream("/com/oop/gymquest/see-password.png"));
    private final Image NOT_SEE_IMG = new Image(getClass().getResourceAsStream("/com/oop/gymquest/not-see-password.png"));

    /**
     * Toggles between hidden PasswordField and visible TextField.
     * Updates the toggleIcon image.
     */
    @FXML
    private void togglePassword() {
        if (isPasswordVisible) {
            // Switch back to hidden
            passwordField.setText(eyeFieldVisible.getText());
            passwordField.setVisible(true);
            eyeFieldVisible.setVisible(false);
            toggleIcon.setImage(SEE_IMG);
        } else {
            // Switch to visible
            eyeFieldVisible.setText(passwordField.getText());
            eyeFieldVisible.setVisible(true);
            passwordField.setVisible(false);
            toggleIcon.setImage(NOT_SEE_IMG);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void handleLogin() {
        String username = emailField.getText();

        // Get the password from whichever field is currently active/visible
        String password = isPasswordVisible ? eyeFieldVisible.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Authenticate via SQL
        User user = DatabaseHandler.authenticate(username, password);

        if (user != null) {
            MainApp.instance.currentUser = user;
            MainApp.instance.saveSession(user);

            // Success: Proceed to dashboard
            MainApp.instance.changeScene("dashboard_shell.fxml", "GymQuest - Dashboard");
        } else {
            showError("Invalid Username or Password");
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
    }

    @FXML
    private void handleGoToRegister() {
        MainApp.instance.changeScene("register.fxml", "GymQuest - Create Account");
    }
}