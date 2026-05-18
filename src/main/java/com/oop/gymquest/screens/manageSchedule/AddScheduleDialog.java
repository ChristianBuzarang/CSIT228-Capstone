package com.oop.gymquest.screens.manageSchedule;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.screens.utils.CustomDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.time.LocalDate;
import java.time.LocalTime;

public class AddScheduleDialog extends Stage {
    private final TextField typeField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final ComboBox<String> hourBox = new ComboBox<>();
    private final ComboBox<String> minuteBox = new ComboBox<>();
    private final ComboBox<String> amPmBox = new ComboBox<>();
    private final ComboBox<String> durationBox = new ComboBox<>();
    private final ManageScheduleController parent;

    private int editSlotId = -1;

    public AddScheduleDialog(ManageScheduleController parent) {
        this.parent = parent;
        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);
        buildUI("Add Schedule", "Add Schedule");
    }

    public AddScheduleDialog(ManageScheduleController parent, int id, String type, String date, String time, String dur) {
        this.parent = parent;
        this.editSlotId = id;
        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);
        buildUI("Edit Schedule", "Update Schedule");

        typeField.setText(type);
        datePicker.setValue(LocalDate.parse(date));
        durationBox.setValue(dur);

        try {
            String[] parts = time.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            String amPm = (h >= 12) ? "PM" : "AM";
            if (h > 12) h -= 12;
            if (h == 0) h = 12;
            hourBox.setValue(String.format("%02d", h));
            minuteBox.setValue(String.format("%02d", m));
            amPmBox.setValue(amPm);
        } catch (Exception ignored) {}
    }

    private void buildUI(String titleText, String buttonText) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setPrefWidth(480);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 8);");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(titleText);
        titleLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0;");
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0;"));
        closeBtn.setOnAction(e -> close());

        header.getChildren().addAll(titleLbl, headerSpacer, closeBtn);

        typeField.setPromptText("e.g., Strength Training");
        VBox typeBox = createLabeledField("Session Type", typeField);

        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setPromptText("Select a date");
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f8fafc; -fx-text-fill: #cbd5e1;");
                }
            }
        });
        VBox dateBox = createLabeledField("Date", datePicker);
        HBox timeHBox = new HBox(12);
        timeHBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 1; i <= 12; i++) hourBox.getItems().add(String.format("%02d", i));
        minuteBox.getItems().addAll("00", "15", "30", "45");
        amPmBox.getItems().addAll("AM", "PM");

        if (editSlotId == -1) {
            hourBox.setValue("08");
            minuteBox.setValue("00");
            amPmBox.setValue("AM");
        }

        hourBox.setPrefWidth(90);
        minuteBox.setPrefWidth(90);
        amPmBox.setPrefWidth(90);

        Label colon = new Label(":");
        colon.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #64748b;");

        timeHBox.getChildren().addAll(hourBox, colon, minuteBox, amPmBox);
        VBox timeBox = createLabeledField("Time", timeHBox);

        durationBox.setMaxWidth(Double.MAX_VALUE);
        durationBox.getItems().addAll("30 minutes", "45 minutes", "60 minutes", "90 minutes", "120 minutes");
        if (editSlotId == -1) durationBox.setValue("60 minutes");
        VBox durBox = createLabeledField("Duration", durationBox);

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelActionBtn = new Button("Cancel");
        cancelActionBtn.setStyle("-fx-background-color: white; -fx-text-fill: #64748b; -fx-border-color: #cbd5e1; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-weight: bold; -fx-padding: 10 24; -fx-cursor: hand;");
        cancelActionBtn.setOnAction(e -> close());

        Button saveBtn = new Button(buttonText);
        saveBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> handleSave());

        buttonBox.getChildren().addAll(cancelActionBtn, saveBtn);

        card.getChildren().addAll(header, typeBox, dateBox, timeBox, durBox, buttonBox);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        applyHighFidelityCSS(scene);

        setScene(scene);
        centerOnScreen();
    }

    private VBox createLabeledField(String labelText, javafx.scene.Node field) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3a5f; -fx-font-size: 14px;");
        return new VBox(8, lbl, field);
    }

    private void handleSave() {
        String type = typeField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (type.isEmpty() || date == null) {
            showError("Missing Fields", "Please fill out the Session Type and select a Date.");
            return;
        }

        int h = Integer.parseInt(hourBox.getValue());
        int m = Integer.parseInt(minuteBox.getValue());
        boolean isPm = "PM".equals(amPmBox.getValue());

        int timeH = h;
        if (isPm && h < 12) timeH += 12;
        if (!isPm && h == 12) timeH = 0;

        LocalTime selectedTime = LocalTime.of(timeH, m);

        if (date.equals(LocalDate.now()) && selectedTime.isBefore(LocalTime.now())) {
            showError("Invalid Time", "You cannot schedule a session for a time that has already passed today.");
            return;
        }

        String dbTime = String.format("%02d:%02d:00", timeH, m);
        String dbDate = date.toString();
        String dur = durationBox.getValue();

        boolean success;
        if (editSlotId == -1) {
            int tid = MainApp.instance.currentUser.getUserId();
            success = DatabaseHandler.addTrainerSlot(tid, type, dbDate, dbTime, dur);
        } else {
            success = DatabaseHandler.updateTrainerSlot(editSlotId, type, dbDate, dbTime, dur);
        }

        if (success) {
            if (parent != null) parent.refreshView();
            close();
        } else {
            showError("Database Error", "Failed to save the schedule. Please try again.");
        }
    }

    private void showError(String title, String msg) {
        CustomDialog.showError(title, msg);
    }

    private void applyHighFidelityCSS(Scene scene) {
        try {
            String cssUrl = getClass().getResource("/com/oop/gymquest/styles.css").toExternalForm();
            scene.getStylesheets().add(cssUrl);
        } catch (NullPointerException e) {
            System.err.println("Could not load styles.css for the dialog. Please check the path.");
        }
    }
}