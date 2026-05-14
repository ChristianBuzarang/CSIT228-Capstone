package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MemberDashboardController {
    @FXML private VBox sessionsTodayContainer;

    @FXML public void initialize() {
        loadTodaySessions();
    }

    public void loadTodaySessions() {
        sessionsTodayContainer.getChildren().clear();
        int memberId = MainApp.instance.currentUser.getUserId();

        try (ResultSet rs = DatabaseHandler.getMemberSessionsToday(memberId)) {
            boolean hasSessions = false;
            while (rs != null && rs.next()) {
                hasSessions = true;
                String trainer = rs.getString("firstname") + " " + rs.getString("lastname");
                String activity = rs.getString("activity");
                String timeStr = rs.getString("slot_time"); // Format "01:00 PM"

                sessionsTodayContainer.getChildren().add(createSessionCard(trainer, activity, timeStr));
            }

            if (!hasSessions) {
                Label placeholder = new Label("No sessions scheduled for today.");
                placeholder.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 20;");
                sessionsTodayContainer.getChildren().add(placeholder);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private VBox createSessionCard(String trainer, String activity, String time) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        java.time.format.DateTimeFormatter parser = java.time.format.DateTimeFormatter
                .ofPattern("h:mm a", java.util.Locale.ENGLISH);
        LocalTime sessionTime;
        try {
            sessionTime = LocalTime.parse(time, parser);
        } catch (Exception e) {
            System.err.println("❌ Could not parse time: " + time + ". Defaulting to now.");
            sessionTime = LocalTime.now();
        }
        LocalTime now = LocalTime.now();

        String statusText;
        String statusStyle;
        if (now.isBefore(sessionTime)) {
            statusText = "UPCOMING";
            statusStyle = "-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6;";
        } else if (now.isAfter(sessionTime) && now.isBefore(sessionTime.plusHours(1))) {
            statusText = "ONGOING";
            statusStyle = "-fx-background-color: #fff7ed; -fx-text-fill: #f97316;";
        } else {
            statusText = "FINISHED";
            statusStyle = "-fx-background-color: #f0fdf4; -fx-text-fill: #10b981;";
        }

        VBox info = new VBox(2);
        Label title = new Label(activity + " with " + trainer);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #1e293b;");
        Label timeLbl = new Label("⏱ " + time);
        timeLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");
        info.getChildren().addAll(title, timeLbl);

        Label badge = new Label(statusText);
        badge.setStyle(statusStyle + "-fx-padding: 4 10; -fx-background-radius: 10; -fx-font-size: 10; -fx-font-weight: bold;");

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        row.getChildren().addAll(info, spacer, badge);
        card.getChildren().add(row);
        return card;
    }

    private void addSessionCard(int slotId, String act, String coach, String date, String time) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("card");
        card.setStyle("-fx-padding: 15; -fx-border-color: #eff6ff; -fx-border-width: 1;");

        Image img = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png"));
        ImageView icon = new ImageView(img);
        icon.setFitHeight(30);
        icon.setFitWidth(30);

        VBox info = new VBox(2);
        Label title = new Label(act + " with " + coach);
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        Label dateTime = new Label(date + " at " + time);
        dateTime.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");
        info.getChildren().add(title);
        info.getChildren().add(dateTime);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #ef4444; -fx-border-color: #fee2e2; -fx-border-radius: 8; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> {
            if (DatabaseHandler.cancelBooking(slotId)) {
                loadTodaySessions();
            }
        });

        card.getChildren().add(icon);
        card.getChildren().add(info);
        card.getChildren().add(spacer);
        card.getChildren().add(cancelBtn);
        sessionsTodayContainer.getChildren().add(card);
    }
}