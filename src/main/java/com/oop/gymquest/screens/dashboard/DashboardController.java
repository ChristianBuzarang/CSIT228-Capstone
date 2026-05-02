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

    @FXML private Label headerNameLabel, headerTypeLabel;
    @FXML private StackPane contentArea;
    @FXML private Button notificationBell;
    @FXML private Button btnDashboard, btnWorkouts, btnBooking, btnCommunity;

    private Popup notificationPopup;
    public static DashboardController instance;

    private final String STYLE_ACTIVE = "-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-alignment: CENTER_LEFT; -fx-padding: 15; -fx-background-radius: 15; -fx-font-weight: bold; -fx-cursor: hand;";
    private final String STYLE_INACTIVE = "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-alignment: CENTER_LEFT; -fx-padding: 15; -fx-font-weight: normal; -fx-cursor: hand;";

    public DashboardController() {
        instance = this;
    }

    // Inside DashboardController.java initialize()
    @FXML
    public void initialize() {
        User user = MainApp.instance.currentUser;
        if (user != null) {
            headerNameLabel.setText(user.getFullName());
            headerTypeLabel.setText(user.getType().toUpperCase());

            // CHANGE: If Trainer, rename the booking button to Manage Schedule
            if (user.getType().equalsIgnoreCase("trainer")) {
                btnBooking.setText("  Manage Schedule");
            }

            loadRoleDashboard(user);
        }
    }

    private void setActiveButton(Button activeBtn) {
        btnDashboard.setStyle(STYLE_INACTIVE);
        btnWorkouts.setStyle(STYLE_INACTIVE);
        btnBooking.setStyle(STYLE_INACTIVE);
        btnCommunity.setStyle(STYLE_INACTIVE);
        if (activeBtn != null) {
            activeBtn.setStyle(STYLE_ACTIVE);
        }
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/" + fxml));
            Pane newView = loader.load();
            contentArea.getChildren().setAll(newView);
        } catch (IOException e) {
            System.err.println("Could not load view: " + fxml);
            e.printStackTrace();
        }
    }

    private void loadRoleDashboard(User user) {
        String fxml = switch (user.getType().toLowerCase()) {
            case "admin" -> "dashboard_admin.fxml";
            case "trainer" -> "dashboard_trainer.fxml";
            default -> "dashboard_member.fxml";
        };
        loadView(fxml);
    }

    @FXML
    private void handleNavDashboard() {
        setActiveButton(btnDashboard);
        loadRoleDashboard(MainApp.instance.currentUser);
    }

    @FXML
    private void handleNavWorkouts() {
        setActiveButton(btnWorkouts);
        loadView("workouts.fxml");
    }

    @FXML
    private void handleNavBooking() {
        setActiveButton(btnBooking);
        loadView("booking.fxml");
    }

    @FXML
    private void handleNavCommunity() {
        setActiveButton(btnCommunity);
        loadView("community.fxml");
    }

    @FXML
    private void handleNavProfile() {
        setActiveButton(null);
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