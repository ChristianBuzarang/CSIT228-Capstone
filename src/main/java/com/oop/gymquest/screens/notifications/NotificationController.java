package com.oop.gymquest.screens.notifications;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.userdata.User;
import com.oop.gymquest.screens.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationController {
    @FXML private VBox notificationList;

    @FXML public void initialize() { loadNotifications(); }

    private void loadNotifications() {
        notificationList.getChildren().clear();
        User currentUser = MainApp.instance.currentUser;
        if (currentUser == null) return;

        try (ResultSet rs = DatabaseHandler.getMemberSessionsToday(currentUser.getUserId())) {
            boolean found = false;
            while (rs != null && rs.next()) {
                found = true;
                String coach = rs.getString("firstname") + " " + rs.getString("lastname");
                String activity = rs.getString("activity");
                String date = rs.getString("slot_date");

                String time = rs.getString("formatted_time") != null
                        ? rs.getString("formatted_time")
                        : rs.getString("slot_time");

                String message = "Confirmed: " + activity + " with " + coach;
                String timestamp = date + " at " + time;
                addNotificationItem("📅", message, timestamp);
            }
            if (!found) {
                addNotificationItem("👋", "Welcome to GymQuest! Start your journey by booking a session.", "Just now");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleMarkAllAsRead() {
        notificationList.getChildren().clear();

        addNotificationItem("✅", "You're all caught up! No new notifications.", "Just now");

        if (DashboardController.instance != null) DashboardController.instance.resetNotificationBell();
    }

    private void addNotificationItem(String icon, String msg, String time) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/notification_item.fxml"));
            Node node = loader.load();
            NotificationItemController itemController = loader.getController();
            if (itemController != null) {
                itemController.setData(icon, msg, time);
                notificationList.getChildren().add(node);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}