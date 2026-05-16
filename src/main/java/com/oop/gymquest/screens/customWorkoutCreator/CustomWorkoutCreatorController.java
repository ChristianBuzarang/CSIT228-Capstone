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
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * CustomWorkoutCreatorController
 *
 * ── Changes in this version ────────────────────────────────────────────────
 *  FIX — Exercise rows in updateExerciseList() now use the category image
 *        (via WorkoutsViewController.buildExerciseIcon()) instead of an emoji
 *        label.  This matches the visual language of the workouts list and
 *        exercise picker dialog.
 */
public class CustomWorkoutCreatorController {

    @FXML private TextField                 nameField;
    @FXML private ComboBox<WorkoutCategory> categoryPicker;
    @FXML private VBox                      exerciseListBox;

    private final List<Exercise> selectedExercises = new ArrayList<>();

    // ── Lifecycle ──────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        categoryPicker.getItems().addAll(WorkoutCategory.values());
        categoryPicker.setValue(WorkoutCategory.STRENGTH);

        categoryPicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(WorkoutCategory cat) {
                return cat == null ? "" : WorkoutsViewController.getCategoryLabel(cat);
            }
            @Override
            public WorkoutCategory fromString(String s) { return null; }
        });

        categoryPicker.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(WorkoutCategory cat, boolean empty) {
                super.updateItem(cat, empty);
                if (empty || cat == null) {
                    setText(null);
                } else {
                    String color = WorkoutsViewController.getCategoryColor(cat);
                    setText("  ● " + WorkoutsViewController.getCategoryLabel(cat));
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });

        updateExerciseList();
    }

    // ── Navigation ─────────────────────────────────────────────────────────

    @FXML
    private void handleBack() {
        MainApp.instance.changeScene("dashboard_shell.fxml", "GymQuest - Dashboard");
        if (DashboardController.instance != null) {
            DashboardController.instance.handleNavWorkouts();
        }
    }

    // ── Exercise picker ────────────────────────────────────────────────────

    @FXML
    private void handleAddExercise() {
        List<Exercise> library = ExerciseDAO.getAll();

        if (library.isEmpty()) {
            System.out.println("[CustomWorkoutCreator] ExerciseDAO returned empty — using fallback library.");
            library = buildFallbackLibrary();
        }

        ExercisePickerDialog picker = new ExercisePickerDialog(library);
        picker.showAndWait().ifPresent(ex -> {
            selectedExercises.add(ex);
            updateExerciseList();
        });
    }

    // ── Save ───────────────────────────────────────────────────────────────

    @FXML
    private void handleSave() {
        String title = nameField.getText().trim();
        WorkoutCategory selectedCategory = categoryPicker.getValue();

        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter a workout name.");
            return;
        }
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a category for this workout.");
            return;
        }
        if (selectedExercises.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please add at least one exercise.");
            return;
        }

        int newId = WorkoutDAO.getAllWorkouts().size() + 100;

        Workout newWorkout = new Workout(
            newId,
            title,
            Workout.Difficulty.BEGINNER,
            estimateDuration(selectedExercises),
            false,
            new ArrayList<>(selectedExercises),
            selectedCategory,
            "Custom routine: " + title + ".",
            null
        );

        // createCustomWorkout now also saves exercises to workout_exercises table
        WorkoutDAO.createCustomWorkout(newWorkout);

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Workout Saved!");
        info.setHeaderText(null);
        info.setContentText("\"" + title + "\" has been added to your " +
            WorkoutsViewController.getCategoryLabel(selectedCategory) + " workouts! 🎉");
        info.showAndWait();

        handleBack();
    }

    // ── Exercise list rendering ────────────────────────────────────────────


    private void updateExerciseList() {
        exerciseListBox.getChildren().clear();

        if (selectedExercises.isEmpty()) {
            VBox placeholder = new VBox();
            placeholder.setAlignment(Pos.CENTER);
            placeholder.setMinHeight(100);
            placeholder.setPadding(new Insets(20));
            placeholder.setStyle(
                "-fx-border-color: #bae6fd; -fx-border-width: 2;" +
                "-fx-border-style: dashed; -fx-border-radius: 14;" +
                "-fx-background-radius: 14;"
            );
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
            row.setStyle(
                "-fx-background-color: #f0f8ff; -fx-background-radius: 12;" +
                "-fx-border-color: #bae6fd; -fx-border-width: 2; -fx-border-radius: 12;"
            );


            // Step number circle
            Label numLbl = new Label(String.valueOf(i + 1));
            numLbl.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
            StackPane numCircle = new StackPane(numLbl);
            numCircle.setPrefSize(30, 30);
            numCircle.setMinSize(30, 30);
            numCircle.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15;" +
                            "-fx-border-color: #bae6fd; -fx-border-width: 2;"
            );

            // Text-based Category Badge (Replaces image icon)
            Label catBadge = new Label((ex.getCategory() != null ? ex.getCategory() : "GENERAL").toUpperCase());
            catBadge.setStyle(
                    "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748b;" +
                            "-fx-background-color: #e2e8f0; -fx-padding: 3 8; -fx-background-radius: 6;"
            );

            // Name + sets/reps
            VBox info = new VBox(4);
            Label nameLbl = new Label(ex.getName());
            nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3a5f; -fx-font-size: 13px;");
            Label detailLbl = new Label(ex.getSets() + " sets × " + ex.getReps() + " reps");
            detailLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

            HBox titleAndBadge = new HBox(8, nameLbl, catBadge);
            titleAndBadge.setAlignment(Pos.CENTER_LEFT);
            info.getChildren().addAll(titleAndBadge, detailLbl);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button removeBtn = new Button("🗑");
            removeBtn.setStyle(
                    "-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px;"
            );
            removeBtn.setOnAction(e -> {
                selectedExercises.remove(idx);
                updateExerciseList();
            });

            row.getChildren().addAll(numCircle, info, spacer, removeBtn);
            exerciseListBox.getChildren().add(row);
        }
    }

    // ── Utilities ──────────────────────────────────────────────────────────

    private static String estimateDuration(List<Exercise> exercises) {
        int totalSets = exercises.stream().mapToInt(Exercise::getSets).sum();
        int minutes   = Math.max(10, totalSets * 3);
        return minutes + " min";
    }

    private static List<Exercise> buildFallbackLibrary() {
        return List.of(
            new Exercise(1,  "Push-ups",          3, "10-12",    "strength"),
            new Exercise(2,  "Squats",            3, "15",      "strength"),
            new Exercise(3,  "Plank",             3, "30 sec",   "core"),
            new Exercise(4,  "Lunges",            3, "10 each",  "strength"),
            new Exercise(5,  "Burpees",           4, "10",       "cardio"),
            new Exercise(6,  "Mountain Climbers", 3, "20",       "cardio"),
            new Exercise(7,  "Dumbbell Press",    4, "8-10",     "strength"),
            new Exercise(8,  "Pull-ups",          3, "6-8",     "strength")
        );
    }

    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
