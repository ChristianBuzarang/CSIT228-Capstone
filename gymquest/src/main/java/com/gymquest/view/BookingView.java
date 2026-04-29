package com.gymquest.view;

import com.gymquest.controller.MainController;
import com.gymquest.model.AppState;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class BookingView {

    private final ScrollPane root;
    private LocalDate selectedDate = null;
    private YearMonth currentYM = YearMonth.now();
    private GridPane calGrid;
    private VBox trainerPanel;
    private Label monthLabel;

    private static final String[][] TRAINERS = {
        {"🏋️", "Coach Alex", "Strength & Conditioning", "08:00 AM", "10:00 AM", "02:00 PM", "04:00 PM"},
        {"🏃", "Coach Sam", "HIIT & Cardio", "09:00 AM", "11:00 AM", "03:00 PM", "05:00 PM"},
        {"🧘", "Coach Jordan", "Flexibility & Mobility", "10:00 AM", "12:00 PM", "02:00 PM", "06:00 PM"},
        {"⚡", "Coach Taylor", "CrossFit & Functional", "07:00 AM", "09:00 AM", "01:00 PM", "03:00 PM"}
    };

    public BookingView(AppState state, MainController controller) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        // Header
        VBox header = UIHelper.card();
        HBox hrow = new HBox(20);
        hrow.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
            UIHelper.title("Book a Training Session"),
            UIHelper.subtitle("Select a date to see available trainers and time slots")
        );
        Region sp = UIHelper.spacer();
        Label mascot = new Label("📅");
        mascot.setStyle("-fx-font-size: 48px;");
        hrow.getChildren().addAll(titleBox, sp, mascot);
        header.getChildren().add(hrow);
        content.getChildren().add(header);

        // Calendar + trainers side by side
        HBox mainRow = new HBox(20);
        mainRow.setAlignment(Pos.TOP_LEFT);

        // Calendar
        VBox calCard = UIHelper.card();
        calCard.setPrefWidth(450);

        HBox navRow = new HBox(12);
        navRow.setAlignment(Pos.CENTER_LEFT);
        monthLabel = new Label(getMonthLabel());
        monthLabel.setStyle("-fx-text-fill: #1e3a5f; -fx-font-weight: bold; -fx-font-size: 18px;");
        Region navSp = UIHelper.spacer();
        Button prev = new Button("‹");
        Button next = new Button("›");
        styleNavBtn(prev);
        styleNavBtn(next);
        prev.setOnAction(e -> { currentYM = currentYM.minusMonths(1); refreshCal(); });
        next.setOnAction(e -> { currentYM = currentYM.plusMonths(1); refreshCal(); });
        navRow.getChildren().addAll(monthLabel, navSp, prev, next);
        calCard.getChildren().add(navRow);

        // Day headers
        HBox dayHeaders = new HBox(4);
        for (String d : new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}) {
            Label dl = new Label(d);
            dl.setPrefWidth(52);
            dl.setAlignment(Pos.CENTER);
            dl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
            dayHeaders.getChildren().add(dl);
        }
        calCard.getChildren().add(dayHeaders);

        calGrid = new GridPane();
        calGrid.setHgap(4);
        calGrid.setVgap(4);
        calCard.getChildren().add(calGrid);
        refreshCal();

        // Trainer panel
        trainerPanel = new VBox(16);
        trainerPanel.setPrefWidth(400);
        showPlaceholder();

        mainRow.getChildren().addAll(calCard, trainerPanel);
        content.getChildren().add(mainRow);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    private void refreshCal() {
        calGrid.getChildren().clear();
        monthLabel.setText(getMonthLabel());

        LocalDate first = currentYM.atDay(1);
        int startDow = first.getDayOfWeek().getValue() % 7; // Sun=0
        int days = currentYM.lengthOfMonth();
        LocalDate today = LocalDate.now();

        int col = startDow, row = 0;
        for (int d = 1; d <= days; d++) {
            final int day = d;
            LocalDate date = currentYM.atDay(d);
            boolean isPast = date.isBefore(today);
            boolean isToday = date.equals(today);
            boolean isSelected = date.equals(selectedDate);

            Button btn = new Button(String.valueOf(d));
            btn.setPrefSize(52, 40);
            btn.setMinSize(52, 40);
            btn.setStyle(dayBtnStyle(isPast, isToday, isSelected));

            if (!isPast) {
                btn.setOnAction(e -> {
                    selectedDate = currentYM.atDay(day);
                    refreshCal();
                    showTrainers();
                });
            } else {
                btn.setDisable(true);
            }

            calGrid.add(btn, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    private String dayBtnStyle(boolean past, boolean today, boolean selected) {
        String base = "-fx-background-radius: 10; -fx-font-size: 13px; -fx-cursor: ";
        if (past) return base + "default; -fx-text-fill: #cbd5e1; -fx-background-color: transparent;";
        if (selected) return base + "hand; -fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold;";
        if (today) return base + "hand; -fx-background-color: #fef3c7; -fx-text-fill: #d97706; -fx-border-color: #d97706; -fx-border-width: 2;";
        return base + "hand; -fx-background-color: transparent; -fx-text-fill: #1e3a5f;";
    }

    private void showPlaceholder() {
        trainerPanel.getChildren().clear();
        VBox ph = UIHelper.card();
        ph.setAlignment(Pos.CENTER);
        ph.setPrefHeight(300);
        Label icon = new Label("📅");
        icon.setStyle("-fx-font-size: 56px;");
        Label msg = UIHelper.heading("Select a Date");
        Label sub = UIHelper.subtitle("Click on a day to see available trainers");
        ph.getChildren().addAll(icon, msg, sub);
        trainerPanel.getChildren().add(ph);
    }

    private void showTrainers() {
        trainerPanel.getChildren().clear();
        String dateStr = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d"));
        Label heading = UIHelper.heading("Available on " + dateStr);
        trainerPanel.getChildren().add(heading);

        for (String[] t : TRAINERS) {
            VBox card = UIHelper.card(16);
            HBox trow = new HBox(12);
            trow.setAlignment(Pos.CENTER_LEFT);

            StackPane avatar = UIHelper.avatarCircle(t[0], 44, "#3b82f6", "#60a5fa");
            VBox info = new VBox(3);
            Label name = UIHelper.label(t[1]);
            name.setStyle(name.getStyle() + " -fx-font-weight: bold;");
            Label spec = UIHelper.muted(t[2]);
            info.getChildren().addAll(name, spec);

            trow.getChildren().addAll(avatar, info);
            card.getChildren().add(trow);

            // Time slots
            FlowPane slots = new FlowPane(8, 8);
            for (int i = 3; i < t.length; i++) {
                final String slot = t[i];
                final String trainerName = t[1];
                Button slotBtn = new Button("🕐 " + slot);
                slotBtn.setStyle(
                    "-fx-background-color: #e0f2fe; -fx-text-fill: #1e3a5f;" +
                    "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 6 12;"
                );
                slotBtn.setOnMouseEntered(e -> slotBtn.setStyle(
                    "-fx-background-color: #3b82f6; -fx-text-fill: white;" +
                    "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 6 12;"
                ));
                slotBtn.setOnMouseExited(e -> slotBtn.setStyle(
                    "-fx-background-color: #e0f2fe; -fx-text-fill: #1e3a5f;" +
                    "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 6 12;"
                ));
                slotBtn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Session booked with " + trainerName + " on " +
                        selectedDate.format(DateTimeFormatter.ofPattern("MMM d")) +
                        " at " + slot + "! 🎉", ButtonType.OK);
                    alert.setHeaderText(null);
                    alert.showAndWait();
                });
                slots.getChildren().add(slotBtn);
            }
            card.getChildren().add(slots);
            trainerPanel.getChildren().add(card);
        }
    }

    private void styleNavBtn(Button btn) {
        btn.setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #3b82f6; -fx-background-radius: 20; -fx-cursor: hand; -fx-font-size: 18px; -fx-padding: 4 12;");
    }

    private String getMonthLabel() {
        return currentYM.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
            + " " + currentYM.getYear();
    }

    public ScrollPane getRoot() { return root; }
}
