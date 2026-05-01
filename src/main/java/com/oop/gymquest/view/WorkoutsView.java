package com.oop.gymquest.view;

import com.oop.gymquest.controller.MainController;
import com.oop.gymquest.model.*;
import com.oop.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.List;

public class WorkoutsView {

    private final ScrollPane root;

    // ── State tracked for dynamic filtering ───────────────────────────────
    private final WorkoutService service  = WorkoutService.getInstance();
    private final AppState       state;
    private final MainController controller;

    private final TextField searchField;
    private final GridPane       grid;
    private WorkoutCategory activeCategory = null;   // null = "All"

    // ── Category pill button references (for active-state toggle) ─────────
    private final Button[] categoryBtns;

    public WorkoutsView(AppState state, MainController controller) {
        this.state      = state;
        this.controller = controller;

        // ── Outer scroll pane ─────────────────────────────────────────────
        VBox content = new VBox(24);
        content.setPadding(new Insets(24));

        // ── Header card ───────────────────────────────────────────────────
        VBox headerCard = UIHelper.card();

        // Title row
        HBox titleRow = new HBox(16);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
                UIHelper.title("All Workouts"),
                UIHelper.subtitle("Choose your challenge and level up!")
        );

        Button customBtn = UIHelper.amberBtn("+ Create Custom");
        customBtn.setOnAction(e -> controller.openCustomWorkoutCreator());

        Label mascot = new Label("🏋️");
        mascot.setStyle("-fx-font-size: 48px;");

        titleRow.getChildren().addAll(titleBox, UIHelper.spacer(), customBtn, mascot);

        // ── Search bar ────────────────────────────────────────────────────
        searchField = new TextField();
        searchField.setPromptText("🔍  Search workouts by name or keyword…");
        searchField.setStyle(
                "-fx-padding: 11 16; -fx-border-color: #bae6fd; -fx-border-width: 2;" +
                        "-fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: 13px;" +
                        "-fx-background-color: white;"
        );
        searchField.setMaxWidth(Double.MAX_VALUE);
        // Live search: rebuild the grid on every keystroke
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshGrid());

        // ── Category filter pills ─────────────────────────────────────────
        HBox filterRow = new HBox(8);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterRow.setPadding(new Insets(4, 0, 0, 0));

        WorkoutCategory[] categories = WorkoutCategory.values();
        // +1 for the "All" button
        categoryBtns = new Button[categories.length + 1];

        // "All" pill (index 0)
        categoryBtns[0] = filterPill("All", null, true);
        filterRow.getChildren().add(categoryBtns[0]);

        // One pill per category
        for (int i = 0; i < categories.length; i++) {
            final WorkoutCategory cat = categories[i];
            categoryBtns[i + 1] = filterPill(cat.getLabel(), cat, false);
            filterRow.getChildren().add(categoryBtns[i + 1]);
        }

        headerCard.getChildren().addAll(titleRow, searchField, filterRow);
        content.getChildren().add(headerCard);

        // ── Workout grid ──────────────────────────────────────────────────
        grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        // Fix: ColumnConstraints are required for HGrow to work in GridPane
        for (int col = 0; col < 3; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            cc.setPercentWidth(33.33);
            grid.getColumnConstraints().add(cc);
        }

        content.getChildren().add(grid);

        // Initial population
        refreshGrid();

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    // ── Grid refresh ──────────────────────────────────────────────────────

    /**
     * Clears and re-populates the grid using the current search keyword and
     * active category filter.  Called on every keystroke and every category
     * pill click.
     */
    private void refreshGrid() {
        grid.getChildren().clear();

        List<Workout> results = service.searchAndFilter(searchField.getText(), activeCategory);

        if (results.isEmpty()) {
            // Empty state
            VBox empty = new VBox(12);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(48));
            Label emptyIcon = new Label("🔍");
            emptyIcon.setStyle("-fx-font-size: 48px;");
            Label emptyMsg = UIHelper.subtitle("No workouts match your search.");
            Button clearBtn = UIHelper.ghostBtn("Clear Filters");
            clearBtn.setOnAction(e -> {
                searchField.clear();
                setActiveCategory(null);
            });
            empty.getChildren().addAll(emptyIcon, emptyMsg, clearBtn);
            grid.add(empty, 0, 0, 3, 1);   // span all 3 columns
            return;
        }

        for (int i = 0; i < results.size(); i++) {
            final Workout w = results.get(i);
            WorkoutCardView card = new WorkoutCardView(
                    w, state.getCompletedWorkouts().contains(w.getId())
            );
            card.getRoot().setMaxWidth(Double.MAX_VALUE);
            card.setOnAction(() -> {
                if (!w.isLocked()) controller.openWorkoutDetail(w);
            });
            grid.add(card.getRoot(), i % 3, i / 3);
        }
    }

    // ── Filter pill factory ───────────────────────────────────────────────

    /**
     * Creates a styled toggle pill for the category filter row.
     *
     * @param label    display text
     * @param category the {@link WorkoutCategory} this pill represents
     *                 ({@code null} means "All")
     * @param active   whether this pill starts in the active/selected state
     */
    private Button filterPill(String label, WorkoutCategory category, boolean active) {
        Button btn = new Button(label);
        applyPillStyle(btn, category, active);
        btn.setOnAction(e -> {
            // Clicking the already-active pill resets to "All"
            if (activeCategory == category) {
                setActiveCategory(null);
            } else {
                setActiveCategory(category);
            }
        });
        return btn;
    }

    /**
     * Activates the pill for {@code category} and deactivates all others,
     * then triggers a grid refresh.
     */
    private void setActiveCategory(WorkoutCategory category) {
        activeCategory = category;

        WorkoutCategory[] cats = WorkoutCategory.values();
        // Index 0 = "All", index 1..N = categories
        applyPillStyle(categoryBtns[0], null, category == null);
        for (int i = 0; i < cats.length; i++) {
            applyPillStyle(categoryBtns[i + 1], cats[i], cats[i] == category);
        }

        refreshGrid();
    }

    /**
     * Applies active or inactive styling to a pill button.
     * Active pills use the category's own accent color; inactive pills are
     * outlined in the global border blue.
     */
    private static void applyPillStyle(Button btn, WorkoutCategory category, boolean active) {
        String accent = (category != null) ? category.getColor() : "#3b82f6";
        if (active) {
            btn.setStyle(
                    "-fx-background-color: " + accent + "; -fx-text-fill: white;" +
                            "-fx-font-weight: bold; -fx-font-size: 12px;" +
                            "-fx-padding: 6 16; -fx-background-radius: 20; -fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, " + accent + "55, 6, 0, 0, 2);"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #64748b;" +
                            "-fx-border-color: #bae6fd; -fx-border-width: 1.5;" +
                            "-fx-font-size: 12px;" +
                            "-fx-padding: 6 16; -fx-background-radius: 20; -fx-border-radius: 20;" +
                            "-fx-cursor: hand;"
            );
        }
    }

    public ScrollPane getRoot() { return root; }
}
