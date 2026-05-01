package com.oop.gymquest.screens.profile;

import com.oop.gymquest.app.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProfileViewController {

    @FXML private TextField nameField, emailField, phoneField;
    @FXML private TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, String> dateCol;
    @FXML private TableColumn<Attendance, String> timeCol;
    @FXML private TableColumn<Attendance, String> trainerCol;

    @FXML
    public void initialize() {

        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        trainerCol.setCellValueFactory(new PropertyValueFactory<>("trainer"));

        // Placeholder data
        ObservableList<Attendance> history = FXCollections.observableArrayList(
                new Attendance("2023-11-01", "07:00 AM", "Coach Mike"),
                new Attendance("2023-11-03", "05:30 PM", "Coach Sarah"),
                new Attendance("2023-11-05", "08:00 AM", "Coach Mike")
        );

        attendanceTable.setItems(history);
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Profile Updated");
        alert.setHeaderText(null);
        alert.setContentText("Member details for " + name + " have been saved.");
        alert.showAndWait();
    }

    public static class Attendance {
        private String date, time, trainer;

        public Attendance(String d, String t, String tr) {
            this.date = d; this.time = t; this.trainer = tr;
        }

        public String getDate() {
            return date;
        }
        public String getTime() {
            return time;
        }
        public String getTrainer() {
            return trainer;
        }
    }

    @FXML
    public static void handleAction() {
        MainApp.instance.changeScene("/com/oop/gymquest/profile.fxml", "GymQuest - Member Profile");
    }
}