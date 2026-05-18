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

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    public void refreshDashboard() {
        int uid = MainApp.instance.currentUser.getUserId();

        activeSessionsContainer.getChildren().clear();
        try (ResultSet rs = DatabaseHandler.getTop3ActiveSessions(uid)) {
            int count = 0;
            while (rs != null && rs.next()) {
                activeSessionsContainer.getChildren().add(buildSessionCard(rs, true));
                count++;
            }
            if (count == 0) {
                showEmptyPlaceholder(activeSessionsContainer, "No Upcoming Sessions", "Book a session to start your journey!");
            }
        } catch (Exception e) { e.printStackTrace(); }

        historySessionsContainer.getChildren().clear();
        try (ResultSet rs = DatabaseHandler.getTop3HistorySessions(uid)) {
            int count = 0;
            while (rs != null && rs.next()) {
                historySessionsContainer.getChildren().add(buildSessionCard(rs, false));
                count++;
            }
            if (count == 0) {
                showEmptyPlaceholder(historySessionsContainer, "No Completed Activity", "Your completed sessions will appear here.");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private LocalTime parseDatabaseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
                return LocalTime.parse(timeStr, formatter);
            } catch (Exception ex) {
                System.err.println("Warning: Completely unknown time format '" + timeStr + "'. Defaulting to 12:00 AM.");
                return LocalTime.MIDNIGHT;
            }
        }
    }

    private VBox buildSessionCard(ResultSet rs, boolean checkStatus) throws Exception {
        String activity = rs.getString("activity");
        String trainer = rs.getString("firstname") + " " + rs.getString("lastname");
        String dateStr = rs.getString("slot_date");
        String timeStr = rs.getString("slot_time");

        LocalTime parsedTime = parseDatabaseTime(timeStr);
        String formattedTime = parsedTime.format(DateTimeFormatter.ofPattern("h:mm a"));

        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; " +
                "-fx-padding: 18; " +
                "-fx-background-radius: 18; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 10, 0, 0, 4);");

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 12; -fx-padding: 12;");

        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
            icon.setFitHeight(22);
            icon.setFitWidth(22);
            iconBox.getChildren().add(icon);
        } catch (Exception e) {
            iconBox.getChildren().add(new Label("📅"));
        }

        VBox info = new VBox(3);
        Label title = new Label(activity + " with " + trainer);
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-font-size: 14;");

        Label meta = new Label(dateStr + " • " + formattedTime);
        meta.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");
        info.getChildren().addAll(title, meta);

        row.getChildren().addAll(iconBox, info);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().add(spacer);

        if (checkStatus) {
            Label badge = calculateStatus(dateStr, parsedTime);
            row.getChildren().add(badge);
        }

        card.getChildren().add(row);
        return card;
    }

    private Label calculateStatus(String dateStr, LocalTime time) {
        LocalDate date = LocalDate.parse(dateStr);

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

    private void showEmptyPlaceholder(VBox container, String title, String subtitle) {
        VBox ph = new VBox(15);
        ph.setAlignment(Pos.CENTER);
        ph.setPadding(new Insets(40, 0, 40, 0));
        ph.setOpacity(0.4);
        ph.setStyle("-fx-background-color: transparent;");

        try {
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
            iv.setFitHeight(60); iv.setFitWidth(60);
            ph.getChildren().add(iv);
        } catch (Exception e) { }

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-text-fill: #1e293b;");
        Label s = new Label(subtitle);
        s.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");

        ph.getChildren().addAll(t, s);
        container.getChildren().add(ph);
    }

    @FXML private void handleSeeAll() {
        DashboardController.instance.loadView("sessions.fxml");
    }
}