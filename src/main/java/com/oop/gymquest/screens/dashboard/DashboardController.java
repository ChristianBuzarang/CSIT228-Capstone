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
    // Shell Elements
    @FXML private Label headerNameLabel;
    @FXML private StackPane contentArea;

    public static DashboardController instance;

    public DashboardController() {
        instance = this;
    }

    @FXML
    public void initialize() {
        User user = MainApp.instance.currentUser;
        if (user != null) {
            // FIX: Only set text if the label exists in the current FXML
            if (headerNameLabel != null) {
                headerNameLabel.setText(user.getFirstname().toLowerCase());
            }

            // Only load the role dashboard if we are currently in the SHELL
            if (contentArea != null) {
                loadRoleDashboard(user);
            }
        }
    }

    private void loadRoleDashboard(User user) {
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
            // This prints if there's a typo in FXML or an error in the sub-FXML's controller logic
            System.err.println("Fatal: Could not load " + fxmlName);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        MainApp.instance.currentUser = null;
        MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
    }
}