package com.gymquest.view;

import com.gymquest.model.AppState;
import com.gymquest.model.DataStore;
import com.gymquest.model.TrainerSession;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class TrainerView {

    private final ScrollPane root;
    private VBox scheduleList;
    private List<TrainerSession> sessions;

    public TrainerView(AppState state) {
        sessions = DataStore.getInstance().getSessions();

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        // Header
        VBox header = UIHelper.card();
        HBox hrow = new HBox(20);
        hrow.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
            UIHelper.title("Trainer Dashboard"),
            UIHelper.subtitle("Manage your schedule and sessions")
        );
        Region sp = UIHelper.spacer();
        Button addBtn = UIHelper.primaryBtn("+ Add Schedule");
        addBtn.setOnAction(e -> showAddScheduleDialog());
        Label mascot = new Label("🏋️");
        mascot.setStyle("-fx-font-size: 48px;");
        hrow.getChildren().addAll(titleBox, sp, addBtn, mascot);
        header.getChildren().add(hrow);
        content.getChildren().add(header);

        // Stats
        HBox statsRow = new HBox(16);
        long total = sessions.size();
        long booked = sessions.stream().filter(TrainerSession::isBooked).count();
        long available = sessions.stream().filter(s -> !s.isBooked()).count();
        long clients = sessions.stream().filter(TrainerSession::isBooked)
            .map(TrainerSession::getBookedBy).distinct().count();

        HBox s1 = UIHelper.statsCard("Total Slots", String.valueOf(total), "#e0f2fe", "#bae6fd", "#3b82f6");
        HBox s2 = UIHelper.statsCard("Booked", String.valueOf(booked), "#fef3c7", "#fde68a", "#d97706");
        HBox s3 = UIHelper.statsCard("Available", String.valueOf(available), "#d1fae5", "#a7f3d0", "#10b981");
        HBox s4 = UIHelper.statsCard("Clients", String.valueOf(clients), "#e0f2fe", "#bae6fd", "#3b82f6");
        for (HBox s : new HBox[]{s1, s2, s3, s4}) HBox.setHgrow(s, Priority.ALWAYS);
        statsRow.getChildren().addAll(s1, s2, s3, s4);
        content.getChildren().add(statsRow);

        // Schedule list
        VBox scheduleCard = UIHelper.card();
        scheduleCard.getChildren().add(UIHelper.heading("Your Schedule"));
        scheduleList = new VBox(10);
        refreshSchedule();
        scheduleCard.getChildren().add(scheduleList);
        content.getChildren().add(scheduleCard);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    private void refreshSchedule() {
        scheduleList.getChildren().clear();
        for (TrainerSession s : sessions) {
            scheduleList.getChildren().add(buildSessionRow(s));
        }
    }

    private HBox buildSessionRow(TrainerSession s) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle(
            (s.isBooked()
                ? "-fx-background-color: linear-gradient(to right, #fef3c7, #fde68a); -fx-border-color: #d97706;"
                : "-fx-background-color: #f0f8ff; -fx-border-color: #bae6fd;") +
            " -fx-background-radius: 12; -fx-border-width: 2; -fx-border-radius: 12;"
        );

        StackPane icon = UIHelper.avatarCircle("📅", 44,
            s.isBooked() ? "#d97706" : "#3b82f6",
            s.isBooked() ? "#f59e0b" : "#60a5fa");

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label type = UIHelper.label(s.getType());
        type.setStyle(type.getStyle() + " -fx-font-weight: bold;");
        HBox meta = new HBox(16);
        meta.getChildren().addAll(
            UIHelper.muted(s.getDate()),
            UIHelper.muted(s.getTime()),
            UIHelper.muted(s.getDuration() + " min")
        );
        info.getChildren().addAll(type, meta);

        Region sp = UIHelper.spacer();

        VBox statusBox = new VBox(4);
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        Label statusBadge = UIHelper.badge(
            s.isBooked() ? "Booked" : "Available",
            s.isBooked() ? "#d97706" : "#10b981",
            "white"
        );
        statusBox.getChildren().add(statusBadge);
        if (s.isBooked() && s.getBookedBy() != null) {
            Label by = UIHelper.muted("by " + s.getBookedBy());
            statusBox.getChildren().add(by);
        }

        row.getChildren().addAll(icon, info, sp, statusBox);
        return row;
    }

    private void showAddScheduleDialog() {
        Dialog<TrainerSession> dialog = new Dialog<>();
        dialog.setTitle("Add Schedule");
        dialog.setHeaderText(null);

        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setPrefWidth(400);

        TextField typeField = styledInput("e.g., Strength Training");
        TextField dateField = styledInput("2026-05-01");
        TextField timeField = styledInput("10:00 AM");
        ComboBox<String> durationBox = new ComboBox<>();
        durationBox.getItems().addAll("30 minutes", "45 minutes", "60 minutes", "90 minutes");
        durationBox.setValue("60 minutes");
        durationBox.setMaxWidth(Double.MAX_VALUE);
        durationBox.setStyle("-fx-padding: 8 12; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-background-radius: 10;");

        form.getChildren().addAll(
            fieldPair("Session Type", typeField),
            fieldPair("Date (YYYY-MM-DD)", dateField),
            fieldPair("Time", timeField),
            fieldPair("Duration", durationBox)
        );

        dialog.getDialogPane().setContent(form);
        ButtonType saveType = new ButtonType("Add Schedule", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == saveType) {
                int dur = switch (durationBox.getValue()) {
                    case "30 minutes" -> 30;
                    case "45 minutes" -> 45;
                    case "90 minutes" -> 90;
                    default -> 60;
                };
                TrainerSession ns = new TrainerSession(
                    sessions.size() + 1,
                    dateField.getText().trim(),
                    timeField.getText().trim(),
                    dur,
                    typeField.getText().trim()
                );
                sessions.add(ns);
                refreshSchedule();
                return ns;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private TextField styledInput(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle("-fx-padding: 10 14; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");
        f.setMaxWidth(Double.MAX_VALUE);
        return f;
    }

    private VBox fieldPair(String label, Control field) {
        VBox box = new VBox(4);
        Label lbl = UIHelper.label(label);
        box.getChildren().addAll(lbl, field);
        return box;
    }

    public ScrollPane getRoot() { return root; }
}
