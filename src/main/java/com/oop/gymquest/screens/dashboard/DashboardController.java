package com.oop.gymquest.screens.dashboard;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.userdata.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import java.io.IOException;

public class DashboardController {
    @FXML private Label headerNameLabel, roleLabel, notificationBadge;
    @FXML private StackPane contentArea, notificationContainer;
    @FXML private Button btnDashboard, btnWorkouts, btnBooking, btnManageSchedule, btnCommunity;

    private Popup notificationPopup;
    public static DashboardController instance;
    public DashboardController() {
        instance = this;
    }

    @FXML
    public void initialize() {
        User user = MainApp.instance.currentUser;
        if (user != null) {
            if (headerNameLabel != null) {
                headerNameLabel.setText(user.getFullName());
            }
            if (roleLabel != null) {
                roleLabel.setText(user.getType().toUpperCase());
            }
            boolean isTrainer = user.getType().equalsIgnoreCase("trainer");
            if (btnManageSchedule != null) {
                btnManageSchedule.setVisible(isTrainer);
                btnManageSchedule.setManaged(isTrainer);
            }
            if (btnBooking != null) {
                btnBooking.setVisible(!isTrainer);
                btnBooking.setManaged(!isTrainer);
            }
            loadRoleDashboard(user);
        }
    }

    @FXML
    private void handleToggleNotifications() {
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
            Bounds bounds = notificationContainer.localToScreen(notificationContainer.getBoundsInLocal());
            notificationPopup.show(notificationContainer, bounds.getMinX() - 300, bounds.getMaxY() + 10);
            notificationBadge.setVisible(false);
        }
    }

    public void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/" + fxml));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeBtn) {
        Button[] btns = {btnDashboard, btnWorkouts, btnBooking, btnManageSchedule, btnCommunity};
        for (Button b : btns) {
            if (b == null) continue;
            b.getStyleClass().removeAll("sidebar-btn", "sidebar-btn-active");
            b.getStyleClass().add(b == activeBtn ? "sidebar-btn-active" : "sidebar-btn");
        }
    }

    @FXML public void handleNavDashboard() { setActiveButton(btnDashboard); loadRoleDashboard(MainApp.instance.currentUser); }
    @FXML public void handleNavWorkouts() { setActiveButton(btnWorkouts); loadView("workouts.fxml"); }
    @FXML public void handleNavBooking() { setActiveButton(btnBooking); loadView("booking.fxml"); }
    @FXML public void handleNavManageSchedule() { setActiveButton(btnManageSchedule); loadView("manage_schedule.fxml"); }
    @FXML public void handleNavCommunity() { setActiveButton(btnCommunity); loadView("community.fxml"); }
    @FXML public void handleNavProfile() { setActiveButton(null); loadView("profile.fxml"); }

    private void loadRoleDashboard(User user) {
        String fxml = user.getType().equalsIgnoreCase("trainer") ? "dashboard_trainer.fxml" : "dashboard_member.fxml";
        loadView(fxml);
    }

    @FXML private void handleLogout() {
        MainApp.instance.currentUser = null;
        MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
    }
}