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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MemberDashboardController {
    @FXML private VBox activeSessionsContainer, historySessionsContainer;
    @FXML private Label totalWorkoutsLabel, weeklyWorkoutsLabel;

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    public void refreshDashboard() {
        int uid = MainApp.instance.currentUser.getUserId();

        // 1. Load Stats
        totalWorkoutsLabel.setText(String.valueOf(DatabaseHandler.getActiveMemberCount()));

        // 2. Load Top 3 Active (Future/Today)
        activeSessionsContainer.getChildren().clear();
        try (ResultSet rs = DatabaseHandler.getTop3ActiveSessions(uid)) {
            while (rs != null && rs.next()) {
                activeSessionsContainer.getChildren().add(buildSessionCard(rs, true));
            }
            if (activeSessionsContainer.getChildren().isEmpty()) showEmptyMsg(activeSessionsContainer);
        } catch (Exception e) { e.printStackTrace(); }

        // 3. Load Top 3 History
        historySessionsContainer.getChildren().clear();
        try (ResultSet rs = DatabaseHandler.getTop3HistorySessions(uid)) {
            while (rs != null && rs.next()) {
                historySessionsContainer.getChildren().add(buildSessionCard(rs, false));
            }
            if (historySessionsContainer.getChildren().isEmpty()) showEmptyMsg(historySessionsContainer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private VBox buildSessionCard(ResultSet rs, boolean checkStatus) throws Exception {
        String activity = rs.getString("activity");
        String trainer = rs.getString("firstname") + " " + rs.getString("lastname");
        String date = rs.getString("slot_date");
        String time = rs.getString("slot_time");

        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 15; -fx-border-color: #f1f5f9; -fx-border-width: 1;");

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        // Icon
        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 10; -fx-padding: 10;");
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
        icon.setFitHeight(20); icon.setFitWidth(20);
        iconBox.getChildren().add(icon);

        VBox info = new VBox(2);
        Label title = new Label(activity + " with " + trainer);
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label meta = new Label(date + " • " + time);
        meta.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");
        info.getChildren().addAll(title, meta);

        row.getChildren().addAll(iconBox, info);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().add(spacer);

        // Status Badge Logic
        if (checkStatus) {
            Label badge = calculateStatus(date, time);
            row.getChildren().add(badge);
        }

        card.getChildren().add(row);
        return card;
    }

    private Label calculateStatus(String dateStr, String timeStr) {
        LocalDate date = LocalDate.parse(dateStr);
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
        LocalTime time = LocalTime.parse(timeStr, parser);

        Label badge = new Label();
        badge.setPadding(new Insets(4, 12, 4, 12));

        if (date.isBefore(LocalDate.now())) {
            badge.setText("FINISHED");
            badge.setStyle("-fx-background-color: #f0fdf4; -fx-text-fill: #10b981; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 10;");
        } else if (date.isEqual(LocalDate.now()) && LocalTime.now().isAfter(time) && LocalTime.now().isBefore(time.plusHours(1))) {
            badge.setText("ONGOING");
            badge.setStyle("-fx-background-color: #fff7ed; -fx-text-fill: #f97316; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 10;");
        } else {
            badge.setText("UPCOMING");
            badge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 10;");
        }
        return badge;
    }

    private void showEmptyMsg(VBox container) {
        Label lbl = new Label("No recent sessions found.");
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 10;");
        container.getChildren().add(lbl);
    }

    @FXML private void handleSeeAll() {
        DashboardController.instance.loadView("sessions.fxml");
    }
}