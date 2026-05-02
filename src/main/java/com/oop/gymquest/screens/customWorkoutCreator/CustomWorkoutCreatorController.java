package com.oop.gymquest.screens.customWorkoutCreator;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.*;
import com.oop.gymquest.data.workoutdata.Exercise;
import com.oop.gymquest.data.workoutdata.Workout;
import com.oop.gymquest.data.workoutdata.WorkoutCategory;
import com.oop.gymquest.screens.exercisePicker.ExercisePickerDialog;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;

public class CustomWorkoutCreatorController {
    @FXML private TextField nameField;
    @FXML private VBox exerciseListBox;
    private final List<Exercise> selectedExercises = new ArrayList<>();

    @FXML public void initialize() { updateExerciseList(); }

    @FXML private void handleBack() { MainApp.instance.changeScene("booking.fxml", "GymQuest"); }

    @FXML
    private void handleAddExercise() {
        System.out.println("Add Exercise button clicked!"); // Debug line

        List<Exercise> library = ExerciseDAO.getAll();
        if (library.isEmpty()) {
            System.out.println("Warning: Database returned 0 exercises.");
        }

        ExercisePickerDialog dialog = new ExercisePickerDialog(library);
        dialog.showAndWait().ifPresent(ex -> {
            System.out.println("Exercise selected: " + ex.getName());
            selectedExercises.add(ex);
            updateExerciseList();
        });
    }

    @FXML private void handleSave() {
        if (nameField.getText().isEmpty() || selectedExercises.isEmpty()) return;
        Workout w = new Workout(0, nameField.getText(), Workout.Difficulty.BEGINNER, "30 min", false,
                new ArrayList<>(selectedExercises), WorkoutCategory.STRENGTH, "Custom", null);
        if (WorkoutDAO.createCustomWorkout(w)) handleBack();
    }

    private void updateExerciseList() {
        exerciseListBox.getChildren().clear();
        if (selectedExercises.isEmpty()) {
            VBox empty = new VBox(new Label("Click \"Add Exercise\" to build your custom routine"));
            empty.setAlignment(Pos.CENTER); empty.setMinHeight(120);
            empty.setStyle("-fx-border-color: #bae6fd; -fx-border-width: 2; -fx-border-style: dashed; -fx-border-radius: 15;");
            exerciseListBox.getChildren().add(empty);
        } else {
            for (int i = 0; i < selectedExercises.size(); i++) {
                final int idx = i; Exercise ex = selectedExercises.get(i);
                HBox row = new HBox(15, new Label(ex.getEmoji()), new VBox(new Label(ex.getName()), new Label(ex.getSets() + "x" + ex.getReps())));
                row.setStyle("-fx-background-color: #f0f8ff; -fx-padding: 15; -fx-background-radius: 15; -fx-border-color: #bae6fd;");
                Button del = new Button("🗑"); del.setOnAction(e -> { selectedExercises.remove(idx); updateExerciseList(); });
                Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS); row.getChildren().addAll(s, del);
                exerciseListBox.getChildren().add(row);
            }
        }
    }
}