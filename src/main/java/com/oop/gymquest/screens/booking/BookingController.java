package com.oop.gymquest.screens.booking;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class BookingController implements Initializable {
    @FXML private Label monthLabel;
    @FXML private GridPane calendarGrid;
    @FXML private VBox trainerPanel;

    public static BookingController instance;
    private LocalDate selectedDate = null;
    private YearMonth currentYM = YearMonth.now();

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
                selectedDate = date;
                showTrainers();
                refreshCalendar();
            });

            int col = (offset + d - 1) % 7;
            int row = (offset + d - 1) / 7;
            calendarGrid.add(btn, col, row);
        }
    }

    private void showTrainers() {
        trainerPanel.getChildren().clear();
        Label availHeader = new Label("Available on " + selectedDate.format(DateTimeFormatter.ofPattern("MMMM d")));
        availHeader.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        trainerPanel.getChildren().add(availHeader);

        try (ResultSet rs = DatabaseHandler.getAvailableSlots(selectedDate.toString())) {
            Map<String, VBox> trainerCards = new HashMap<>();

            while (rs != null && rs.next()) {
                String coachName = rs.getString("firstname") + " " + rs.getString("lastname");
                String avatar = rs.getString("avatar");
                String activity = rs.getString("activity");
                String time = rs.getString("slot_time");
                int slotId = rs.getInt("slot_id");

                if (!trainerCards.containsKey(coachName)) {
                    VBox card = createHighFidelityTrainerCard(coachName, avatar, activity);
                    trainerCards.put(coachName, card);
                    trainerPanel.getChildren().add(card);
                }

                FlowPane slotsPane = (FlowPane) trainerCards.get(coachName).getChildren().get(1);
                Button slotBtn = createTimeSlotButton(slotId, coachName, time);
                slotsPane.getChildren().add(slotBtn);
            }

            if (trainerCards.isEmpty()) {
                VBox emptyPh = new VBox(15);
                emptyPh.setAlignment(Pos.CENTER);
                emptyPh.setPrefHeight(350);
                emptyPh.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
                iv.setFitHeight(60); iv.setFitWidth(60);

                Label t = new Label("No Schedules Found");
                t.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: #1e293b;");
                Label s = new Label("No trainers have posted a schedule for this date yet.");
                s.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14;");

                emptyPh.getChildren().addAll(iv, t, s);
                trainerPanel.getChildren().add(emptyPh);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private VBox createHighFidelityTrainerCard(String name, String avatar, String specialization) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(44, 44);
        iconBox.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 22;");

        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + avatar)));
        iv.setFitHeight(24); iv.setFitWidth(24);
        iconBox.getChildren().add(iv);

        VBox info = new VBox(2);
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-font-size: 16;");

        Label lblSpec = new Label(specialization);
        lblSpec.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");
        info.getChildren().addAll(lblName, lblSpec);

        header.getChildren().addAll(iconBox, info);

        FlowPane slots = new FlowPane(10, 10);
        card.getChildren().addAll(header, slots);
        return card;
    }

    private Button createTimeSlotButton(int slotId, String coach, String time) {
        Button b = new Button(time);
        b.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #3b82f6; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; -fx-font-weight: bold;");

        ImageView calIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
        calIcon.setFitHeight(12); calIcon.setFitWidth(12);
        b.setGraphic(calIcon);

        b.setOnAction(e -> handleBookingConfirmation(slotId, coach, time));
        return b;
    }

    private void handleBookingConfirmation(int slotId, String coach, String time) {
        boolean confirmed = com.oop.gymquest.screens.utils.CustomDialog.showConfirmation(
                "Confirm Booking",
                "Book Session with " + coach + "\nDate: " + selectedDate + "\nTime: " + time + "\n\nDo you want to proceed?",
                "Confirm",
                false
        );

        if (confirmed) {
            int memberId = MainApp.instance.currentUser.getUserId();
            if (DatabaseHandler.saveBooking(memberId, slotId)) {
                showTrainers();
                com.oop.gymquest.screens.utils.CustomDialog.showInfo("Success", "✅ Session with " + coach + " confirmed.");
            }
        }
    }

    private void showPlaceholder() {
        trainerPanel.getChildren().clear();

        VBox ph = new VBox(18);
        ph.setAlignment(Pos.CENTER);
        ph.setPrefHeight(400);
        ph.setOpacity(0.4);
        ph.setStyle("-fx-background-color: transparent;");

        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
        iv.setFitHeight(80);
        iv.setFitWidth(80);

        Label t = new Label("Select a Date");
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 22; -fx-text-fill: #94a3b8;");
        Label s = new Label("Choose a day to view available trainer slots");
        s.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 15;");
        ph.getChildren().addAll(iv, t, s);
        trainerPanel.getChildren().add(ph);
    }

    @FXML
    private void onPrevMonth() {
        currentYM = currentYM.minusMonths(1);
        selectedDate = null; refreshCalendar();
        showPlaceholder();
    }

    @FXML
    private void onNextMonth() {
        currentYM = currentYM.plusMonths(1);
        selectedDate = null; refreshCalendar();
        showPlaceholder();
    }
}