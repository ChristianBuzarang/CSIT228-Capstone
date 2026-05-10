package com.oop.gymquest.screens.notifications;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class NotificationItemController {
    @FXML private Label iconLabel, msgLabel, timeLabel;

    public void setData(String icon, String msg, String time) {
        iconLabel.setText(icon);
        msgLabel.setText(msg);
        timeLabel.setText(time);
    }
}
