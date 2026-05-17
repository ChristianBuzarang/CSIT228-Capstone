package com.oop.gymquest.screens.manageSchedule;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;

public class AddScheduleDialog extends Stage {

    private final TextField typeField = new TextField();
    private final DatePicker datePicker = new DatePicker();

    // Time Components
    private final ComboBox<String> hourBox = new ComboBox<>();
    private final ComboBox<String> minuteBox = new ComboBox<>();
    private final ComboBox<String> amPmBox = new ComboBox<>();

    private final ComboBox<String> durationBox = new ComboBox<>();
    private final ManageScheduleController parent;

    public AddScheduleDialog(ManageScheduleController parent) {
        this.parent = parent;

        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Add Schedule");

        buildUI();
    }

    private void buildUI() {
        // Main Card Container (The white box with a shadow)
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setPrefWidth(480);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 8);");

        // Header (Title + Close Button)
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label("Add Schedule");
        titleLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0;");
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0;"));
        closeBtn.setOnAction(e -> close());

        header.getChildren().addAll(titleLbl, headerSpacer, closeBtn);

        // 1. Session Type
        typeField.setPromptText("e.g., Strength Training");
        VBox typeBox = createLabeledField("Session Type", typeField);

        // 2. Date Picker (Disable Past Dates)
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

        // 3. Time Picker (Properly sized to prevent "...")
        HBox timeHBox = new HBox(12);
        timeHBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 1; i <= 12; i++) hourBox.getItems().add(String.format("%02d", i));
        minuteBox.getItems().addAll("00", "15", "30", "45");
        amPmBox.getItems().addAll("AM", "PM");

        hourBox.setValue("09");
        minuteBox.setValue("00");
        amPmBox.setValue("AM");

        // Increased prefWidth slightly so the numbers fit with the new padding
        hourBox.setPrefWidth(90);
        minuteBox.setPrefWidth(90);
        amPmBox.setPrefWidth(90);

        Label colon = new Label(":");
        colon.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #64748b;");

        timeHBox.getChildren().addAll(hourBox, colon, minuteBox, amPmBox);
        VBox timeBox = createLabeledField("Time", timeHBox);

        // 4. Duration
        durationBox.setMaxWidth(Double.MAX_VALUE);
        durationBox.getItems().addAll("30 minutes", "45 minutes", "60 minutes", "90 minutes", "120 minutes");
        durationBox.setValue("60 minutes");
        VBox durBox = createLabeledField("Duration", durationBox);

        // 5. Bottom Buttons (Aligned perfectly inside the card now)
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelActionBtn = new Button("Cancel");
        cancelActionBtn.setStyle("-fx-background-color: white; -fx-text-fill: #64748b; -fx-border-color: #cbd5e1; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-weight: bold; -fx-padding: 10 24; -fx-cursor: hand;");
        cancelActionBtn.setOnAction(e -> close());

        Button saveBtn = new Button("Add Schedule");
        saveBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> handleSave());

        buttonBox.getChildren().addAll(cancelActionBtn, saveBtn);

        card.getChildren().addAll(header, typeBox, dateBox, timeBox, durBox, buttonBox);

        // Root wrapper so the shadow doesn't get clipped by the Stage boundaries
        StackPane root = new StackPane(card);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        applyHighFidelityCSS(scene); // Apply styling directly to the scene

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

        // Convert 12-hour selection to 24-hour LocalTime for comparison and database
        int h = Integer.parseInt(hourBox.getValue());
        int m = Integer.parseInt(minuteBox.getValue());
        boolean isPm = "PM".equals(amPmBox.getValue());

        int timeH = h;
        if (isPm && h < 12) timeH += 12;
        if (!isPm && h == 12) timeH = 0;

        LocalTime selectedTime = LocalTime.of(timeH, m);

        // Validation: Prevent scheduling in the past today
        if (date.equals(LocalDate.now()) && selectedTime.isBefore(LocalTime.now())) {
            showError("Invalid Time", "You cannot schedule a session for a time that has already passed today.");
            return;
        }

        // Save to DB
        String dbTime = String.format("%02d:%02d:00", timeH, m);
        String dbDate = date.toString();
        String dur = durationBox.getValue();

        int tid = MainApp.instance.currentUser.getUserId();
        boolean success = DatabaseHandler.addTrainerSlot(tid, type, dbDate, dbTime, dur);

        if (success) {
            if (parent != null) parent.refreshView();
            close(); // Close the modal
        } else {
            showError("Database Error", "Failed to save the schedule. Please try again.");
        }
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void applyHighFidelityCSS(Scene scene) {
        String css = """
            .text-field, .combo-box, .date-picker {
                -fx-background-color: #f8fafc;
                -fx-border-color: #cbd5e1;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 10 14;
                -fx-font-size: 14px;
                -fx-text-fill: #1e3a5f;
            }
            .text-field:focused, .combo-box:focused, .date-picker:focused {
                -fx-border-color: #3b82f6;
                -fx-background-color: white;
            }
            
            /* Clean up ComboBox dropdown styling */
            .combo-box .list-cell { -fx-background-color: transparent; -fx-text-fill: #1e3a5f; -fx-padding: 0; }
            .combo-box-popup .list-view { -fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 4; }
            .combo-box-popup .list-cell { -fx-padding: 8 12; -fx-border-radius: 4; -fx-background-radius: 4; }
            .combo-box-popup .list-cell:hover { -fx-background-color: #f1f5f9; -fx-text-fill: #3b82f6; }
            
            /* High Fidelity DatePicker Calendar Popup */
            .date-picker-popup {
                -fx-background-color: white;
                -fx-border-color: #e2e8f0;
                -fx-border-radius: 12;
                -fx-background-radius: 12;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);
            }
            .date-picker-popup .month-year-pane { -fx-background-color: #f8fafc; -fx-padding: 12; -fx-background-radius: 12 12 0 0; }
            .date-picker-popup .spinner .button { -fx-background-color: transparent; -fx-cursor: hand; }
            .date-picker-popup .day-cell { -fx-background-color: white; -fx-text-fill: #475569; -fx-padding: 8; -fx-cursor: hand; -fx-background-radius: 6; }
            .date-picker-popup .day-cell:hover { -fx-background-color: #f1f5f9; }
            .date-picker-popup .today { -fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold; }
            .date-picker-popup .selected { -fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; }
        """;

        String b64 = Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
        scene.getStylesheets().add("data:text/css;base64," + b64);
    }
}