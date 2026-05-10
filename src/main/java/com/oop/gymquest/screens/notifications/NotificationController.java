package com.oop.gymquest.screens.notifications;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;

public class NotificationController {
    @FXML private VBox notificationList;

    @FXML
    public void initialize() {
        if (notificationList != null) {
            notificationList.getChildren().clear();
            addNotification("🏆", "You reached a 30-day streak!", "2h ago");
            addNotification("📅", "Training session tomorrow at 10am", "5h ago");
        } else {
            System.err.println("❌ Error: fx:id 'notificationList' not found in FXML!");
        }
    }

    private void addNotification(String icon, String msg, String time) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/notification_item.fxml"));
            Node item = loader.load();
            NotificationItemController controller = loader.getController();
            if (controller != null) {
                controller.setData(icon, msg, time);
                notificationList.getChildren().add(item);
            } else {
                System.err.println("❌ Error: NotificationItemController not found in FXML!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}