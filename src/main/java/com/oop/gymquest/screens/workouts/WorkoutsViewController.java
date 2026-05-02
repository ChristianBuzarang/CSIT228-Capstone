package com.oop.gymquest.screens.workouts;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.WorkoutDAO;
import com.oop.gymquest.data.workoutdata.Exercise;
import com.oop.gymquest.data.workoutdata.Workout;
import com.oop.gymquest.data.workoutdata.WorkoutCategory;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;


public class WorkoutsViewController {

    // ── FXML injections ────────────────────────────────────────────────────
    @FXML private TextField   searchField;
    @FXML private HBox        filterContainer;   // pill row
    @FXML private FlowPane    cardContainer;      // workout card grid

    // ── State ──────────────────────────────────────────────────────────────
    private List<Workout>    allWorkouts;
    private WorkoutCategory  activeCategory = null;   // null = "All"

    // ── Lifecycle ──────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Load catalog — DB first, hardcoded fallback if DB is unavailable
        allWorkouts = WorkoutDAO.getAllWorkouts();

        buildCategoryPills();
        renderCards(allWorkouts);

        // Live search: re-filter on every keystroke
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    // ── Filter logic ───────────────────────────────────────────────────────

    private void applyFilter() {
        final String kw = searchField.getText().toLowerCase();
        final boolean hasKeyword = !kw.isBlank();

        List<Workout> filtered = allWorkouts.stream()
            .filter(w -> {
                boolean keyOk = !hasKeyword
                    || w.getTitle().toLowerCase().contains(kw)
                    || (w.getDescription() != null && w.getDescription().toLowerCase().contains(kw));
                boolean catOk = activeCategory == null || w.getCategory() == activeCategory;
                return keyOk && catOk;
            })
            .toList();

        renderCards(filtered);
    }

    // ── Category pills ─────────────────────────────────────────────────────

    private void buildCategoryPills() {
        filterContainer.getChildren().clear();

        // "All" pill
        Button allBtn = pillButton("All", null, activeCategory == null);
        filterContainer.getChildren().add(allBtn);

        // One pill per category
        for (WorkoutCategory cat : WorkoutCategory.values()) {
            Button btn = pillButton(getCategoryLabel(cat), cat, cat == activeCategory);
            filterContainer.getChildren().add(btn);
        }
    }

    private Button pillButton(String label, WorkoutCategory cat, boolean active) {
        Button btn = new Button(label);
        applyPillStyle(btn, cat, active);
        btn.setOnAction(e -> {
            // Clicking the active pill resets to "All"
            activeCategory = (activeCategory == cat) ? null : cat;
            buildCategoryPills();
            applyFilter();
        });
        return btn;
    }

    private static void applyPillStyle(Button btn, WorkoutCategory cat, boolean active) {
        String accent = getCategoryColor(cat);
        if (active) {
            btn.setStyle(
                "-fx-background-color: " + accent + "; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-font-size: 12px;" +
                "-fx-padding: 8 18; -fx-background-radius: 20; -fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #64748b;" +
                "-fx-border-color: #bae6fd; -fx-border-width: 1.5;" +
                "-fx-font-size: 12px; -fx-padding: 8 18;" +
                "-fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;"
            );
        }
    }

    // ── Card rendering ─────────────────────────────────────────────────────

