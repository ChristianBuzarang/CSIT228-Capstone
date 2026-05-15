package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.screens.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class TrainerDashboardController {
    @FXML private Label totalSlotsLabel, bookedSlotsLabel, clientCountLabel;
    @FXML private VBox scheduleContainer, clientContainer;

    @FXML public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        scheduleContainer.getChildren().clear();
        clientContainer.getChildren().clear();
        int tid = MainApp.instance.currentUser.getUserId();
        int total = 0;
        int booked = 0;
        Set<String> clients = new HashSet<>();

        try (ResultSet rs = DatabaseHandler.getTodaySessions(tid)) {
            while (rs != null && rs.next()) {
                total++;
                String status = rs.getString("status");
                String client = rs.getString("booked_by_name");
                if ("Booked".equalsIgnoreCase(status)) {
                    booked++;
                    if (client != null) clients.add(client);
                }
                addScheduleItem(rs.getString("activity"), rs.getString("slot_time"), client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String c : clients) addClientCard(c);
        totalSlotsLabel.setText(String.valueOf(total));
        bookedSlotsLabel.setText(String.valueOf(booked));
        clientCountLabel.setText(String.valueOf(DatabaseHandler.getUniqueClientCount(tid)));
    }

    private void addScheduleItem(String activity, String time, String client) {
        VBox box = new VBox(10);
        box.getStyleClass().add("card");
        box.setStyle("-fx-border-color: #bae6fd; -fx-border-width: 1.5;");

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
        icon.setFitHeight(20);
        icon.setFitWidth(20);

        VBox texts = new VBox(2);
        Label actLabel = new Label(activity);
        actLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        texts.getChildren().addAll(actLabel, new Label(time));

        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);

        Label badge = new Label(client != null ? "BOOKED" : "AVAILABLE");
        badge.setStyle("-fx-background-color: " + (client != null ? "#3b82f6" : "#94a3b8") + "; -fx-text-fill: white; -fx-padding: 5 12; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 10;");

        row.getChildren().addAll(icon, texts, s, badge);
        box.getChildren().add(row);
        if (client != null) box.getChildren().add(new Label("by " + client));
        scheduleContainer.getChildren().add(box);
    }

    private void addClientCard(String name) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        Label l = new Label(name);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

        HBox profile = new HBox(10);
        profile.setAlignment(Pos.CENTER_LEFT);
        ImageView clientIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/user.png")));
        clientIcon.setFitHeight(24);
        clientIcon.setFitWidth(24);
        profile.getChildren().addAll(clientIcon, l);

        ProgressBar p = new ProgressBar(0.75);
        p.setMaxWidth(Double.MAX_VALUE);
        card.getChildren().addAll(profile, p);
        clientContainer.getChildren().add(card);
    }

    @FXML private void handleSeeAllSchedules() {
        DashboardController.instance.loadView("trainer_sessions_list.fxml");
    }

    @FXML private void handleSeeAllClients() {
        DashboardController.instance.loadView("trainer_clients_list.fxml");
    }

    @FXML private void handleSeeAll() {
        DashboardController.instance.loadView("trainer_sessions_list.fxml");
    }
}