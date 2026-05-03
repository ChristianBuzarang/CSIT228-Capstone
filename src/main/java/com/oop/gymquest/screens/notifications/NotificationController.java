package com.oop.gymquest.screens.notifications;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;

public class NotificationController {
    @FXML private VBox notificationList;

    @FXML
    public void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {
        notificationList.getChildren().clear();
        // Sample data - normally fetched from a Database Handler
        addNotificationItem("Session Confirmed", "Coach Alex accepted your booking for May 8.", "calendar.png");
        addNotificationItem("New Workout", "A new 'Full Body' routine is available.", "muscle.png");
    }

    private void addNotificationItem(String title, String message, String iconName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/notification_item.fxml"));
            Node item = loader.load();

            // Get children of the HBox item (adjust based on your item FXML)
            // This assumes a simple VBox inside an HBox structure
            notificationList.getChildren().add(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void markAllAsRead() {
        notificationList.getChildren().clear();
        Label empty = new Label("No new notifications");
        empty.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 20;");
        notificationList.getChildren().add(empty);
    }
}