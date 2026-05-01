package com.oop.gymquest.screens.login;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML
    private TextField uField;
    @FXML private PasswordField pField;
    @FXML private Label statusLabel;

    @FXML
    private void handleLogin() {
        User user = DatabaseHandler.authenticate(uField.getText(), pField.getText());
        if (user != null) {
            MainApp.instance.currentUser = user;
            MainApp.instance.saveSession(user); // Serialize
            MainApp.instance.changeScene("dashboard_shell.fxml", "GymQuest - Dashboard");
        } else {
            statusLabel.setText("Invalid Username or Password");
        }
    }

    @FXML
    private void handleGoToRegister() {
        MainApp.instance.changeScene("register.fxml", "GymQuest - Create Account");
    }
}
