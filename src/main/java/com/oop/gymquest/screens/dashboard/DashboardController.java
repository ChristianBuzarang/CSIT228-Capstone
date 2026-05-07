package com.oop.gymquest.screens.dashboard;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.userdata.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class DashboardController {
    @FXML private Label headerNameLabel, roleLabel;
    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard, btnWorkouts, btnBooking, btnManageSchedule, btnSchedules, btnCommunity;

    public static DashboardController instance;

    public DashboardController() { instance = this; }

    @FXML
    public void initialize() {
        User user = MainApp.instance.currentUser;
        if (user != null) {
            headerNameLabel.setText(user.getFullName());
            roleLabel.setText(user.getType().toUpperCase());
            String type = user.getType().toLowerCase();

            // Visibility Rules
            boolean isAdmin = type.equals("admin");
            boolean isMember = type.equals("member");
            boolean isTrainer = type.equals("trainer");

            btnSchedules.setVisible(isAdmin);
            btnSchedules.setManaged(isAdmin);

            btnBooking.setVisible(isMember);
            btnBooking.setManaged(isMember);

            btnManageSchedule.setVisible(isTrainer);
            btnManageSchedule.setManaged(isTrainer);

            // Admin: No Workouts. Member: No Community.
            btnWorkouts.setVisible(!isAdmin);
            btnWorkouts.setManaged(!isAdmin);

            btnCommunity.setVisible(isTrainer); // Member requested removal
            btnCommunity.setManaged(isTrainer);

            handleNavDashboard();
        }
    }

    public void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/" + fxml));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setActive(Button b) {
        Button[] all = {btnDashboard, btnWorkouts, btnBooking, btnManageSchedule, btnSchedules, btnCommunity};
        for (Button btn : all) {
            if (btn == null) continue;
            btn.getStyleClass().removeAll("sidebar-btn-active", "sidebar-btn");
            btn.getStyleClass().add(btn == b ? "sidebar-btn-active" : "sidebar-btn");
        }
    }

    @FXML public void handleNavDashboard() { setActive(btnDashboard); loadView("dashboard_" + MainApp.instance.currentUser.getType().toLowerCase() + ".fxml"); }
    @FXML public void handleNavWorkouts() { setActive(btnWorkouts); loadView("workouts.fxml"); }
    @FXML public void handleNavBooking() { setActive(btnBooking); loadView("booking.fxml"); }
    @FXML public void handleNavManageSchedule() { setActive(btnManageSchedule); loadView("manage_schedule.fxml"); }
    @FXML public void handleNavAdminSchedules() { setActive(btnSchedules); loadView("admin_schedules.fxml"); }
    @FXML public void handleNavCommunity() { setActive(btnCommunity); loadView("community.fxml"); }
    @FXML public void handleLogout() { MainApp.instance.currentUser = null; MainApp.instance.changeScene("login.fxml", "GymQuest - Login"); }
    @FXML public void handleShowNotifications() { /* Context menu logic... */ }
}