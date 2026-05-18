package com.oop.gymquest.screens.manageSchedule;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.sql.ResultSet;

public class ManageScheduleController {
    @FXML private Label countLabel;
    @FXML private VBox emptyPlaceholder;
    @FXML private VBox scheduleList;

    @FXML public void initialize() { refreshView(); }

    public void refreshView() {
        scheduleList.getChildren().clear();
        int tid = MainApp.instance.currentUser.getUserId();
        int count = 0;
        try (ResultSet rs = DatabaseHandler.getTrainerFullSchedule(tid)) {
            while (rs != null && rs.next()) {
                count++;
                addCard(
                        rs.getInt("slot_id"),
                        rs.getString("activity"),
                        rs.getString("slot_date"),
                        rs.getString("slot_time"),
                        rs.getString("duration"),
                        rs.getString("status"),
                        rs.getString("booked_by_name")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        countLabel.setText("Your Schedules (" + count + ")");
        boolean isEmpty = (count == 0);
        emptyPlaceholder.setVisible(isEmpty);
        emptyPlaceholder.setManaged(isEmpty);
    }

    private void addCard(int id, String act, String date, String time, String dur, String status, String bookedByName) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("card");
        row.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(50, 50);
        iconBox.setMinSize(50, 50);
        iconBox.setMaxSize(50, 50);
        iconBox.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 25;");

        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
            icon.setFitHeight(22);
            icon.setFitWidth(22);
            iconBox.getChildren().add(icon);
        } catch (Exception e) {
            Label fallback = new Label("📅");
            iconBox.getChildren().add(fallback);
        }

        VBox content = new VBox(8);
        Label title = new Label(act);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #1e293b;");

        Label details = new Label(date + "  •  " + time + " (" + dur + ")");
        details.setStyle("-fx-text-fill: #64748b;");

        HBox badgeRow = new HBox(10);
        badgeRow.setAlignment(Pos.CENTER_LEFT);

        Label memberInfo = new Label();
        if (status.equalsIgnoreCase("Booked")) {
            memberInfo.setText("👤 Booked by: " + (bookedByName != null ? bookedByName : "Unknown"));
            memberInfo.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
        } else {
            memberInfo.setText("👥 Open for booking");
            memberInfo.setStyle("-fx-text-fill: #3b82f6;");
        }

        Label statusBadge = new Label(status.toUpperCase());
        statusBadge.setStyle(status.equalsIgnoreCase("Available")
                ? "-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-padding: 2 8; -fx-background-radius: 5;"
                : "-fx-background-color: #fef2f2; -fx-text-fill: #ef4444; -fx-padding: 2 8; -fx-background-radius: 5;");

        badgeRow.getChildren().addAll(memberInfo, statusBadge);
        content.getChildren().addAll(title, details, badgeRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Edit");
        String editNormal = "-fx-background-color: #f1f5f9; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 6 14;";
        String editHover = "-fx-background-color: #dbeafe; -fx-text-fill: #2563eb; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 6 14;";
        editBtn.setStyle(editNormal);
        editBtn.setOnMouseEntered(e -> editBtn.setStyle(editHover));
        editBtn.setOnMouseExited(e -> editBtn.setStyle(editNormal));
        editBtn.setOnAction(e -> new AddScheduleDialog(this, id, act, date, time, dur).show());

        Button delBtn = new Button("Delete");
        String delNormal = "-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 6 14;";
        String delHover = "-fx-background-color: #fecaca; -fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 6 14;";
        delBtn.setStyle(delNormal);
        delBtn.setOnMouseEntered(e -> delBtn.setStyle(delHover));
        delBtn.setOnMouseExited(e -> delBtn.setStyle(delNormal));
        delBtn.setOnAction(e -> {
            if (DatabaseHandler.deleteSlot(id)) refreshView();
        });

        HBox actionBox = new HBox(10, editBtn, delBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(iconBox, content, spacer, actionBox);
        scheduleList.getChildren().add(row);
    }

    @FXML private void handleAddSchedule() {
        new AddScheduleDialog(this).show();
    }
}