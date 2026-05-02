package com.oop.gymquest.screens.booking;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

    private final String[][] TRAINERS = {
            {"muscle.png", "Coach Alex", "Strength & Conditioning", "08:00 AM", "10:00 AM", "02:00 PM"},
            {"group.png", "Coach Sam", "HIIT & Cardio", "09:00 AM", "11:00 AM", "03:00 PM"},
            {"muscle.png", "Coach Jordan", "Flexibility", "10:00 AM", "12:00 PM", "04:00 PM"}
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshCalendar();
        showPlaceholder();
    }

    private void refreshCalendar() {
        calendarGrid.getChildren().clear();
        String monthName = currentYM.getMonth().toString();
        monthLabel.setText(monthName.charAt(0) + monthName.substring(1).toLowerCase() + " " + currentYM.getYear());

        int days = currentYM.lengthOfMonth();
        int offset = currentYM.atDay(1).getDayOfWeek().getValue() % 7;
        LocalDate today = LocalDate.now();

        int col = offset, row = 0;
        for (int d = 1; d <= days; d++) {
            LocalDate date = currentYM.atDay(d);
            Button btn = new Button(String.valueOf(d));
            btn.getStyleClass().add("calendar-day");
            btn.setPrefSize(48, 40);

            if (date.isBefore(today)) btn.setDisable(true);
            else if (date.equals(selectedDate)) btn.getStyleClass().add("selected");

            btn.setOnAction(e -> {
                if (date.equals(selectedDate)) {
                    selectedDate = null;
                    showPlaceholder();
                } else {
                    selectedDate = date;
                    showTrainers();
                }
                refreshCalendar();
            });

            calendarGrid.add(btn, col, row);
            col++; if (col == 7) { col = 0; row++; }
        }
    }

    private void showTrainers() {
        trainerPanel.getChildren().clear();
        Label heading = new Label("Available on " + selectedDate.format(DateTimeFormatter.ofPattern("MMMM d")));
        heading.getStyleClass().add("heading-text");
        trainerPanel.getChildren().add(heading);

        for (String[] t : TRAINERS) {
            VBox card = new VBox(15); card.getStyleClass().add("card");
            HBox row = new HBox(15); row.setAlignment(Pos.CENTER_LEFT);

            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + t[0])));
            iv.setFitHeight(40); iv.setFitWidth(40);

            VBox info = new VBox(2);
            Label name = new Label(t[1]); name.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3a5f;");
            Label spec = new Label(t[2]); spec.getStyleClass().add("subtitle-gray");
            info.getChildren().addAll(name, spec);
            row.getChildren().addAll(iv, info);

            FlowPane slots = new FlowPane(10, 10);
            for (int i = 3; i < t.length; i++) {
                // Skip slots already booked in DB
                if (DatabaseHandler.isSlotBooked(t[1], selectedDate.toString(), t[i])) continue;

                Button b = new Button("🕒 " + t[i]); b.getStyleClass().add("time-slot");
                String coach = t[1]; String time = t[i];
                b.setOnAction(e -> handleBooking(coach, time));
                slots.getChildren().add(b);
            }

            if (!slots.getChildren().isEmpty()) {
                card.getChildren().addAll(row, slots);
                trainerPanel.getChildren().add(card);
            }
        }
    }

    private void handleBooking(String coach, String time) {
        Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(25); root.getStyleClass().add("custom-dialog-card");
        Label title = new Label("Confirm Booking"); title.getStyleClass().add("dialog-title");
        Label msg = new Label("Book with " + coach + " on " + selectedDate + " at " + time + "?");
        msg.getStyleClass().add("dialog-message");

        HBox btns = new HBox(15); btns.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel"); cancel.getStyleClass().add("btn-secondary");
        Button confirm = new Button("Confirm Booking"); confirm.getStyleClass().add("btn-primary");

        cancel.setOnAction(e -> dialog.close());
        confirm.setOnAction(e -> {
            // FIX: Ensure this matches the method name in your User class (getUserId or userid)
            int uid = MainApp.instance.currentUser.getUserId();
            if(DatabaseHandler.saveBooking(uid, coach, selectedDate.toString(), time)) {
                dialog.close();
                showTrainers(); // Refresh list to remove the confirmed slot
                // To reflect on dashboard, the dashboard needs to reload its data when it opens.
            }
        });

        btns.getChildren().addAll(cancel, confirm);
        root.getChildren().addAll(title, msg, btns);
        Scene scene = new Scene(root); scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/com/oop/gymquest/styles.css").toExternalForm());
        dialog.setScene(scene); dialog.showAndWait();
    }

    private void showPlaceholder() {
        trainerPanel.getChildren().clear();
        VBox ph = new VBox(15);

        ph.getStyleClass().add("card");
        ph.setAlignment(Pos.CENTER);
        ph.setPrefHeight(400);

        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
        iv.setFitHeight(60); iv.setFitWidth(60);

        Label t = new Label("Select a Date");
        t.getStyleClass().add("heading-text");

        Label s = new Label("Click on a day to see available trainers");
        s.getStyleClass().add("subtitle-gray");

        ph.getChildren().addAll(iv, t, s);
        trainerPanel.getChildren().add(ph);
    }

    @FXML
    private void onPrevMonth() {
        currentYM = currentYM.minusMonths(1); selectedDate = null;
        refreshCalendar(); showPlaceholder();
    }
    @FXML
    private void onNextMonth() {
        currentYM = currentYM.plusMonths(1);
        selectedDate = null; refreshCalendar(); showPlaceholder();
    }

    public static void handleAction() {
        if (MainApp.instance != null)
            MainApp.instance.changeScene("booking.fxml", "GymQuest - Booking");
    }
}