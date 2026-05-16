package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AdminSchedulesController {
    @FXML private Label monthYearLabel, dateLabel;
    @FXML private GridPane calendarGrid;
    @FXML private VBox scheduleContainer, placeholder;
    @FXML private ComboBox<String> trainerFilter;

    private LocalDate selectedDate = null;
    private YearMonth currentMonthView;

    @FXML
    public void initialize() {
        currentMonthView = YearMonth.now();
        setupTrainerFilter();
        populateCalendar();
        placeholder.setOpacity(0.4);
        placeholder.setStyle("-fx-background-color: transparent;");
    }

    private void setupTrainerFilter() {
        trainerFilter.getItems().add("All Trainers");
        trainerFilter.getItems().addAll(DatabaseHandler.fetchTrainersNames());
        trainerFilter.getSelectionModel().selectFirst();
        trainerFilter.setOnAction(e -> { if(selectedDate != null) loadSchedules(selectedDate); });
    }

    private void populateCalendar() {
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        monthYearLabel.setText(currentMonthView.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        LocalDate firstOfMonth = currentMonthView.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonthView.lengthOfMonth();

        int row = 1;
        for (int i = 1; i <= daysInMonth; i++) {
            int col = (dayOfWeek + i - 1) % 7;
            Button dayBtn = new Button(String.valueOf(i));
            dayBtn.getStyleClass().add("calendar-day-btn");

            final LocalDate date = currentMonthView.atDay(i);
            dayBtn.setOnAction(e -> selectDate(date, dayBtn));

            if (date.equals(LocalDate.now())) dayBtn.setStyle("-fx-border-color: #3b82f6; -fx-border-width: 1.5; -fx-border-radius: 10;");
            if (date.equals(selectedDate)) dayBtn.getStyleClass().add("calendar-day-selected");

            calendarGrid.add(dayBtn, col, row);
            if (col == 6) row++;
        }
    }

    private void selectDate(LocalDate date, Button btn) {
        if (date.equals(selectedDate)) {
            selectedDate = null;
            btn.getStyleClass().remove("calendar-day-selected");
            dateLabel.setText("Select a date");
            scheduleContainer.getChildren().clear();
            placeholder.setVisible(true);
            return;
        }

        selectedDate = date;
        calendarGrid.getChildren().forEach(n -> n.getStyleClass().remove("calendar-day-selected"));
        btn.getStyleClass().add("calendar-day-selected");

        dateLabel.setText("Schedules for " + date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        loadSchedules(date);
    }

    private void loadSchedules(LocalDate date) {
        scheduleContainer.getChildren().clear();
        String selectedTrainer = trainerFilter.getValue();
        Map<String, VBox> coachCards = new HashMap<>();

        try (ResultSet rs = DatabaseHandler.getAdminAllSchedulesByDate(date.toString(), selectedTrainer)) {
            while (rs != null && rs.next()) {
                String coach = rs.getString("t_fname") + " " + rs.getString("t_lname");
                String time = rs.getString("slot_time");
                String status = rs.getString("status");
                String activity = rs.getString("activity");

                VBox card = coachCards.computeIfAbsent(coach, k -> createCoachCard(coach));
                FlowPane slots = (FlowPane) card.getChildren().get(1);

                Label pill = new Label(time + " (" + activity + ")");
                pill.getStyleClass().add("time-slot-pill");

                if ("Booked".equalsIgnoreCase(status)) {
                    pill.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 8;");
                    String bookedBy = rs.getString("m_fname") != null ? rs.getString("m_fname") : "User";
                    Tooltip.install(pill, new Tooltip("Booked by: " + bookedBy));
                } else {
                    pill.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-padding: 5 12; -fx-background-radius: 8;");
                }

                slots.getChildren().add(pill);
            }
        } catch (Exception e) { e.printStackTrace(); }

        boolean hasData = !coachCards.isEmpty();

        placeholder.setVisible(!hasData);
        placeholder.setManaged(!hasData);

        scheduleContainer.setVisible(hasData);
        scheduleContainer.setManaged(hasData);

        if (hasData) {
            scheduleContainer.getChildren().addAll(coachCards.values());
        }
    }

    private VBox createCoachCard(String name) {
        VBox card = new VBox(12);
        card.getStyleClass().add("coach-card");
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        HBox header = new HBox(12); header.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 20; -fx-padding: 8;");
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/user.png")));
        icon.setFitWidth(25); icon.setFitHeight(25);
        avatar.getChildren().add(icon);

        VBox info = new VBox(0);
        Label nameLbl = new Label(name); nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #1e3a5f;");
        Label sub = new Label("Fitness Coach");
        sub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");
        info.getChildren().addAll(nameLbl, sub);
        header.getChildren().addAll(avatar, info);

        FlowPane slotContainer = new FlowPane(10, 10);
        card.getChildren().addAll(header, slotContainer);
        return card;
    }

    @FXML private void nextMonth() { currentMonthView = currentMonthView.plusMonths(1); populateCalendar(); }
    @FXML private void prevMonth() { currentMonthView = currentMonthView.minusMonths(1); populateCalendar(); }
}