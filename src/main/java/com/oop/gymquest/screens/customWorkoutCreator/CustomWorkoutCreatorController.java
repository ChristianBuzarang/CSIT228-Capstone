package com.oop.gymquest.screens.customWorkoutCreator;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.ExerciseDAO;
import com.oop.gymquest.data.WorkoutDAO;
import com.oop.gymquest.data.workoutdata.Exercise;
import com.oop.gymquest.data.workoutdata.Workout;
import com.oop.gymquest.data.workoutdata.WorkoutCategory;
import com.oop.gymquest.screens.dashboard.DashboardController;
import com.oop.gymquest.screens.exercisePicker.ExercisePickerDialog;
import com.oop.gymquest.screens.workouts.WorkoutsViewController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class CustomWorkoutCreatorController {

    @FXML private TextField nameField;
    @FXML private VBox exerciseListBox;

    private final List<Exercise> selectedExercises = new ArrayList<>();
    public static Workout workoutToEdit = null;

    @FXML public void initialize() {
        if (workoutToEdit != null) {
            nameField.setText(workoutToEdit.getTitle());
            selectedExercises.addAll(workoutToEdit.getExercises());
        }
        updateExerciseList();
    }

    @FXML private void handleBack() {
        workoutToEdit = null;
        MainApp.instance.changeScene("dashboard_shell.fxml", "GymQuest - Dashboard");
        if (DashboardController.instance != null) {
            DashboardController.instance.handleNavWorkouts();
        }
    }

    @FXML private void handleAddExercise() {
        List<Exercise> library = ExerciseDAO.getAll();
        if (library.isEmpty()) {
            library = buildFallbackLibrary();
        }
        ExercisePickerDialog picker = new ExercisePickerDialog(library);
        picker.showAndWait().ifPresent(ex -> {
            selectedExercises.add(ex);
            updateExerciseList();
        });
    }

    @FXML private void handleSave() {
        String title = nameField.getText().trim();
        if (title.isEmpty() || selectedExercises.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter a name and at least one exercise.");
            return;
        }
        int currentUserId = MainApp.instance.currentUser.getUserId();
        if(workoutToEdit == null){
            int newId = WorkoutDAO.getAllWorkouts(currentUserId).size() + 100;
            Workout newWorkout = new Workout(
                    newId, title, Workout.Difficulty.BEGINNER, estimateDuration(selectedExercises),
                    false, new ArrayList<>(selectedExercises), WorkoutCategory.STRENGTH,
                    "Custom routine: " + title + ".", null
            );
            WorkoutDAO.createCustomWorkout(newWorkout, currentUserId);
            showAlert(Alert.AlertType.INFORMATION, "\"" + title + "\" has been added to your workouts! 🎉");
        } else {
            Workout updatedWorkout = new Workout(
                    workoutToEdit.getId(), title, workoutToEdit.getDifficulty(), estimateDuration(selectedExercises),
                    false, new ArrayList<>(selectedExercises), WorkoutCategory.STRENGTH, workoutToEdit.getDescription(), null
            );
            WorkoutDAO.updateCustomWorkout(updatedWorkout);
            WorkoutDAO.removeWorkout(workoutToEdit.getId());
            WorkoutDAO.getAllWorkouts(currentUserId).add(updatedWorkout);
            showAlert(Alert.AlertType.INFORMATION, "\"" + title + "\" has been updated! 🎉");
        }
        workoutToEdit = null;
        handleBack();
    }

    private void updateExerciseList() {
        exerciseListBox.getChildren().clear();
        if (selectedExercises.isEmpty()) {
            VBox placeholder = new VBox();
            placeholder.setAlignment(Pos.CENTER);
            placeholder.setMinHeight(100);
            placeholder.setPadding(new Insets(20));
            placeholder.setStyle("-fx-border-color: #bae6fd; -fx-border-width: 2; -fx-border-style: dashed; -fx-border-radius: 14; -fx-background-radius: 14;");

            Label hint = new Label("Click \"+ Add Exercise\" to build your custom routine");
            hint.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
            placeholder.getChildren().add(hint);
            exerciseListBox.getChildren().add(placeholder);
            return;
        }
        for (int i = 0; i < selectedExercises.size(); i++) {
            final int idx = i;
            Exercise ex = selectedExercises.get(i);

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12));
            row.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 12; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-border-radius: 12;");

            Label numLbl = new Label(String.valueOf(i + 1));
            numLbl.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
            StackPane numCircle = new StackPane(numLbl);
            numCircle.setPrefSize(30, 30);
            numCircle.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #bae6fd; -fx-border-width: 2;");

            StackPane exIcon = WorkoutsViewController.buildExerciseIcon(ex.getCategory(), 20);

            VBox info = new VBox(2, new Label(ex.getName()), new Label(ex.getSets() + " sets × " + ex.getReps() + " reps"));
            info.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3a5f; -fx-font-size: 13px;");
            info.getChildren().get(1).setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button removeBtn = new Button("🗑");
            removeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px;");
            removeBtn.setOnAction(e -> { selectedExercises.remove(idx); updateExerciseList(); });

            row.getChildren().addAll(numCircle, exIcon, info, spacer, removeBtn);
            exerciseListBox.getChildren().add(row);
        }
    }

    private static String estimateDuration(List<Exercise> exercises) {
        return Math.max(10, exercises.stream().mapToInt(Exercise::getSets).sum() * 3) + " min";
    }

    private static List<Exercise> buildFallbackLibrary() {
        return List.of(new Exercise(1, "Push-ups", 3, "10", "💪", "strength"));
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}