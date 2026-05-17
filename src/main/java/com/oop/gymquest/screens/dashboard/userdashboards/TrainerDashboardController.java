package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.screens.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.sql.ResultSet;
import java.util.Map;

public class TrainerDashboardController {
    @FXML private Label totalSlotsLabel, clientCountLabel;
    @FXML private VBox scheduleContainer, clientContainer;

    @FXML public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        scheduleContainer.getChildren().clear();
        clientContainer.getChildren().clear();

        int tid = MainApp.instance.currentUser.getUserId();
        Map<String, String> dashboardClients = new java.util.LinkedHashMap<>();

        totalSlotsLabel.setText(String.valueOf(DatabaseHandler.getTrainerTotalSlotsCount(tid)));
        clientCountLabel.setText(String.valueOf(DatabaseHandler.getUniqueClientCount(tid)));

        int sessionsAdded = 0;
        try (ResultSet rs = DatabaseHandler.getTodaySessions(tid)) {
            while (rs != null && rs.next()) {
                String clientName = rs.getString("booked_by_name");
                String clientAvatar = rs.getString("avatar");

                addScheduleItem(rs.getString("activity"), rs.getString("slot_time"), clientName);
                sessionsAdded++;

                if (clientName != null) {
                    dashboardClients.put(clientName, clientAvatar);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        if (dashboardClients.isEmpty()) {
            try (ResultSet rs = DatabaseHandler.getMemberBookingsForTrainer(tid)) {
                int count = 0;
                while (rs != null && rs.next() && count < 3) {
                    addClientCard(rs.getString("firstname") + " " + rs.getString("lastname"), rs.getString("avatar"));
                    count++;
                }
                if (count == 0) showEmptyPlaceholder(clientContainer, "No Clients Found", "No bookings found in your history.");
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            dashboardClients.forEach((name, avatar) -> addClientCard(name, avatar));
        }

        if (sessionsAdded == 0) {
            showEmptyPlaceholder(scheduleContainer, "No Sessions Found", "You haven't created any schedules yet.");
        }
    }

    private void showEmptyPlaceholder(VBox container, String title, String subtitle) {
        VBox ph = new VBox(12);
        ph.setAlignment(Pos.CENTER);
        ph.setPadding(new Insets(30, 0, 30, 0));
        ph.setOpacity(0.35);

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #1e3a5f;");
        Label s = new Label(subtitle);
        s.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");

        ph.getChildren().addAll(t, s);
        container.getChildren().add(ph);
    }

    private void addScheduleItem(String activity, String time, String client) {
        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: white; -fx-padding: 18; -fx-background-radius: 15; " +
                "-fx-border-color: #f1f5f9; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 10, 0, 0, 4);");

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-padding: 10;");
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
            icon.setFitHeight(18); icon.setFitWidth(18);
            iconBox.getChildren().add(icon);
        } catch (Exception e) { iconBox.getChildren().add(new Label("📅")); }

        VBox texts = new VBox(2);
        Label actLabel = new Label(activity);
        actLabel.setStyle("-fx-font-weight: 800; -fx-text-fill: #1e293b; -fx-font-size: 14;");
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");
        texts.getChildren().addAll(actLabel, timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean isBooked = (client != null);
        Label badge = new Label(isBooked ? "BOOKED" : "AVAILABLE");
        badge.setStyle("-fx-background-color: " + (isBooked ? "#dbeafe" : "#f1f5f9") + "; " +
                "-fx-text-fill: " + (isBooked ? "#2563eb" : "#64748b") + "; " +
                "-fx-padding: 5 12; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 10;");

        row.getChildren().addAll(iconBox, texts, spacer, badge);
        box.getChildren().add(row);

        if (isBooked) {
            Label clientDetail = new Label("by " + client);
            clientDetail.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11; -fx-padding: 0 0 0 45;");
            box.getChildren().add(clientDetail);
        }

        scheduleContainer.getChildren().add(box);
    }

    private void addClientCard(String name, String avatar) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 18; " +
                "-fx-border-color: #f1f5f9; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 10, 0, 0, 4);");

        HBox profile = new HBox(12);
        profile.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarFrame = new StackPane();
        avatarFrame.setPrefSize(40, 40);

        try {
            String imagePath = (avatar == null || avatar.isEmpty()) ? "user.png" : avatar;
            Image img = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + imagePath));

            ImageView iv = new ImageView(img);
            iv.setFitHeight(40);
            iv.setFitWidth(40);
            iv.setPreserveRatio(true);

            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(20, 20, 20);
            iv.setClip(clip);

            avatarFrame.getChildren().add(iv);
        } catch (Exception e) {
            Label fallback = new Label("👤");
            fallback.setStyle("-fx-font-size: 20;");
            avatarFrame.getChildren().add(fallback);
        }

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-weight: 800; -fx-text-fill: #1e293b; -fx-font-size: 14;");

        profile.getChildren().addAll(avatarFrame, nameLbl);
        card.getChildren().add(profile);
        clientContainer.getChildren().add(card);
    }

    @FXML private void handleSeeAllSchedules() {
        DashboardController.instance.loadView("trainer_sessions_list.fxml");
    }

    @FXML private void handleSeeAllClients() {
        DashboardController.instance.loadView("trainer_clients_list.fxml");
    }
}