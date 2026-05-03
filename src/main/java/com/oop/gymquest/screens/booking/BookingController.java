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
            {"muscle.png", "Coach Alex", "Strength & Conditioning", "08:00 AM", "10:00 AM", "02:00 PM", "04:00 PM"},
            {"group.png", "Coach Sam", "HIIT & Cardio", "09:00 AM", "11:00 AM", "03:00 PM", "05:00 PM"},
            {"muscle.png", "Coach Jordan", "Flexibility & Mobility", "10:00 AM", "12:00 PM", "02:00 PM", "06:00 PM"}
    };

    public static void handleAction() {
        if (MainApp.instance != null) {
            MainApp.instance.changeScene("booking.fxml", "GymQuest - Booking");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshCalendar();
        showPlaceholder();
    }

    private void refreshCalendar() {
        calendarGrid.getChildren().clear();
        String monthName = currentYM.getMonth().toString();
        String formattedMonth = monthName.charAt(0) + monthName.substring(1).toLowerCase() + " " + currentYM.getYear();
        monthLabel.setText(formattedMonth);
        int days = currentYM.lengthOfMonth();
        int offset = currentYM.atDay(1).getDayOfWeek().getValue() % 7;
        LocalDate today = LocalDate.now();
        for (int d = 1; d <= days; d++) {
            LocalDate date = currentYM.atDay(d);
            Button btn = new Button(String.valueOf(d));
            btn.getStyleClass().add("calendar-day");
            btn.setPrefSize(55, 45);
            if (date.isBefore(today)) {
                btn.setDisable(true);
            } else if (date.equals(selectedDate)) {
                btn.getStyleClass().add("selected");
            }
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
            int col = (offset + d - 1) % 7;
            int row = (offset + d - 1) / 7;
            calendarGrid.add(btn, col, row);
        }
    }

    private void showTrainers() {
        trainerPanel.getChildren().clear();
        String headText = "Available on " + selectedDate.format(DateTimeFormatter.ofPattern("MMMM d"));
        Label headingLabel = new Label(headText);
        headingLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: black;");
        trainerPanel.getChildren().add(headingLabel);
        for (String[] t : TRAINERS) {
            VBox card = new VBox(15);
            card.getStyleClass().add("card");
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            StackPane iconBox = new StackPane();
            iconBox.getStyleClass().add("coach-circle");
            Image tImg = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + t[0]));
            ImageView iv = new ImageView(tImg);
            iv.setFitHeight(24);
            iv.setFitWidth(24);
            iconBox.getChildren().add(iv);
            VBox info = new VBox(2);
            Label nameLabel = new Label(t[1]);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
            Label spec = new Label(t[2]);
            spec.setStyle("-fx-text-fill: #64748b;");
            info.getChildren().add(nameLabel);
            info.getChildren().add(spec);
            row.getChildren().add(iconBox);
            row.getChildren().add(info);
            FlowPane slots = new FlowPane(10, 10);
            for (int i = 3; i < t.length; i++) {
                if (DatabaseHandler.isSlotBooked(t[1], selectedDate.toString(), t[i])) {
                    continue;
                }
                Button b = new Button(t[i]);
                b.getStyleClass().add("time-slot-btn");
                Image calImg = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png"));
                ImageView calIcon = new ImageView(calImg);
                calIcon.setFitHeight(12);
                calIcon.setFitWidth(12);
                b.setGraphic(calIcon);
                String cName = t[1];
                String time = t[i];
                b.setOnAction(e -> handleBooking(cName, time));
                slots.getChildren().add(b);
            }
            if (!slots.getChildren().isEmpty()) {
                card.getChildren().add(row);
                card.getChildren().add(slots);
                trainerPanel.getChildren().add(card);
            }
        }
    }

    private void handleBooking(String coach, String time) {
        Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox root = new VBox(25);
        root.getStyleClass().add("custom-dialog-card");
        Label title = new Label("Confirm Booking");
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 20;");
        Label msg = new Label("Book with " + coach + " on " + selectedDate + " at " + time + "?");
        msg.setStyle("-fx-text-fill: black; -fx-font-size: 15;");
        HBox btns = new HBox(15);
        btns.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("btn-secondary");
        cancel.setOnAction(e -> dialog.close());
        Button confirm = new Button("Confirm Booking");
        confirm.getStyleClass().add("btn-primary");
        confirm.setOnAction(e -> {
            int uid = MainApp.instance.currentUser.getUserId();
            String date = selectedDate.toString();
            if (DatabaseHandler.saveBooking(uid, coach, date, time)) {
                dialog.close();
                showTrainers();
            }
        });
        btns.getChildren().add(cancel);
        btns.getChildren().add(confirm);
        root.getChildren().add(title);
        root.getChildren().add(msg);
        root.getChildren().add(btns);
        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/com/oop/gymquest/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showPlaceholder() {
        trainerPanel.getChildren().clear();
        VBox ph = new VBox(15);
        ph.getStyleClass().add("card");
        ph.setAlignment(Pos.CENTER);
        ph.setPrefHeight(400);
        Image phImg = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png"));
        ImageView iv = new ImageView(phImg);
        iv.setFitHeight(60);
        iv.setFitWidth(60);
        Label t = new Label("Select a Date");
        t.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 20;");
        Label s = new Label("Click on a day to see available trainers");
        s.setStyle("-fx-text-fill: #64748b;");
        ph.getChildren().add(iv);
        ph.getChildren().add(t);
        ph.getChildren().add(s);
        trainerPanel.getChildren().add(ph);
    }

    @FXML private void onPrevMonth() { currentYM = currentYM.minusMonths(1); selectedDate = null; refreshCalendar(); showPlaceholder(); }
    @FXML private void onNextMonth() { currentYM = currentYM.plusMonths(1); selectedDate = null; refreshCalendar(); showPlaceholder(); }
}