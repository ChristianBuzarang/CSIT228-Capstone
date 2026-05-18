package com.oop.gymquest.screens.exercisePicker;

import com.oop.gymquest.data.workoutdata.Exercise;
import com.oop.gymquest.screens.workouts.WorkoutsViewController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ExercisePickerDialog extends Dialog<Exercise> {

    private static final Object[][] STATIC_EXERCISES = {
            {"Bench Press",          4, "8",    "🏋️", "strength"},
            {"Overhead Press",       3, "10",      "🏋️", "strength"},
            {"Barbell Row",          4, "8",    "🏋️", "strength"},
            {"Pull-ups",             3, "8",     "💪", "strength"},
            {"Push-ups",             3, "15",   "💪", "strength"},
            {"Dumbbell Curl",        3, "12",      "💪", "strength"},
            {"Tricep Dips",          3, "10",      "🔥", "strength"},
            {"Lunges",               3, "10", "🏃", "strength"},
            {"Treadmill Sprint",     5, "30 sec",  "🏃", "cardio"},
            {"Jump Rope",            3, "60 sec",   "🪢", "cardio"},
            {"Cycling",              1, "20 min",  "🚴", "cardio"},
            {"Plank",                3, "45 sec",  "🧘", "core"},
            {"Crunches",             4, "20",      "🔥", "core"},
            {"Russian Twists",       3, "15", "🌀", "core"},
            {"Leg Raises",           3, "12",      "🦵", "core"},
            {"Bicycle Crunch",       3, "20",      "🔥", "core"},
            {"Mountain Climbers",    3, "20",      "⛰️", "core"},
            {"Side Plank",           3, "30 sec",  "🧘", "core"},
            {"Hip Flexor Stretch",   2, "30 sec",  "🧘", "flexibility"},
            {"Hamstring Stretch",    2, "30 sec",  "🦵", "flexibility"},
            {"Shoulder Stretch",     2, "30 sec",  "💪", "flexibility"},
            {"Cat-Cow Stretch",      1, "60 sec",   "🐱", "flexibility"},
            {"Child's Pose",         1, "60 sec",   "🧘", "flexibility"},
            {"Quad Stretch",         2, "30 sec",  "🦵", "flexibility"},
            {"Burpees",              5, "10",      "💥", "hiit"},
            {"Squat Jumps",          4, "15",      "💥", "hiit"},
            {"Push-up to T-Raise",   3, "10",      "💥", "hiit"},
            {"Lateral Bound",        3, "12", "💥", "hiit"},
            {"Tuck Jumps",           4, "12",      "💥", "hiit"},
            {"Plyo Push-ups",        3, "10",      "💥", "hiit"},
            {"Broad Jump",           4, "8",       "💥", "hiit"},
            {"Single-Leg Stand",     3, "30 sec",  "🧍", "balance"},
            {"Single-Leg RDL",       3, "8",  "🧍", "balance"},
            {"Bosu Ball Squat",      3, "12",      "🟣", "balance"},
            {"Pistol Squat",         3, "5",  "🦵", "balance"},
            {"Heel-to-Toe Walk",     3, "15","👣", "balance"},
            {"Tree Pose",            2, "30 sec",  "🧘", "balance"},
            {"Lateral Step-over",    3, "10", "🧍", "balance"}
    };

    private static final String NAVY    = "#1e3a5f";
    private static final String SLATE   = "#64748b";
    private static final String BORDER  = "#bae6fd";
    private static final String CARD_BG = "#f8fafc";

    private final List<Exercise> allExercises = new ArrayList<>();
    private Exercise selectedExercise = null;

    private final TextField searchField   = new TextField();
    private final HBox      filterRow     = new HBox(8);
    private final FlowPane  grid          = new FlowPane(14, 14);
    private String          activeCategory = null;

    private final Label            selectedNameLbl = new Label("Select an exercise to add...");
    private final Spinner<Integer> setsSpinner     = new Spinner<>(1, 20, 3);
    private final TextField        repsField       = new TextField();
    private Button                 addBtnRef;

    public ExercisePickerDialog(List<Exercise> library) {
        setTitle("Exercise Library");
        setHeaderText(null);

        int idCounter = 1000;
        for (Object[] row : STATIC_EXERCISES) {
            allExercises.add(new Exercise(
                    idCounter++,
                    (String) row[0],
                    (int) row[1],
                    (String) row[2],
                    (String) row[3],
                    (String) row[4]
            ));
        }

        applyHighFidelityCSS();
        buildContent();
        buildButtons();
        setupResultConverter();
    }

    public ExercisePickerDialog(List<Exercise> library, Exercise exerciseToEdit) {
        this(library);
        Button okNode = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okNode.setText("Update Exercise");
        Platform.runLater(() -> preSelectExercise(exerciseToEdit));
    }

    private void applyHighFidelityCSS() {
        try {
            String cssUrl = getClass().getResource("/com/oop/gymquest/styles.css").toExternalForm();
            getDialogPane().getStylesheets().add(cssUrl);
        } catch (NullPointerException e) {
            System.err.println("Could not load styles.css for the Exercise dialog. Please check the path.");
        }
    }

    private void buildContent() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setPrefWidth(720);

        searchField.setPromptText("🔍  Search exercises by name...");
        searchField.setStyle(
                "-fx-padding: 12 16; -fx-border-color: " + BORDER + "; -fx-border-width: 2;" +
                        "-fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: 13px;" +
                        "-fx-background-color: " + CARD_BG + "; -fx-text-fill: " + NAVY + ";"
        );
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.textProperty().addListener((obs, o, n) -> refreshGrid());

        filterRow.setAlignment(Pos.CENTER_LEFT);
        buildFilterPills();

        grid.setAlignment(Pos.TOP_LEFT);
        grid.setPrefWrapLength(680);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(380);
        scroll.getStyleClass().add("modern-scroll");

        HBox editPanel = new HBox(15);
        editPanel.setAlignment(Pos.CENTER_LEFT);
        editPanel.setPadding(new Insets(15));
        editPanel.setStyle(
                "-fx-background-color: #f1f5f9; -fx-background-radius: 12; " +
                        "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 12;"
        );

        selectedNameLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label setsLbl = new Label("Sets:");
        setsLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + SLATE + ";");
        setsSpinner.setPrefWidth(85);
        setsSpinner.getStyleClass().add("modern-spinner");
        setsSpinner.setDisable(true);

        Label repsLbl = new Label("Reps / Time:");
        repsLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + SLATE + ";");
        repsField.setPrefWidth(100);
        repsField.setStyle("-fx-font-size: 13px; -fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cbd5e1; -fx-padding: 6 10; -fx-text-fill: " + NAVY + "; -fx-font-weight: bold;");
        repsField.setDisable(true);

        editPanel.getChildren().addAll(selectedNameLbl, spacer, setsLbl, setsSpinner, repsLbl, repsField);

        root.getChildren().addAll(searchField, filterRow, scroll, editPanel);
        getDialogPane().setContent(root);
        getDialogPane().setStyle("-fx-background-color: white;");
        getDialogPane().setPrefWidth(760);

        refreshGrid();
    }

    private void buildButtons() {
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okNode = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okNode.setText("Add to Workout");
        okNode.setStyle(
                "-fx-background-color: #94a3b8; -fx-text-fill: white;" +
                        "-fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 24; -fx-cursor: hand;"
        );
        okNode.setDisable(true);

        Button cancelNode = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelNode.setText("Cancel");
        cancelNode.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #cbd5e1; -fx-border-radius: 10;" +
                        "-fx-text-fill: " + SLATE + "; -fx-font-weight: bold; -fx-padding: 9 24; -fx-cursor: hand;"
        );

        addBtnRef = okNode;
    }

    private void setupResultConverter() {
        setResultConverter(bt -> {
            if (bt == ButtonType.OK && selectedExercise != null) {
                return new Exercise(
                        0,
                        selectedExercise.getName(),
                        setsSpinner.getValue(),
                        repsField.getText().trim(),
                        selectedExercise.getEmoji(),
                        selectedExercise.getCategory()
                );
            }
            return null;
        });
    }

    private void buildFilterPills() {
        filterRow.getChildren().clear();
        filterRow.getChildren().add(filterPill("All", null));

        String[] categories = {"strength", "cardio", "core", "flexibility", "hiit", "balance"};
        for (String cat : categories) {
            filterRow.getChildren().add(filterPill(capitalize(cat), cat));
        }
    }

    private Button filterPill(String label, String category) {
        boolean active = (activeCategory == null && category == null)
                || (activeCategory != null && activeCategory.equals(category));
        Button btn = new Button(label);

        String color = getCategoryColor(category);
        if (active) {
            btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 20; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: white; -fx-text-fill: " + SLATE + "; -fx-border-color: " + BORDER + "; -fx-border-width: 1.5; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;");
        }

        btn.setOnAction(e -> {
            activeCategory = (activeCategory != null && activeCategory.equals(category)) ? null : category;
            buildFilterPills();
            refreshGrid();
        });
        return btn;
    }

    private void refreshGrid() {
        grid.getChildren().clear();
        clearSelection();

        final String kw = searchField.getText().toLowerCase();
        final boolean hasKw = !kw.isBlank();

        for (Exercise ex : allExercises) {
            boolean nameOk = !hasKw || ex.getName().toLowerCase().contains(kw);
            boolean catOk  = activeCategory == null || ex.getCategory().equalsIgnoreCase(activeCategory);

            if (nameOk && catOk) {
                grid.getChildren().add(buildExerciseCard(ex));
            }
        }

        if (grid.getChildren().isEmpty()) {
            Label empty = new Label("No exercises found.");
            empty.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 14px; -fx-padding: 20;");
            grid.getChildren().add(empty);
        }
    }

    private VBox buildExerciseCard(Exercise exercise) {
        VBox card = new VBox(8);
        card.setPrefWidth(215);
        card.setMaxWidth(215);
        card.setPadding(new Insets(14));
        card.setAlignment(Pos.TOP_LEFT);
        card.setUserData(exercise);

        String catColor = getCategoryColor(exercise.getCategory());

        String defaultStyle = "-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: " + BORDER + "; -fx-border-width: 2; -fx-border-radius: 14; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);";
        String hoverStyle   = "-fx-background-color: #f8fafc; -fx-background-radius: 14; -fx-border-color: " + catColor + "80; -fx-border-width: 2; -fx-border-radius: 14; -fx-cursor: hand;";
        String selectedStyle = "-fx-background-color: " + catColor + "10; -fx-background-radius: 14; -fx-border-color: " + catColor + "; -fx-border-width: 2.5; -fx-border-radius: 14; -fx-cursor: hand;";

        card.setStyle(defaultStyle);

        Image img = WorkoutsViewController.getExerciseImage(exercise.getCategory());
        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(42, 42);
        iconPane.setMinSize(42, 42);
        iconPane.setStyle("-fx-background-color: " + catColor + "15; -fx-background-radius: 10;");

        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(26); iv.setFitHeight(26);
            iv.setPreserveRatio(true); iv.setSmooth(true);
            iconPane.getChildren().add(iv);
        }

        Label nameLbl = new Label(exercise.getName());
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13.5px; -fx-text-fill: " + NAVY + ";");
        nameLbl.setWrapText(true);

        Label detailLbl = new Label(exercise.getCategory().toUpperCase());
        detailLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + catColor + ";");

        card.getChildren().addAll(iconPane, nameLbl, detailLbl);

        card.setOnMouseEntered(e -> { if (selectedExercise != exercise) card.setStyle(hoverStyle); });
        card.setOnMouseExited(e -> { if (selectedExercise != exercise) card.setStyle(defaultStyle); });

        card.setOnMouseClicked(e -> {
            grid.getChildren().forEach(n -> {
                if (n instanceof VBox vb && vb.getUserData() instanceof Exercise ex) {
                    vb.setStyle(defaultStyle);
                }
            });

            selectedExercise = exercise;
            card.setStyle(selectedStyle);

            selectedNameLbl.setText(exercise.getName());
            setsSpinner.setDisable(false);
            setsSpinner.getValueFactory().setValue(exercise.getSets());
            repsField.setDisable(false);
            repsField.setText(exercise.getReps());

            addBtnRef.setDisable(false);
            addBtnRef.setStyle("-fx-background-color: " + catColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 24; -fx-cursor: hand;");

            if (e.getClickCount() == 2) addBtnRef.fire();
        });

        return card;
    }

    private void clearSelection() {
        selectedExercise = null;
        selectedNameLbl.setText("Select an exercise to add...");
        setsSpinner.setDisable(true);
        repsField.setDisable(true);
        repsField.clear();

        if (addBtnRef != null) {
            addBtnRef.setDisable(true);
            addBtnRef.setStyle("-fx-background-color: #94a3b8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 24;");
        }
    }

    private static String getCategoryColor(String category) {
        if (category == null) return "#3b82f6";
        return switch (category.toLowerCase()) {
            case "strength" -> "#3b82f6";
            case "cardio"   -> "#ef4444";
            case "core"     -> "#6366f1";
            case "flexibility" -> "#10b981";
            case "hiit"     -> "#f97316";
            case "balance"  -> "#0d9488";
            default         -> "#3b82f6";
        };
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    private void preSelectExercise(Exercise exerciseToEdit) {
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof VBox card && card.getUserData() instanceof Exercise ex) {
                if (ex.getName().equals(exerciseToEdit.getName())) {
                    selectedExercise = ex;
                    String catColor = getCategoryColor(ex.getCategory());
                    String selectedStyle = "-fx-background-color: " + catColor + "10; -fx-background-radius: 14; -fx-border-color: " + catColor + "; -fx-border-width: 2.5; -fx-border-radius: 14; -fx-cursor: hand;";
                    card.setStyle(selectedStyle);
                    selectedNameLbl.setText(ex.getName());
                    setsSpinner.setDisable(false);
                    setsSpinner.getValueFactory().setValue(exerciseToEdit.getSets());
                    repsField.setDisable(false);
                    repsField.setText(exerciseToEdit.getReps());
                    addBtnRef.setDisable(false);
                    addBtnRef.setStyle("-fx-background-color: " + catColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 24; -fx-cursor: hand;");
                    break;
                }
            }
        }
    }
}