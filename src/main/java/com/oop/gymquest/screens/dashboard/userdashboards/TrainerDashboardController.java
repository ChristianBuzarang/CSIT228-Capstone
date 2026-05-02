package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class TrainerDashboardController {
    @FXML private Label totalSlotsLabel, bookedSlotsLabel, availableSlotsLabel, clientCountLabel;
    @FXML private VBox scheduleContainer;

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        scheduleContainer.getChildren().clear();

        // FIX: Changed getUserid() to getUserId()
        int tid = MainApp.instance.currentUser.getUserId();

        int total = 0, booked = 0, available = 0;
        Set<String> uniqueClients = new HashSet<>();

        try (ResultSet rs = DatabaseHandler.getTrainerSchedule(tid)) {
            while (rs != null && rs.next()) {
                total++;
                String status = rs.getString("status");
                String client = rs.getString("booked_by_name");

                if (status.equalsIgnoreCase("Booked")) {
                    booked++;
                    if (client != null) uniqueClients.add(client);
                } else {
                    available++;
                }

                addScheduleCard(
                        rs.getString("activity"),
                        rs.getString("slot_date"),
                        rs.getString("slot_time"),
                        rs.getString("duration"),
                        status,
                        client
                );
            }
        } catch (Exception e) { e.printStackTrace(); }

        totalSlotsLabel.setText(String.valueOf(total));
        bookedSlotsLabel.setText(String.valueOf(booked));
        availableSlotsLabel.setText(String.valueOf(available));
        clientCountLabel.setText(String.valueOf(uniqueClients.size()));
    }

    private void addScheduleCard(String activity, String date, String time, String duration, String status, String client) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("card");

        boolean isBooked = status.equalsIgnoreCase("Booked");
        card.setStyle("-fx-border-color: " + (isBooked ? "#f59e0b" : "#3b82f6") + "; -fx-border-width: 1.5; -fx-background-color: " + (isBooked ? "#fffbeb" : "white") + ";");

        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: " + (isBooked ? "#f59e0b" : "#3b82f6") + "; -fx-padding: 10; -fx-background-radius: 12;");

        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
        iv.setFitHeight(20); iv.setFitWidth(20);
        iconBox.getChildren().add(iv);

        VBox info = new VBox(4);
        Label title = new Label(activity);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-text-fill: #1e3a5f;");
        Label sub = new Label(date + " • " + time + " • " + duration);
        sub.getStyleClass().add("subtitle-gray");
        info.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox statusBox = new VBox(5);
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        Label badge = new Label(status.toUpperCase());
        badge.setStyle("-fx-background-color: " + (isBooked ? "#f59e0b" : "#10b981") + "; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 10; -fx-font-size: 10; -fx-font-weight: bold;");
        Label clientLbl = new Label(isBooked ? "by " + client : "");
        clientLbl.getStyleClass().add("subtitle-gray");
        statusBox.getChildren().addAll(badge, clientLbl);

        card.getChildren().addAll(iconBox, info, spacer, statusBox);
        scheduleContainer.getChildren().add(card);
    }

    @FXML
    private void handleAddSlot() {
        // FIX: Changed getUserid() to getUserId()
        int tid = MainApp.instance.currentUser.getUserId();

        DatabaseHandler.addTrainerSlot(tid, "Strength Training", LocalDate.now().toString(), "10:00 AM", "60 min");
        loadDashboardData();
    }
}