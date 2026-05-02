package com.oop.gymquest.screens.dashboard;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.userdata.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;

import java.io.IOException;

public class DashboardController {

    @FXML private Label headerNameLabel;
    @FXML private Label headerTypeLabel;
    @FXML private StackPane contentArea;
    @FXML private Button notificationBell;
    private Popup notificationPopup;

    public static DashboardController instance;

    public DashboardController() {
        instance = this;
    }

    @FXML
    public void initialize() {
        User user = MainApp.instance.currentUser;

        if (user != null) {
            // Check if labels were successfully injected from FXML before using them
            if (headerNameLabel != null) headerNameLabel.setText(user.getFullName());
            if (headerTypeLabel != null) headerTypeLabel.setText(user.getType().toUpperCase());

            loadRoleDashboard(user);
        }
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/" + fxml));
            Pane newView = loader.load();

            // This clears the current center and puts the new FXML there
            contentArea.getChildren().setAll(newView);

        } catch (IOException e) {
            System.err.println("❌ Could not load view: " + fxml);
            e.printStackTrace();
        }
    }

    private void loadRoleDashboard(User user) {
        String fxmlName = switch (user.getType().toLowerCase()) {
            case "admin" -> "dashboard_admin.fxml";
            case "trainer" -> "dashboard_trainer.fxml";
            default -> "dashboard_member.fxml";
        };

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/" + fxmlName));
            Pane roleView = loader.load();
            contentArea.getChildren().setAll(roleView);
        } catch (IOException e) {
            System.err.println("Error loading role dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleNavDashboard() {
        loadRoleDashboard(MainApp.instance.currentUser);
    }

    @FXML
    private void handleNavWorkouts() {
        loadView("workouts.fxml");
    }

    @FXML
    private void handleNavBooking() {
        loadView("booking.fxml");
    }

    @FXML
    private void handleNavCommunity() {
        loadView("community.fxml");
    }

    @FXML
    private void handleNavProfile() {
        loadView("profile.fxml");
    }

    @FXML
    private void handleLogout() {
        MainApp.instance.currentUser = null;
        MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
    }

    @FXML
    private void handleShowNotifications(ActionEvent event) {
        if (notificationPopup == null) {
            notificationPopup = new Popup();
            notificationPopup.setAutoHide(true);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/notification.fxml"));
                Parent root = loader.load();
                notificationPopup.getContent().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (notificationPopup.isShowing()) {
            notificationPopup.hide();
        } else {
            notificationPopup.show(notificationBell,
                    notificationBell.getScene().getWindow().getX() + notificationBell.localToScene(0, 0).getX() - 300,
                    notificationBell.getScene().getWindow().getY() + notificationBell.localToScene(0, 0).getY() + 50
            );
        }
    }


}