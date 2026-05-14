package com.oop.gymquest.screens.exercisePicker;

import com.oop.gymquest.data.workoutdata.Exercise;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;


public class ExercisePickerDialog extends Dialog<Exercise> {

    // ── Color palette (mirrors WorkoutsViewController) ────────────────────
    private static final String NAVY      = "#1e3a5f";
    private static final String SLATE     = "#64748b";
    private static final String BORDER    = "#bae6fd";
    private static final String CARD_BG   = "#f0f8ff";
    private static final String WHITE     = "white";
    private static final String SELECTED_BORDER = "#3b82f6";

    // ── State ──────────────────────────────────────────────────────────────
    private final List<Exercise> allExercises;
    private Exercise selectedExercise = null;

    // Live UI references kept so search + filter can re-render
    private final TextField  searchField   = new TextField();
    private final HBox       filterRow     = new HBox(8);
    private final FlowPane   grid          = new FlowPane(14, 14);
    private String           activeCategory = null;  // null = "All"

    // Buttons
    private final Button addBtn    = new Button("Add to Workout");
    private final Button cancelBtn = new Button("Cancel");

    // ── Constructor ───────────────────────────────────────────────────────

    public ExercisePickerDialog(List<Exercise> exercises) {
        this.allExercises = exercises;

        setTitle("Exercise Library");
        setHeaderText(null);

        buildContent();
        buildButtons();

        // Return the selected exercise when "Add to Workout" is clicked
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) return selectedExercise;
            return null;
        });
    }

    // ── UI construction ────────────────────────────────────────────────────

    private void buildContent() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(20));
        root.setPrefWidth(700);

        // ── Search bar ─────────────────────────────────────────────────────
        searchField.setPromptText("🔍  Search exercises…");
        searchField.setStyle(
            "-fx-padding: 11 16; -fx-border-color: " + BORDER + "; -fx-border-width: 2;" +
            "-fx-background-radius: 14; -fx-border-radius: 14; -fx-font-size: 13px;" +
            "-fx-background-color: " + WHITE + "; -fx-text-fill: " + NAVY + ";"
        );
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.textProperty().addListener((obs, o, n) -> refreshGrid());

        // ── Category filter pills ──────────────────────────────────────────
        filterRow.setAlignment(Pos.CENTER_LEFT);
        buildFilterPills();

        // ── Exercise grid ──────────────────────────────────────────────────
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setPrefWrapLength(660);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(380);
        scroll.setStyle(
            "-fx-background-color: transparent; -fx-background: transparent;" +
            "-fx-border-color: transparent;"
        );
        scroll.getStyleClass().add("modern-scroll");

        root.getChildren().addAll(searchField, filterRow, scroll);

        getDialogPane().setContent(root);
        getDialogPane().setStyle("-fx-background-color: white;");
        getDialogPane().setPrefWidth(740);

        refreshGrid();
    }

    private void buildButtons() {
        // Use CANCEL and OK so setResultConverter can distinguish them
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Grab the real Button nodes from the dialog pane and restyle them
        Button okNode = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okNode.setText("Add to Workout");
        okNode.setStyle(
            "-fx-background-color: #3b82f6; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 10 24; -fx-cursor: hand;"
        );
        // Disable until an exercise is selected
        okNode.setDisable(true);

        Button cancelNode = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelNode.setText("Cancel");
        cancelNode.setStyle(
            "-fx-background-color: #f1f5f9; -fx-text-fill: " + SLATE + ";" +
            "-fx-background-radius: 12; -fx-padding: 10 24; -fx-cursor: hand;"
        );

        // Expose okNode reference so card clicks can enable it
        addBtn.setDisable(true);  // internal tracking mirror (unused, but kept for clarity)
        addBtnRef = okNode;
    }

    /** Stored so card clicks can enable/disable the "Add to Workout" button. */
    private Button addBtnRef;

    // ── Filter pills ───────────────────────────────────────────────────────

    private void buildFilterPills() {
        filterRow.getChildren().clear();
        filterRow.getChildren().add(filterPill("All", null));

        // Collect distinct categories from the exercise list
        allExercises.stream()
            .map(Exercise::getCategory)
            .filter(c -> c != null && !c.isBlank())
            .distinct()
            .sorted()
            .forEach(cat -> filterRow.getChildren().add(filterPill(capitalize(cat), cat)));
    }

    private Button filterPill(String label, String category) {
        boolean active = (activeCategory == null && category == null)
                      || (activeCategory != null && activeCategory.equals(category));
        Button btn = new Button(label);
        applyPillStyle(btn, active);
        btn.setOnAction(e -> {
            activeCategory = (activeCategory != null && activeCategory.equals(category)) ? null : category;
            buildFilterPills();
            refreshGrid();
        });
        return btn;
    }

    private static void applyPillStyle(Button btn, boolean active) {
        if (active) {
            btn.setStyle(
                "-fx-background-color: #1e3a5f; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-font-size: 12px;" +
                "-fx-padding: 7 16; -fx-background-radius: 20; -fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: white; -fx-text-fill: " + SLATE + ";" +
                "-fx-border-color: " + BORDER + "; -fx-border-width: 1.5;" +
                "-fx-font-size: 12px; -fx-padding: 7 16;" +
                "-fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;"
            );
        }
    }

    // ── Grid rendering ─────────────────────────────────────────────────────

    private void refreshGrid() {
        grid.getChildren().clear();
        selectedExercise = null;
        if (addBtnRef != null) addBtnRef.setDisable(true);

        final String kw = searchField.getText().toLowerCase();
        final boolean hasKw = !kw.isBlank();

        List<Exercise> visible = allExercises.stream()
            .filter(ex -> {
                boolean nameOk = !hasKw || ex.getName().toLowerCase().contains(kw);
                boolean catOk  = activeCategory == null
                    || (ex.getCategory() != null && ex.getCategory().equalsIgnoreCase(activeCategory));
                return nameOk && catOk;
            })
            .toList();

        if (visible.isEmpty()) {
            Label empty = new Label("No exercises match your search.");
            empty.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 14px; -fx-padding: 20;");
            grid.getChildren().add(empty);
            return;
        }

        for (Exercise ex : visible) {
            grid.getChildren().add(buildExerciseCard(ex));
        }
    }

    /**
     * Builds one exercise card.
     *
     * ── Text color fix ────────────────────────────────────────────────────
     *  The previous version left text color unset, so JavaFX used the global
     *  `.label { -fx-text-fill: black; }` style — but the stylesheet override
     *  for light-blue backgrounds was forcing white.  Now every Label has an
     *  explicit dark text color so the result is predictable regardless of CSS.
     */
    private VBox buildExerciseCard(Exercise exercise) {
        VBox card = new VBox(8);
        card.setPrefWidth(195);
        card.setMaxWidth(195);
        card.setPrefHeight(110);
        card.setPadding(new Insets(14));
        card.setAlignment(Pos.TOP_LEFT);
        card.setUserData(exercise);

        final String defaultStyle =
            "-fx-background-color: " + CARD_BG + "; -fx-background-radius: 14;" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 2; -fx-border-radius: 14;" +
            "-fx-cursor: hand;";
        final String hoverStyle =
            "-fx-background-color: #e0f2fe; -fx-background-radius: 14;" +
            "-fx-border-color: #7dd3fc; -fx-border-width: 2; -fx-border-radius: 14;" +
            "-fx-cursor: hand;";
        final String selectedStyle =
            "-fx-background-color: #eff6ff; -fx-background-radius: 14;" +
            "-fx-border-color: " + SELECTED_BORDER + "; -fx-border-width: 2.5; -fx-border-radius: 14;" +
            "-fx-cursor: hand;";

        card.setStyle(defaultStyle);

        // ── Emoji ──────────────────────────────────────────────────────────
        // Inside the card creation logic:
        Label emojiLbl = new Label(exercise.getEmoji());
        emojiLbl.setPrefSize(50, 50);
        emojiLbl.setAlignment(Pos.CENTER);

        // Dynamic styling based on exercise type (if available) or a default clean look
        emojiLbl.setStyle(
                "-fx-background-color: #f1f5f9;" + // Light slate background
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 24px;" +
                        "-fx-text-fill: black;" // Emojis should usually stay 'black' to render native colors
        );

        // ── Name (DARK text — the main fix) ───────────────────────────────
        Label nameLbl = new Label(exercise.getName());
        nameLbl.setStyle(
            "-fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-text-fill: " + NAVY + ";"          // ← was implicitly white
        );
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(170);

        // ── Sets × reps (SLATE grey — readable on light blue) ─────────────
        Label detailLbl = new Label(exercise.getSets() + " sets × " + exercise.getReps());
        detailLbl.setStyle(
            "-fx-font-size: 11.5px;" +
            "-fx-text-fill: " + SLATE + ";"         // ← was implicitly white
        );

        card.getChildren().addAll(emojiLbl, nameLbl, detailLbl);

        // ── Hover & selection ──────────────────────────────────────────────
        card.setOnMouseEntered(e -> {
            if (selectedExercise != exercise) card.setStyle(hoverStyle);
        });
        card.setOnMouseExited(e -> {
            if (selectedExercise != exercise) card.setStyle(defaultStyle);
        });
        card.setOnMouseClicked(e -> {
            // Deselect previous card visually
            grid.getChildren().forEach(node -> {
                if (node instanceof VBox vc && vc.getUserData() instanceof Exercise) {
                    vc.setStyle(defaultStyle);
                }
            });

            // Select this card
            selectedExercise = exercise;
            card.setStyle(selectedStyle);
            if (addBtnRef != null) addBtnRef.setDisable(false);

            // Double-click = confirm immediately
            if (e.getClickCount() == 2 && addBtnRef != null) {
                addBtnRef.fire();
            }
        });

        return card;
    }

    // ── Utility ────────────────────────────────────────────────────────────

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