    private void renderCards(List<Workout> workouts) {
        cardContainer.getChildren().clear();

        if (workouts.isEmpty()) {
            VBox empty = new VBox(10);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(48));
            Label icon  = new Label("🔍");
            icon.setStyle("-fx-font-size: 40px;");
            Label msg   = new Label("No workouts match your search.");
            msg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 15px;");
            Button clear = new Button("Clear Filters");
            clear.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #3b82f6;" +
                "-fx-border-color: #bae6fd; -fx-border-width: 2;" +
                "-fx-padding: 8 16; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;"
            );
            clear.setOnAction(e -> {
                searchField.clear();
                activeCategory = null;
                buildCategoryPills();
                renderCards(allWorkouts);
            });
            empty.getChildren().addAll(icon, msg, clear);
            cardContainer.getChildren().add(empty);
            return;
        }

        for (Workout w : workouts) {
            cardContainer.getChildren().add(buildCard(w));
        }
    }


    private VBox buildCard(Workout workout) {
        VBox card = new VBox(10);
        card.setPrefWidth(270);
        card.setMaxWidth(270);
        card.setPadding(new Insets(20));

        String baseStyle =
            "-fx-background-color: white; -fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 15, 0, 0, 5);";
        card.setStyle(baseStyle + " -fx-cursor: " + (workout.isLocked() ? "default" : "hand") + ";");

        if (workout.isLocked()) card.setOpacity(0.55);

        // ── Icon circle ────────────────────────────────────────────────────
        String catColor = getCategoryColor(workout.getCategory());
        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(52, 52);
        iconPane.setMinSize(52, 52);
        iconPane.setStyle("-fx-background-color: " + catColor + "22; -fx-background-radius: 12;");
        Label iconLbl = new Label(workout.isLocked() ? "🔒" : "💪");
        iconLbl.setStyle("-fx-font-size: 22px;");
        iconPane.getChildren().add(iconLbl);

        // ── Title ──────────────────────────────────────────────────────────
        Label title = new Label(workout.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e3a5f;");
        title.setWrapText(true);

        // ── Badge row: difficulty + duration + category ────────────────────
        // Use FlowPane(horizontalGap, verticalGap) to allow wrapping
                FlowPane badges = new FlowPane(7, 5);
                badges.setAlignment(Pos.CENTER_LEFT);

        // setPrefWrapLength defines the width at which the content should wrap
                badges.setPrefWrapLength(230);

                String diffColor = getDiffColor(workout.getDifficulty());
                Label diffBadge = makeBadge(workout.getDifficultyLabel(), diffColor, "white");

                Label durLabel = new Label("⏱ " + workout.getDuration());
                durLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11.5px;");

                badges.getChildren().addAll(diffBadge, durLabel);

                if (workout.getCategory() != null) {
                    Label catBadge = makeBadge(
                            getCategoryLabel(workout.getCategory()),
                            getCategoryColor(workout.getCategory()) + "20",
                            getCategoryColor(workout.getCategory())
                    );
                    badges.getChildren().add(catBadge);
                }

        // ── Description snippet (2-line preview, unlocked only) ────────────
        card.getChildren().addAll(iconPane, title, badges);
        if (workout.getDescription() != null && !workout.getDescription().isBlank()
                && !workout.isLocked()) {
            Label desc = new Label(workout.getDescription());
            desc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11.5px;");
            desc.setWrapText(true);
            desc.setMaxHeight(30);
            card.getChildren().add(desc);
        }

        // ── Hover + click (unlocked cards only) ───────────────────────────
        if (!workout.isLocked()) {
            card.setOnMouseClicked(e -> showWorkoutDetail(workout));
            card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 20, 0, 0, 6);" +
                "-fx-cursor: hand; -fx-translate-y: -2;"
            ));
            card.setOnMouseExited(e -> card.setStyle(baseStyle + " -fx-cursor: hand;"));
        }

        return card;
    }

    // ── Workout detail dialog ──────────────────────────────────────────────


    private void showWorkoutDetail(Workout workout) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(workout.getTitle());
        dialog.setHeaderText(null);

        VBox content = new VBox(14);
        content.setPadding(new Insets(20));
        content.setPrefWidth(440);

        // Title
        Label titleLbl = new Label(workout.getTitle());
        titleLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");

        // Meta row
        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);
        meta.getChildren().add(makeBadge(workout.getDifficultyLabel(), getDiffColor(workout.getDifficulty()), "white"));
        if (workout.getCategory() != null) {
            String c = getCategoryColor(workout.getCategory());
            meta.getChildren().add(makeBadge(getCategoryLabel(workout.getCategory()), c, "white"));
        }
        Label dur = new Label("⏱ " + workout.getDuration());
        dur.setStyle("-fx-text-fill: #64748b;");
        meta.getChildren().add(dur);

        content.getChildren().addAll(titleLbl, meta);

        // Description
        if (workout.getDescription() != null && !workout.getDescription().isBlank()) {
            VBox descBox = new VBox();
            descBox.setPadding(new Insets(10, 12, 10, 12));
            descBox.setStyle(
                "-fx-background-color: #f0f8ff; -fx-background-radius: 10;" +
                "-fx-border-color: #bae6fd; -fx-border-width: 1; -fx-border-radius: 10;"
            );
            Label desc = new Label(workout.getDescription());
            desc.setWrapText(true);
            desc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
            descBox.getChildren().add(desc);
            content.getChildren().add(descBox);
        }

        // Exercise list
        if (workout.getExercises() != null && !workout.getExercises().isEmpty()) {
            Label exHeader = new Label("Exercises (" + workout.getExercises().size() + ")");
            exHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e3a5f;");
            content.getChildren().add(exHeader);

            for (int i = 0; i < workout.getExercises().size(); i++) {
                Exercise ex = workout.getExercises().get(i);
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                row.setStyle(
                    "-fx-background-color: #f8fafc; -fx-background-radius: 10;" +
                    "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 10;"
                );

                // Step number
                Label numLbl = new Label(String.valueOf(i + 1));
                numLbl.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                StackPane numCircle = new StackPane(numLbl);
                numCircle.setPrefSize(28, 28);
                numCircle.setMinSize(28, 28);
                numCircle.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #bae6fd; -fx-border-width: 1.5;");

                Label emojiLbl = new Label(ex.getEmoji());
                emojiLbl.setStyle("-fx-font-size: 18px;");

                VBox info = new VBox(2);
                Label nameL = new Label(ex.getName());
                nameL.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3a5f;");
                Label detail = new Label(ex.getSets() + " sets × " + ex.getReps() + " reps");
                detail.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
                info.getChildren().addAll(nameL, detail);

                row.getChildren().addAll(numCircle, emojiLbl, info);
                content.getChildren().add(row);
            }
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(460);
        scroll.setStyle("-fx-background-color: white; -fx-background: white;");

        dialog.getDialogPane().setContent(scroll);
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // ── FXML navigation actions ────────────────────────────────────────────

    @FXML
    private void handleCreateCustom() {
        MainApp.instance.changeScene(
            "/com/oop/gymquest/fxml/custom-workout-creator-view.fxml",
            "GymQuest - Create Custom Workout"
        );
    }

    @FXML
    public static void handleAction() {
        MainApp.instance.changeScene(
            "/com/oop/gymquest/fxml/workouts.fxml",
            "GymQuest - Workout Library"
        );
    }

    // ── Style / label helpers ──────────────────────────────────────────────

    private static Label makeBadge(String text, String bg, String fg) {
        Label l = new Label(text);
        l.setPadding(new Insets(3, 10, 3, 10));
        l.setStyle(
            "-fx-background-color: " + bg + "; -fx-text-fill: " + fg + ";" +
            "-fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;"
        );
        return l;
    }

    public static String getCategoryLabel(WorkoutCategory cat) {
        if (cat == null) return "All";
        return switch (cat) {
            case STRENGTH    -> "Strength";
            case CARDIO      -> "Cardio";
            case CORE        -> "Core";
            case FLEXIBILITY -> "Flexibility";
            case HIIT        -> "HIIT";
            case BALANCE     -> "Balance";
        };
    }

    public static String getCategoryColor(WorkoutCategory cat) {
        if (cat == null) return "#3b82f6";
        return switch (cat) {
            case STRENGTH    -> "#3b82f6";
            case CARDIO      -> "#ef4444";
            case CORE        -> "#6366f1";
            case FLEXIBILITY -> "#10b981";
            case HIIT        -> "#f97316";
            case BALANCE     -> "#0d9488";
        };
    }

    private static String getDiffColor(Workout.Difficulty diff) {
        return switch (diff) {
            case BEGINNER     -> "#3b82f6";
            case INTERMEDIATE -> "#d97706";
            case ADVANCED     -> "#ef4444";
        };
    }
}
