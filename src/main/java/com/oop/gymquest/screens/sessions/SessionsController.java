package com.oop.gymquest.screens.sessions;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.screens.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.sql.ResultSet;

public class SessionsController {
    @FXML private VBox allSessionsContainer;

    @FXML public void initialize() { loadAllSessions(); }

    private void loadAllSessions() {
        allSessionsContainer.getChildren().clear();
        int userId = MainApp.instance.currentUser.getUserId();

        String sql = "SELECT s.*, u.firstname, u.lastname FROM trainer_slots s " +
                "JOIN users u ON s.trainer_id = u.userid " +
                "WHERE s.member_id = " + userId + " " +
                "ORDER BY s.slot_date DESC, s.slot_time DESC";

        try (ResultSet rs = DatabaseHandler.getConnection().createStatement().executeQuery(sql)) {
            while (rs.next()) {
                allSessionsContainer.getChildren().add(buildSessionRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox buildSessionRow(ResultSet rs) throws Exception {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        VBox info = new VBox(5);
        Label title = new Label(rs.getString("activity") + " with " + rs.getString("firstname"));
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #1e293b;");

        Label date = new Label("Date: " + rs.getString("slot_date") + " | Time: " + rs.getString("slot_time"));
        date.setStyle("-fx-text-fill: #64748b;");
        info.getChildren().addAll(title, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status Badge
        String status = rs.getString("status");
        Label badge = new Label(status.toUpperCase());
        String badgeStyle = status.equalsIgnoreCase("Booked")
                ? "-fx-background-color: #f0fdf4; -fx-text-fill: #10b981;"
                : "-fx-background-color: #f1f5f9; -fx-text-fill: #64748b;";

        badge.setStyle(badgeStyle + "-fx-padding: 5 12; -fx-background-radius: 10; -fx-font-size: 11; -fx-font-weight: bold;");

        row.getChildren().addAll(info, spacer, badge);
        return row;
    }

    @FXML
    private void handleBack() {
        DashboardController.instance.handleNavDashboard();
    }
}