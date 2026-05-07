package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminSchedulesController {
    @FXML private Label totalSessionsLbl, bookingsLbl, capacityLbl, utilLbl, dateLabel;
    @FXML private VBox scheduleContainer, placeholder;
    @FXML private ComboBox<String> trainerFilter;

    private LocalDate selectedDate = LocalDate.now();

    @FXML
    public void initialize() {
        trainerFilter.getItems().add("All Trainers");
        trainerFilter.getSelectionModel().selectFirst();
        loadData();
    }

    private void loadData() {
        scheduleContainer.getChildren().clear();
        dateLabel.setText(selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));

        int total = 0, bookings = 0;
        try (ResultSet rs = DatabaseHandler.getTrainerSchedule(0)) { // 0 pulls all
            while (rs != null && rs.next()) {
                if (!rs.getString("slot_date").equals(selectedDate.toString())) continue;
                total++;
                if (rs.getString("status").equalsIgnoreCase("Booked")) bookings++;
                addScheduleRow(rs.getString("coach_name"), rs.getString("slot_time"), rs.getString("status"));
            }
        } catch (Exception e) { e.printStackTrace(); }

        totalSessionsLbl.setText(String.valueOf(total));
        bookingsLbl.setText(String.valueOf(bookings));
        capacityLbl.setText(String.valueOf(total * 5));
        double util = total == 0 ? 0 : ((double) bookings / total) * 100;
        utilLbl.setText(String.format("%.0f%%", util));

        placeholder.setVisible(total == 0);
        scheduleContainer.setVisible(total > 0);
    }

    private void addScheduleRow(String coach, String time, String status) {
        HBox row = new HBox(20); row.setAlignment(Pos.CENTER_LEFT); row.getStyleClass().add("card");
        row.setStyle("-fx-padding: 15; -fx-border-color: #bae6fd; -fx-border-radius: 15;");
        row.getChildren().addAll(new Label(coach), new Label(time), new Label(status.toUpperCase()));
        scheduleContainer.getChildren().add(row);
    }

    @FXML private void nextDay() { selectedDate = selectedDate.plusDays(1); loadData(); }
    @FXML private void prevDay() { selectedDate = selectedDate.minusDays(1); loadData(); }
}