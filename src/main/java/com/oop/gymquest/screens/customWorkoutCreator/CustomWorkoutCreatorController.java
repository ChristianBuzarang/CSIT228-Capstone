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


public class CustomWorkoutCreatorController {

    // ── FXML injections ────────────────────────────────────────────────────
    @FXML private TextField               nameField;
    @FXML private ComboBox<WorkoutCategory> categoryPicker;   // NEW
    @FXML private VBox                    exerciseListBox;

    // ── Runtime state ──────────────────────────────────────────────────────
    private final List<Exercise> selectedExercises = new ArrayList<>();

    // ── Lifecycle ──────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // ── Populate the category ComboBox from the enum ──────────────────
        categoryPicker.getItems().addAll(WorkoutCategory.values());
        categoryPicker.setValue(WorkoutCategory.STRENGTH);  // sensible default

        // Use human-readable labels instead of raw enum names (e.g. "Strength" vs "STRENGTH")
        categoryPicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(WorkoutCategory cat) {
                return cat == null ? "" : WorkoutsViewController.getCategoryLabel(cat);
            }
            @Override
            public WorkoutCategory fromString(String s) {
                return null; // not needed for a non-editable ComboBox
            }
        });

        // Style the ComboBox row cells to show the category color dot
        categoryPicker.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(WorkoutCategory cat, boolean empty) {
                super.updateItem(cat, empty);
                if (empty || cat == null) {
                    setText(null);
                } else {
                    String color = WorkoutsViewController.getCategoryColor(cat);
                    String label = WorkoutsViewController.getCategoryLabel(cat);
                    setText("  ● " + label);
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });

        updateExerciseList();
    }

    // ── Navigation ─────────────────────────────────────────────────────────


    @FXML
    private void handleBack() {
        // Return to the shell
        MainApp.instance.changeScene("dashboard_shell.fxml", "GymQuest - Dashboard");

        // Refresh the content area to the workouts view
        if (DashboardController.instance != null) {
            DashboardController.instance.handleNavWorkouts(); // Works now because it's public
        }
    }

    // ── Exercise picker ────────────────────────────────────────────────────

    @FXML
    private void handleAddExercise() {
        List<Exercise> library = ExerciseDAO.getAll();

        // Fallback: if DB isn't connected, build a basic exercise list from
        // the default workouts so the feature works during development.
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

    /**
     * Validates inputs, creates the Workout, persists it, and navigates back.
     *
     * FIX: category is now read from the ComboBox, not hardcoded to STRENGTH.
     * FIX: WorkoutDAO.createCustomWorkout() also adds to the runtime cache so
     *      the workouts list shows the new workout immediately.
     */
    @FXML
    private void handleSave() {
        String title = nameField.getText().trim();
        WorkoutCategory selectedCategory = categoryPicker.getValue();

        // ── Validation ─────────────────────────────────────────────────────
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

        // ── Build Workout object ────────────────────────────────────────────
        // ID uses list size + offset so it doesn't collide with the 0-5 defaults.
        int newId = WorkoutDAO.getAllWorkouts().size() + 100;

        Workout newWorkout = new Workout(
            newId,
            title,
            Workout.Difficulty.BEGINNER,            // default for custom workouts
            estimateDuration(selectedExercises),    // smart duration estimate
            false,                                  // never locked
            new ArrayList<>(selectedExercises),
            selectedCategory,                       // FIX: from ComboBox
            "Custom routine: " + title + ".",
            null                                    // no image for custom workouts
        );

        // ── Persist (DB + in-memory cache) ─────────────────────────────────
        // createCustomWorkout() always adds to the runtime cache regardless of
        // whether the DB write succeeds, so the workouts page always shows it.
        WorkoutDAO.createCustomWorkout(newWorkout);

        // ── Confirm and navigate ───────────────────────────────────────────
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Workout Saved!");
        info.setHeaderText(null);
        info.setContentText("\"" + title + "\" has been added to your " +
            WorkoutsViewController.getCategoryLabel(selectedCategory) + " workouts! 🎉");
        info.showAndWait();

        handleBack();   // navigate to workouts — new card will appear immediately
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

            // Emoji
            Label emojiLbl = new Label(ex.getEmoji());
            emojiLbl.setStyle("-fx-font-size: 20px;");

            // Name + sets/reps
            VBox info = new VBox(2);
            Label nameLbl = new Label(ex.getName());
            nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3a5f; -fx-font-size: 13px;");
            Label detailLbl = new Label(ex.getSets() + " sets × " + ex.getReps() + " reps");
            detailLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
            info.getChildren().addAll(nameLbl, detailLbl);

            // Spacer + remove button
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

            row.getChildren().addAll(numCircle, emojiLbl, info, spacer, removeBtn);
            exerciseListBox.getChildren().add(row);
        }
    }

    // ── Utilities ──────────────────────────────────────────────────────────

    /**
     * Estimates a workout duration from the exercise count.
     * Assumes roughly 3–4 minutes per exercise (sets × rest).
     */
    private static String estimateDuration(List<Exercise> exercises) {
        int totalSets = exercises.stream().mapToInt(Exercise::getSets).sum();
        int minutes = Math.max(10, totalSets * 3);  // ≈3 min per set incl. rest
        return minutes + " min";
    }

    /**
     * Built-in exercise list used when the DB is not yet connected.
     * Covers all major muscle groups so users can still build meaningful routines.
     */
    private static List<Exercise> buildFallbackLibrary() {
        return List.of(
            new Exercise(1,  "Push-ups",           3, "10-12",   "💪", "strength"),
            new Exercise(2,  "Squats",             3, "15",      "🦵", "strength"),
            new Exercise(3,  "Plank",              3, "30 sec",  "🧘", "core"),
            new Exercise(4,  "Lunges",             3, "10 each", "🏃", "strength"),
            new Exercise(5,  "Burpees",            4, "10",      "💥", "cardio"),
            new Exercise(6,  "Mountain Climbers",  3, "20",      "⛰️", "cardio"),
            new Exercise(7,  "Dumbbell Press",     4, "8-10",    "🏋️", "strength"),
            new Exercise(8,  "Pull-ups",           3, "6-8",     "💪", "strength"),
            new Exercise(9,  "Bicep Curls",        3, "12",      "💪", "strength"),
            new Exercise(10, "Tricep Dips",        3, "10",      "🔥", "strength"),
            new Exercise(11, "Crunches",           4, "20",      "🔥", "core"),
            new Exercise(12, "Russian Twists",     3, "15 each", "🌀", "core"),
            new Exercise(13, "Leg Raises",         3, "12",      "🦵", "core"),
            new Exercise(14, "Jumping Jacks",      3, "30",      "⚡", "cardio"),
            new Exercise(15, "Jump Rope",          3, "1 min",   "🪢", "cardio"),
            new Exercise(16, "Yoga Stretches",     1, "10 min",  "🧘", "flexibility"),
            new Exercise(17, "Hamstring Stretch",  2, "30 sec",  "🦵", "flexibility"),
            new Exercise(18, "Deadlifts",          4, "6-8",     "🏋️", "strength"),
            new Exercise(19, "Kettlebell Swings",  4, "15",      "⚡", "cardio"),
            new Exercise(20, "Box Jumps",          3, "10",      "📦", "cardio")
        );
    }

    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
