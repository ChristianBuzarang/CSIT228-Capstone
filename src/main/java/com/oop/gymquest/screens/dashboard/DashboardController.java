package com.oop.gymquest.screens.dashboard;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class DashboardController {
    @FXML private Label headerNameLabel, headerTypeLabel, avatarLabel;
    @FXML private StackPane contentArea;

    // FIX: Re-add the static instance variable
    public static DashboardController instance;

    public DashboardController() {
        instance = this; // Sets the instance when FXML is loaded
    }

    @FXML
    public void initialize() {
        // Use the user from MainApp (populated via SQL login)
        User user = MainApp.instance.currentUser;

        if (user != null) {
            headerNameLabel.setText(user.getFirstname() + " " + user.getLastname());
            headerTypeLabel.setText(user.getType().toUpperCase());

            // Check subclass type for icon
            if (user instanceof Admin) avatarLabel.setText("👨‍💼");
            else if (user instanceof Trainer) avatarLabel.setText("🏋️");
            else avatarLabel.setText("🎯");

            loadRoleDashboard(user);
        }
    }

    private void loadRoleDashboard(User user) {
        // Choose FXML based on the Subclass
        String fxmlName = switch (user.getType().toLowerCase()) {
            case "admin" -> "dashboard_admin.fxml";
            case "trainer" -> "dashboard_trainer.fxml";
            default -> "dashboard_member.fxml";
        };

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/" + fxmlName));
            Pane roleView = loader.load();
            contentArea.getChildren().setAll(roleView);
        } catch (IOException e) {
            System.err.println("Error loading role dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        MainApp.instance.currentUser = null;
        MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
    }
}