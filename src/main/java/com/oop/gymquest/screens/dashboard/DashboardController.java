package com.oop.gymquest.screens.dashboard;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.userdata.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;

import java.io.IOException;
import java.sql.ResultSet;

public class DashboardController {
    @FXML private Label headerNameLabel, roleLabel, notifBadge;
    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard, btnWorkouts, btnBooking, btnManageSchedule, btnSchedules, btnCommunity;
    @FXML private Button notificationBell;
    @FXML private ImageView headerAvatarView;
    @FXML private ImageView bellIcon;

    private Popup notificationPopup;
    private boolean notificationsRead = false;

    public static DashboardController instance;
    public DashboardController() { instance = this; }

    @FXML public void initialize() {
        User user = MainApp.instance.currentUser;
        if (user != null) {
            headerNameLabel.setText(user.getFullName());
            roleLabel.setText(user.getType().toUpperCase());
            String type = user.getType().toLowerCase();
            boolean isAdmin = type.equals("admin");
            boolean isMember = type.equals("member");
            boolean isTrainer = type.equals("trainer");

            btnSchedules.setVisible(isAdmin);
            btnSchedules.setManaged(isAdmin);

            btnBooking.setVisible(isMember);
            btnBooking.setManaged(isMember);

            btnManageSchedule.setVisible(isTrainer);
            btnManageSchedule.setManaged(isTrainer);

            btnWorkouts.setVisible(!isAdmin);
            btnWorkouts.setManaged(!isAdmin);
            btnCommunity.setVisible(!isAdmin);
            btnCommunity.setManaged(!isAdmin);

            handleNavDashboard();
            refreshHeader();
            updateNotificationBadge();
        }
    }

    public void loadView(String fxml) {
        try {
            String path = "/com/oop/gymquest/fxml/" + fxml;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            if (loader.getLocation() == null) {
                System.err.println("❌ FXML not found at: " + path);
                return;
            }
            Node view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActive(Button b) {
        Button[] all = {btnDashboard, btnWorkouts, btnBooking, btnManageSchedule, btnSchedules, btnCommunity};
        for (Button btn : all) {
            if (btn == null) continue;
            btn.getStyleClass().removeAll("sidebar-btn-active");
            if (!btn.getStyleClass().contains("sidebar-btn")) btn.getStyleClass().add("sidebar-btn");
            if (btn == b) {
                btn.getStyleClass().remove("sidebar-btn");
                btn.getStyleClass().add("sidebar-btn-active");
            }
        }
    }

    public void refreshHeader() {
        User user = MainApp.instance.currentUser;
        if (user != null && user.getAvatar() != null) {
            try {
                String path = "/com/oop/gymquest/images/" + user.getAvatar();
                Image img = new Image(getClass().getResourceAsStream(path));
                headerAvatarView.setFitHeight(32);
                headerAvatarView.setFitWidth(32);
                headerAvatarView.setImage(img);
                Circle clip = new Circle(16, 16, 16);
                headerAvatarView.setClip(clip);
            } catch (Exception e) {
                System.err.println("Header avatar not found: " + user.getAvatar());
            }
        }
        if (headerNameLabel != null && MainApp.instance.currentUser != null) {
            headerNameLabel.setText(MainApp.instance.currentUser.getFullName());
        }
    }

    public void updateNotificationBadge() {
        if (notifBadge == null) return;

        if (notificationsRead) {
            resetNotificationBell();
            return;
        }

        int count = 0;
        User user = MainApp.instance.currentUser;
        if (user != null) {
            try (ResultSet rs = DatabaseHandler.getMemberSessionsToday(user.getUserId())) {
                while (rs != null && rs.next()) {
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (count > 0) {
            notifBadge.setText(String.valueOf(count));
            notifBadge.setVisible(true);
            notifBadge.setManaged(true);
            if (bellIcon != null) {
                bellIcon.setImage(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/notif_alarm.png")));
            }
        } else {
            resetNotificationBell();
        }
    }

    public void resetNotificationBell() {
        notificationsRead = true;
        if (notifBadge != null) {
            notifBadge.setText("");
            notifBadge.setVisible(false);
            notifBadge.setManaged(false);
        }
        if (bellIcon != null) {
            try {
                bellIcon.setImage(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/notif_orig.png")));
            } catch (Exception e) {
                System.err.println("Could not load original bell image.");
            }
        }
    }

    @FXML public void handleNavDashboard() {
        setActive(btnDashboard);
        loadView("dashboard_" + MainApp.instance.currentUser.getType().toLowerCase() + ".fxml");
    }

    @FXML public void handleNavWorkouts() {
        setActive(btnWorkouts);
        loadView("workouts.fxml");
    }

    @FXML public void handleNavBooking() {
        setActive(btnBooking);
        loadView("booking.fxml");
    }

    @FXML public void handleNavManageSchedule() {
        setActive(btnManageSchedule);
        loadView("manage_schedule.fxml");
    }

    @FXML public void handleNavAdminSchedules() {
        setActive(btnSchedules);
        loadView("admin_schedules.fxml");
    }

    @FXML public void handleNavCommunity() {
        setActive(btnCommunity);
        loadView("community.fxml");
    }

    @FXML public void handleNavProfile() {
        setActive(null);
        loadView("profile.fxml");
    }

    @FXML public void handleLogout() {
        MainApp.instance.currentUser = null;
        MainApp.instance.changeScene("login.fxml", "GymQuest - Login");
    }

    @FXML public void handleShowNotifications(ActionEvent event) {
        if (notificationPopup == null) {
            notificationPopup = new Popup();
            notificationPopup.setAutoHide(true);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/notification.fxml"));
                Parent root = loader.load();
                notificationPopup.getContent().add(root);
            } catch (IOException e) {
                System.err.println("❌ Could not load notification.fxml");
                e.printStackTrace();
            }
        }
        if (notificationPopup.isShowing()) {
            notificationPopup.hide();
        } else {
            double windowX = notificationBell.getScene().getWindow().getX();
            double windowY = notificationBell.getScene().getWindow().getY();
            double buttonX = notificationBell.localToScene(0, 0).getX();
            double buttonY = notificationBell.localToScene(0, 0).getY();
            notificationPopup.show(notificationBell,
                    windowX + buttonX - 300,
                    windowY + buttonY + 45
            );
        }
    }
}