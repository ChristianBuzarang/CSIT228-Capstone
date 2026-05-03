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

public class MemberDashboardController {
    @FXML
    private VBox sessionsContainer;

    @FXML
    public void initialize() {
        loadUpcomingSessions();
    }

    public void loadUpcomingSessions() {
        if (sessionsContainer == null) {
            return;
        }
        sessionsContainer.getChildren().clear();
        int memberId = MainApp.instance.currentUser.getUserId();
        try (ResultSet rs = DatabaseHandler.getMemberBookings(memberId)) {
            boolean hasData = false;
            while (rs != null && rs.next()) {
                hasData = true;
                addSessionCard(
                        rs.getInt("slot_id"),
                        rs.getString("activity"),
                        rs.getString("coach_name"),
                        rs.getString("slot_date"),
                        rs.getString("slot_time")
                );
            }
            if (!hasData) {
                Label empty = new Label("No upcoming sessions.");
                empty.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 10;");
                sessionsContainer.getChildren().add(empty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                loadUpcomingSessions();
            }
        });

        card.getChildren().add(icon);
        card.getChildren().add(info);
        card.getChildren().add(spacer);
        card.getChildren().add(cancelBtn);
        sessionsContainer.getChildren().add(card);
    }
}