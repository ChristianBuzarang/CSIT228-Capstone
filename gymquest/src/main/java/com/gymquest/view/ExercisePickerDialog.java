package com.gymquest.view;

import com.gymquest.model.Exercise;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ExercisePickerDialog extends Dialog<Exercise> {

    private Exercise selectedExercise;
    private final List<Exercise> library;

    // UI Components defined as fields to avoid initialization errors in lambdas
    private final TextField search = new TextField();
    private final ToggleGroup tg = new ToggleGroup();
    private final FlowPane grid = new FlowPane(10, 10);
    private Button addBtn;

    public ExercisePickerDialog(List<Exercise> library) {
        this.library = library;
        setTitle("Exercise Library");
        setHeaderText(null);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setPrefWidth(600);

        // --- Search Bar ---
        search.setPromptText("🔍  Search exercises...");
        search.setStyle("-fx-padding: 10 14; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");
        search.textProperty().addListener((obs, o, n) -> refreshGrid());

        // --- Category Filters ---
        HBox catRow = new HBox(8);
        String[] cats = {"All", "Strength", "Cardio", "Core", "Flexibility"};
        for (String cat : cats) {
            ToggleButton btn = new ToggleButton(cat);
            btn.setToggleGroup(tg);
            btn.setUserData(cat.toLowerCase());
            btn.setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #64748b; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 6 14;");
            if (cat.equals("All")) btn.setSelected(true);
            catRow.getChildren().add(btn);
        }
        tg.selectedToggleProperty().addListener((obs, old, nw) -> {
            if (nw == null) {
                tg.selectToggle(old); // Prevent deselecting everything
            } else {
                refreshGrid();
            }
        });

        // --- Exercise Grid & ScrollPane ---
        grid.setPrefWrapLength(560);
        ScrollPane gridScroll = new ScrollPane(grid);
        gridScroll.setFitToWidth(true);
        gridScroll.setPrefHeight(350);
        gridScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // --- Dialog Buttons ---
        ButtonType addType = new ButtonType("Add to Workout", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        addBtn = (Button) getDialogPane().lookupButton(addType);
        addBtn.setDisable(true);
        addBtn.setStyle("-fx-background-color: linear-gradient(to right, #3b82f6, #60a5fa); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        setResultConverter(bt -> (bt == addType) ? selectedExercise : null);

        // Initial Layout and First Load
        content.getChildren().addAll(search, catRow, gridScroll);
        getDialogPane().setContent(content);

        refreshGrid();
    }

    /**
     * Filters and rebuilds the exercise cards based on search and category.
     */
    private void refreshGrid() {
        grid.getChildren().clear();
        String filterText = search.getText().toLowerCase();
        String catFilter = (tg.getSelectedToggle() != null)
                ? (String) tg.getSelectedToggle().getUserData()
                : "all";

        for (Exercise ex : library) {
            boolean matchSearch = ex.getName().toLowerCase().contains(filterText);
            boolean matchCat = catFilter.equals("all") || ex.getCategory().equalsIgnoreCase(catFilter);

            if (matchSearch && matchCat) {
                grid.getChildren().add(buildExCard(ex));
            }
        }
    }

    /**
     * Creates an individual exercise card.
     */
    private VBox buildExCard(Exercise ex) {
        VBox card = new VBox(6);
        card.setPrefWidth(160);
        card.setPadding(new Insets(12));
        card.setAlignment(Pos.CENTER_LEFT);

        // Default Style
        String defaultStyle = "-fx-background-color: #f0f8ff; -fx-background-radius: 12; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-cursor: hand;";
        String selectedStyle = "-fx-background-color: #e0f2fe; -fx-background-radius: 12; -fx-border-color: #3b82f6; -fx-border-width: 2; -fx-cursor: hand;";

        card.setStyle(defaultStyle);

        Label emoji = new Label(ex.getEmoji());
        emoji.setStyle("-fx-font-size: 28px;");

        Label name = new Label(ex.getName());
        name.setStyle("-fx-text-fill: #1e3a5f; -fx-font-weight: bold; -fx-font-size: 12px;");
        name.setWrapText(true);

        Label detail = new Label(ex.getSets() + " sets × " + ex.getReps());
        detail.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        card.getChildren().addAll(emoji, name, detail);

        card.setOnMouseClicked(e -> {
            selectedExercise = ex;

            // Visual feedback: Reset all cards, then highlight this one
            grid.getChildren().forEach(node -> node.setStyle(defaultStyle));
            card.setStyle(selectedStyle);

            // Enable the dialog action button
            if (addBtn != null) addBtn.setDisable(false);
        });

        return card;
    }
}