package com.oop.gymquest.screens.exercisePicker;

import com.oop.gymquest.data.workoutdata.Exercise;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ExercisePickerDialog extends Dialog<Exercise> {

    private static final String NAVY           = "#1e3a5f";
    private static final String SLATE          = "#64748b";
    private static final String BORDER         = "#bae6fd";
    private static final String CARD_BG        = "#f0f8ff";
    private static final String WHITE          = "white";
    private static final String SELECTED_BORDER = "#3b82f6";

    private final List<Exercise> allExercises;
    private VBox selectedCard = null;

    private final TextField searchField   = new TextField();
    private final HBox      filterRow     = new HBox(8);
    private final FlowPane  grid          = new FlowPane(14, 14);
    private String          activeCategory = null;

    private Button addBtnRef;

    // ── Constructor ───────────────────────────────────────────────────────

    public ExercisePickerDialog(List<Exercise> exercises) {
        this.allExercises = exercises;
        setTitle("Exercise Library");
        setHeaderText(null);
        buildContent();
        buildButtons();

        // Extract customized values from the selected card's text fields
        setResultConverter(bt -> {
            if (bt == ButtonType.OK && selectedCard != null) {
                Exercise base = (Exercise) selectedCard.getProperties().get("exercise");
                TextField setsField = (TextField) selectedCard.getProperties().get("setsField");
                TextField repsField = (TextField) selectedCard.getProperties().get("repsField");

                int customSets = base.getSets();
                try {
                    customSets = Integer.parseInt(setsField.getText().trim());
                } catch (NumberFormatException ignored) {}

                String customReps = repsField.getText().trim();
                if (customReps.isEmpty()) customReps = base.getReps();

                // Return a new clone of the exercise with customized sets/reps
                return new Exercise(base.getId(), base.getName(), customSets, customReps, base.getCategory());
            }
            return null;
        });
    }

    // ── UI construction ────────────────────────────────────────────────────

    private void buildContent() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(20));
        root.setPrefWidth(720);

        searchField.setPromptText("🔍  Search exercises…");
        searchField.setStyle(
                "-fx-padding: 11 16; -fx-border-color: " + BORDER + "; -fx-border-width: 2;" +
                        "-fx-background-radius: 14; -fx-border-radius: 14; -fx-font-size: 13px;" +
                        "-fx-background-color: " + WHITE + "; -fx-text-fill: " + NAVY + ";"
        );
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.textProperty().addListener((obs, o, n) -> refreshGrid());

        filterRow.setAlignment(Pos.CENTER_LEFT);
        buildFilterPills();

        grid.setAlignment(Pos.TOP_LEFT);
        grid.setPrefWrapLength(680);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(420);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent;" +
                        "-fx-border-color: transparent;"
        );
        scroll.getStyleClass().add("modern-scroll");

        root.getChildren().addAll(searchField, filterRow, scroll);
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
                "-fx-background-color: #3b82f6; -fx-text-fill: white;" +
                        "-fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 10 24; -fx-cursor: hand;"
        );
        okNode.setDisable(true);

        Button cancelNode = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelNode.setText("Cancel");
        cancelNode.setStyle(
                "-fx-background-color: #f1f5f9; -fx-text-fill: " + SLATE + ";" +
                        "-fx-background-radius: 12; -fx-padding: 10 24; -fx-cursor: hand;"
        );

        addBtnRef = okNode;
    }

    // ── Filter pills ───────────────────────────────────────────────────────

    private void buildFilterPills() {
        filterRow.getChildren().clear();
        filterRow.getChildren().add(filterPill("All", null));

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
            activeCategory = (activeCategory != null && activeCategory.equals(category))
                    ? null : category;
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

    // ── Grid ──────────────────────────────────────────────────────────────

    private void refreshGrid() {
        grid.getChildren().clear();
        selectedCard = null;
        if (addBtnRef != null) addBtnRef.setDisable(true);

        final String kw   = searchField.getText().toLowerCase();
        final boolean hasKw = !kw.isBlank();

        List<Exercise> visible = allExercises.stream()
                .filter(ex -> {
                    boolean nameOk = !hasKw || ex.getName().toLowerCase().contains(kw);
                    boolean catOk  = activeCategory == null
                            || (ex.getCategory() != null
                            && ex.getCategory().equalsIgnoreCase(activeCategory));
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


    private VBox buildExerciseCard(Exercise exercise) {
        VBox card = new VBox(10);
        card.setPrefWidth(215);
        card.setMaxWidth(215);
        card.setPrefHeight(135);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.TOP_LEFT);

        // Store reference objects for easy retrieval when saving
        card.getProperties().put("exercise", exercise);

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
                        "-fx-border-color: " + SELECTED_BORDER + "; -fx-border-width: 2.5;" +
                        "-fx-border-radius: 14; -fx-cursor: hand;";

        card.setStyle(defaultStyle);

        // ── Typography Based Category Pill ───────────────────────────────
        Label catBadge = new Label((exercise.getCategory() != null ? exercise.getCategory() : "GENERAL").toUpperCase());
        catBadge.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SLATE + ";" +
                        "-fx-background-color: #e2e8f0; -fx-padding: 3 8; -fx-background-radius: 6;"
        );

        // ── Name ─────────────────────────────────────────────────────────
        Label nameLbl = new Label(exercise.getName());
        nameLbl.setStyle(
                "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";"
        );
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(190);

        // ── Inline Configuration (Sets / Reps Inputs) ────────────────────
        String inputStyle = "-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; " +
                "-fx-background-radius: 6; -fx-padding: 3 6; -fx-font-size: 12px; -fx-text-fill: " + NAVY + ";";

        String labelStyle = "-fx-font-size: 12px; -fx-text-fill: " + SLATE + "; -fx-font-weight: bold;";

        TextField setsField = new TextField(String.valueOf(exercise.getSets()));
        setsField.setPrefWidth(45);
        setsField.setStyle(inputStyle);

        TextField repsField = new TextField(exercise.getReps());
        repsField.setPrefWidth(75);
        repsField.setStyle(inputStyle);

        card.getProperties().put("setsField", setsField);
        card.getProperties().put("repsField", repsField);

        Label setsLabel = new Label("Sets:");
        setsLabel.setStyle(labelStyle);
        HBox setsBox = new HBox(6, setsLabel, setsField);
        setsBox.setAlignment(Pos.CENTER_LEFT);

        Label repsLabel = new Label("Reps / Time:");
        repsLabel.setStyle(labelStyle);
        HBox repsBox = new HBox(6, repsLabel, repsField);
        repsBox.setAlignment(Pos.CENTER_LEFT);

        HBox configRow = new HBox(12, setsBox, repsBox);
        configRow.setAlignment(Pos.CENTER_LEFT);
        configRow.setPadding(new Insets(4, 0, 0, 0));

        card.getChildren().addAll(catBadge, nameLbl, configRow);

        // ── Hover & Selection Logic ──────────────────────────────────────
        Runnable selectThisCard = () -> {
            grid.getChildren().forEach(node -> {
                if (node instanceof VBox vc) vc.setStyle(defaultStyle);
            });
            selectedCard = card;
            card.setStyle(selectedStyle);
            if (addBtnRef != null) addBtnRef.setDisable(false);
        };

        card.setOnMouseEntered(e -> {
            if (selectedCard != card) card.setStyle(hoverStyle);
        });
        card.setOnMouseExited(e -> {
            if (selectedCard != card) card.setStyle(defaultStyle);
        });

        // Clicking the card or interacting with the textfields selects the card
        card.setOnMouseClicked(e -> {
            selectThisCard.run();
            if (e.getClickCount() == 2 && addBtnRef != null) addBtnRef.fire();
        });

        setsField.setOnMouseClicked(e -> selectThisCard.run());
        repsField.setOnMouseClicked(e -> selectThisCard.run());
        setsField.focusedProperty().addListener((obs, oldVal, newVal) -> { if (newVal) selectThisCard.run(); });
        repsField.focusedProperty().addListener((obs, oldVal, newVal) -> { if (newVal) selectThisCard.run(); });

        return card;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}