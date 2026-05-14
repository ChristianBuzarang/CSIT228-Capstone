package com.oop.gymquest.screens.notifications;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class NotificationItemController {
    @FXML private Label iconLabel, msgLabel, timeLabel;

    public void setData(String icon, String msg, String time) {
        iconLabel.setText(icon);
        msgLabel.setText(msg);
        timeLabel.setText(time);
        iconLabel.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 18px;");
        msgLabel.setStyle("-fx-text-fill: #1e293b; -fx-font-weight: bold; -fx-font-size: 13px;");
    }
}
