package com.oop.gymquest.screens.dashboard;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.userdata.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class DashboardController {
    // These IDs MUST match the fx:id in dashboard_shell.fxml
    @FXML private Label headerNameLabel;
    @FXML private Label headerTypeLabel;
    @FXML private StackPane contentArea;

    public static DashboardController instance;

    public DashboardController() {
        instance = this;
    }

    @FXML
    public void initialize() {
        User user = MainApp.instance.currentUser;

        if (user != null) {
            // Check if labels were successfully injected from FXML before using them
            if (headerNameLabel != null) headerNameLabel.setText(user.getFirstname().toLowerCase());
            if (headerTypeLabel != null) headerTypeLabel.setText(user.getType().toUpperCase());

            loadRoleDashboard(user);
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
            System.err.println("Error loading role dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        MainApp.instance.currentUser = null;
        MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
    }
}