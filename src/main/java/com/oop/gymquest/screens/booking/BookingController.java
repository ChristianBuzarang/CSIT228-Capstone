package com.oop.gymquest.screens.booking;

import com.oop.gymquest.app.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BookingController implements Initializable {

    @FXML private Label monthLabel;
    @FXML private GridPane calendarGrid;
    @FXML private VBox trainerPanel;

    private LocalDate selectedDate = null;
    private YearMonth currentYM = YearMonth.now();

    private static final String[][] TRAINERS = {
            {"🏋️", "Coach Alex", "Strength & Conditioning", "08:00 AM", "10:00 AM", "02:00 PM", "04:00 PM"},
            {"🏃", "Coach Sam", "HIIT & Cardio", "09:00 AM", "11:00 AM", "03:00 PM", "05:00 PM"},
            {"🧘", "Coach Jordan", "Flexibility & Mobility", "10:00 AM", "12:00 PM", "02:00 PM", "06:00 PM"}
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshCalendar();
        showPlaceholder();
    }

    @FXML
    private void onPrevMonth() {
        currentYM = currentYM.minusMonths(1);
        refreshCalendar();
    }

    @FXML
    private void onNextMonth() {
        currentYM = currentYM.plusMonths(1);
        refreshCalendar();
    }

    private void refreshCalendar() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(currentYM.getMonth().toString() + " " + currentYM.getYear());

        LocalDate first = currentYM.atDay(1);
        int startDow = first.getDayOfWeek().getValue() % 7;
        int days = currentYM.lengthOfMonth();
        LocalDate today = LocalDate.now();

        int col = startDow, row = 0;
        for (int d = 1; d <= days; d++) {
            LocalDate date = currentYM.atDay(d);
            Button btn = new Button(String.valueOf(d));
            btn.setPrefSize(52, 40);

            // Apply Styles
            btn.getStyleClass().add("calendar-day");
            if (date.isBefore(today)) btn.setDisable(true);
            if (date.equals(today)) btn.getStyleClass().add("today");
            if (date.equals(selectedDate)) btn.getStyleClass().add("selected");

            btn.setOnAction(e -> {
                selectedDate = date;
                refreshCalendar();
                showTrainers();
            });

            calendarGrid.add(btn, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    private void showPlaceholder() {
        trainerPanel.getChildren().clear();
        VBox ph = new VBox(10);
        ph.getStyleClass().add("card");
        ph.setAlignment(Pos.CENTER);
        ph.setPrefHeight(300);

        Label icon = new Label("📅");
        icon.setStyle("-fx-font-size: 56px;");
        Label msg = new Label("Select a Date");
        msg.getStyleClass().add("heading-text");

        ph.getChildren().addAll(icon, msg);
        trainerPanel.getChildren().add(ph);
    }

    private void showTrainers() {
        trainerPanel.getChildren().clear();
        Label heading = new Label("Available on " + selectedDate.format(DateTimeFormatter.ofPattern("MMMM d")));
        heading.getStyleClass().add("heading-text");
        trainerPanel.getChildren().add(heading);

        for (String[] t : TRAINERS) {
            VBox card = new VBox(12);
            card.getStyleClass().add("card");

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            Label avatar = new Label(t[0]);
            avatar.getStyleClass().add("avatar-circle");

            VBox info = new VBox(2);
            Label name = new Label(t[1]);
            name.setStyle("-fx-font-weight: bold;");
            Label spec = new Label(t[2]);
            spec.setStyle("-fx-text-fill: #64748b;");
            info.getChildren().addAll(name, spec);

            row.getChildren().addAll(avatar, info);
            card.getChildren().add(row);

            FlowPane slots = new FlowPane(8, 8);
            for (int i = 3; i < t.length; i++) {
                Button slotBtn = new Button(t[i]);
                slotBtn.getStyleClass().add("time-slot");
                String trainerName = t[1];
                String time = t[i];
                slotBtn.setOnAction(e -> {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Booked " + trainerName + " at " + time);
                    a.show();
                });
                slots.getChildren().add(slotBtn);
            }
            card.getChildren().add(slots);
            trainerPanel.getChildren().add(card);
        }
    }

    public static void handleAction() {
        MainApp.instance.changeScene("/com/oop/gymquest/fxml/booking-view.fxml", "Book Training");
    }
}