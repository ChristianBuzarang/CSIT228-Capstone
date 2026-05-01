package com.oop.gymquest.screens.dashboard;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class DashboardController {
    @FXML private Label headerNameLabel, headerTypeLabel, avatarLabel;
    @FXML private StackPane contentArea;

    public static DashboardController instance;

    @FXML
    public void initialize() {
        User user = MainApp.instance.currentUser;
        if (user != null) {
            headerNameLabel.setText(user.getFirstname() + " " + user.getLastname());
            headerTypeLabel.setText(user.getType());

            // Set Avatar based on the role
            if(user.getType().equals("admin")) avatarLabel.setText("👨‍💼");
            else if(user.getType().equals("trainer")) avatarLabel.setText("🏋️");
            else avatarLabel.setText("🎯");

            loadRoleDashboard(user.getType());
        }
    }

    private void loadRoleDashboard(String type) {
        String fxmlPath = switch (type.toLowerCase()) {
            case "admin" -> "/com/oop/gymquest/dashboard_admin.fxml";
            case "trainer" -> "/com/oop/gymquest/dashboard_trainer.fxml";
            default -> "/com/oop/gymquest/dashboard_member.fxml";
        };

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane roleView = loader.load();
            contentArea.getChildren().setAll(roleView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        MainApp.instance.currentUser = null;
        // Delete the session file for security
        new java.io.File("session.ser").delete();
        MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
    }
}