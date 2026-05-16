package com.oop.gymquest.screens.exercisePicker;

import com.oop.gymquest.data.workoutdata.Exercise;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ExercisePickerDialogController {

    @FXML private TextField searchField;
    @FXML private FlowPane exerciseGrid;
    @FXML private ToggleGroup categoryGroup;

    private List<Exercise> library;
    private Button addBtn;

    // Track selected configurations
    private Exercise selectedExerciseBase;
    private TextField selectedSetsField;
    private TextField selectedRepsField;

    public void init(List<Exercise> lib, Button btn) {
        this.library = lib;
        this.addBtn = btn;
        refresh("");
        searchField.textProperty().addListener((o, old, n) -> refresh(n));
    }

    private void refresh(String query) {
        exerciseGrid.getChildren().clear();
        selectedExerciseBase = null;
        if (addBtn != null) addBtn.setDisable(true);

        for (Exercise ex : library) {
            if (!ex.getName().toLowerCase().contains(query.toLowerCase())) continue;

            VBox card = new VBox(10);
            card.setPrefSize(220, 140);
            card.setPadding(new Insets(16));

            // Text-based typography (No Images)
            Label nameLbl = new Label(ex.getName());
            nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e3a5f;");

            // Inline Configuration Inputs
            String inputStyle = "-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-padding: 4;";
            TextField setsField = new TextField(String.valueOf(ex.getSets()));
            setsField.setPrefWidth(50);
            setsField.setStyle(inputStyle);

            TextField repsField = new TextField(ex.getReps());
            repsField.setPrefWidth(80);
            repsField.setStyle(inputStyle);

            HBox configRow = new HBox(10,
                    new HBox(5, new Label("Sets:"), setsField),
                    new HBox(5, new Label("Reps/Time:"), repsField)
            );
            configRow.setAlignment(Pos.CENTER_LEFT);

            card.getChildren().addAll(nameLbl, configRow);
            card.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 15; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-cursor: hand;");

            Runnable selectRoutine = () -> {
                selectedExerciseBase = ex;
                selectedSetsField = setsField;
                selectedRepsField = repsField;
                addBtn.setDisable(false);

                exerciseGrid.getChildren().forEach(n -> n.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 15; -fx-border-color: #bae6fd; -fx-border-width: 2;"));
                card.setStyle("-fx-background-color: #e0f2fe; -fx-background-radius: 15; -fx-border-color: #3b82f6; -fx-border-width: 2;");
            };

            card.setOnMouseClicked(e -> selectRoutine.run());
            setsField.setOnMouseClicked(e -> selectRoutine.run());
            repsField.setOnMouseClicked(e -> selectRoutine.run());

            exerciseGrid.getChildren().add(card);
        }
    }

    public Exercise getSelected() {
        if (selectedExerciseBase == null) return null;

        int customSets = selectedExerciseBase.getSets();
        try {
            customSets = Integer.parseInt(selectedSetsField.getText().trim());
        } catch (NumberFormatException ignored) {}

        String customReps = selectedRepsField.getText().trim();
        if (customReps.isEmpty()) customReps = selectedExerciseBase.getReps();

        // Returns a safe clone containing the customized inline edits
        return new Exercise(
                selectedExerciseBase.getId(),
                selectedExerciseBase.getName(),
                customSets,
                customReps,
                selectedExerciseBase.getCategory()
        );
    }
}