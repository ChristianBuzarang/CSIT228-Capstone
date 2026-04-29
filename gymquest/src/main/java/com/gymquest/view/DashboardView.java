package com.gymquest.view;

import com.gymquest.controller.MainController;
import com.gymquest.model.AppState;
import com.gymquest.model.DataStore;
import com.gymquest.model.Workout;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

public class DashboardView {

    private final ScrollPane root;

    public DashboardView(AppState state, MainController controller) {
        HBox content = new HBox(24);
        content.setPadding(new Insets(24));

        VBox left = new VBox(20);
        left.setPrefWidth(700);
        HBox.setHgrow(left, Priority.ALWAYS);

        // Welcome card
        VBox welcomeCard = UIHelper.card();
        HBox welcomeHeader = new HBox(16);
        welcomeHeader.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeText = new VBox(6);
        Label welcomeTitle = UIHelper.title("Welcome back, Champion! 🎯");
        Label welcomeSub = UIHelper.subtitle("Ready to crush your goals today?");
        welcomeText.getChildren().addAll(welcomeTitle, welcomeSub);

        Label mascot = new Label("🏋️");
        mascot.setStyle("-fx-font-size: 64px;");
        Region spacer = UIHelper.spacer();

        welcomeHeader.getChildren().addAll(welcomeText, spacer, mascot);

        // Stats mini grid
        HBox statsRow = new HBox(16);
        int completed = state.getCompletedWorkouts().size();
        HBox stat1 = UIHelper.statsCard("Workouts Completed", String.valueOf(completed),
            "#e0f2fe", "#bae6fd", "#3b82f6");
        HBox stat2 = UIHelper.statsCard("This Week", String.valueOf(completed),
            "#fef3c7", "#fde68a", "#d97706");
        HBox.setHgrow(stat1, Priority.ALWAYS);
        HBox.setHgrow(stat2, Priority.ALWAYS);
        statsRow.getChildren().addAll(stat1, stat2);

        welcomeCard.getChildren().addAll(welcomeHeader, statsRow);

        // Today's workouts
        VBox workoutsCard = UIHelper.card();
        workoutsCard.getChildren().add(UIHelper.heading("Today's Workouts"));

        GridPane workoutsGrid = new GridPane();
        workoutsGrid.setHgap(12);
        workoutsGrid.setVgap(12);

        var workouts = DataStore.getInstance().getWorkouts();
        for (int i = 0; i < Math.min(4, workouts.size()); i++) {
            Workout w = workouts.get(i);
            WorkoutCardView card = new WorkoutCardView(w, state.getCompletedWorkouts().contains(w.getId()));
            card.setOnAction(() -> controller.openWorkoutDetail(w));
            workoutsGrid.add(card.getRoot(), i % 2, i / 2);
        }

        workoutsCard.getChildren().add(workoutsGrid);
        left.getChildren().addAll(welcomeCard, workoutsCard);

        // Right sidebar
        VBox right = new VBox(20);
        right.setPrefWidth(280);

        // Streak widget
        right.getChildren().add(buildStreakWidget(state));

        // Quick Stats
        VBox quickStats = UIHelper.card(20);
        quickStats.getChildren().add(UIHelper.heading("Quick Stats"));

        addProgressRow(quickStats, "Weekly Goal", "4/5 days", 0.80, "#3b82f6", "#e0f2fe");
        addProgressRow(quickStats, "Total Minutes", "145 min", 0.60, "#d97706", "#fef3c7");

        right.getChildren().add(quickStats);

        // Motivational card
        VBox motiCard = new VBox(12);
        motiCard.setPadding(new Insets(24));
        motiCard.setAlignment(Pos.CENTER);
        motiCard.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #3b82f6, #60a5fa);" +
            "-fx-background-radius: 20;"
        );
        Label motiEmoji = new Label("🔥");
        motiEmoji.setStyle("-fx-font-size: 48px;");
        Label motiTitle = new Label("Keep it up!");
        motiTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label motiText = new Label("You're on fire! Complete one more workout\nto maintain your streak.");
        motiText.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 13px; -fx-text-alignment: center;");
        motiText.setWrapText(true);
        motiText.setMaxWidth(220);
        motiCard.getChildren().addAll(motiEmoji, motiTitle, motiText);
        right.getChildren().add(motiCard);

        content.getChildren().addAll(left, right);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    private VBox buildStreakWidget(AppState state) {
        VBox widget = new VBox(12);
        widget.setPadding(new Insets(20));
        widget.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #d97706, #f59e0b);" +
            "-fx-background-radius: 16;"
        );

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        StackPane fireCircle = new StackPane();
        fireCircle.setPrefSize(56, 56);
        fireCircle.setMinSize(56, 56);
        fireCircle.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 28;");
        Label fire = new Label("🔥");
        fire.setStyle("-fx-font-size: 28px;");
        fireCircle.getChildren().add(fire);

        VBox streakText = new VBox(2);
        Label currentLabel = new Label("Current Streak");
        currentLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 12px;");

        HBox numRow = new HBox(6);
        numRow.setAlignment(Pos.BASELINE_LEFT);
        Label streakNum = new Label(String.valueOf(state.getStreak()));
        streakNum.setStyle("-fx-text-fill: white; -fx-font-size: 40px; -fx-font-weight: bold;");
        Label daysLabel = new Label("days");
        daysLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 18px;");
        numRow.getChildren().addAll(streakNum, daysLabel);
        streakText.getChildren().addAll(currentLabel, numRow);

        row.getChildren().addAll(fireCircle, streakText);

        int daysLeft = 7 - (state.getStreak() % 7);
        Label milestone = new Label(daysLeft + " days until next milestone");
        milestone.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 11px;");

        widget.getChildren().addAll(row, milestone);
        return widget;
    }

    private void addProgressRow(VBox parent, String label, String value, double pct, String color, String trackColor) {
        HBox labelRow = new HBox();
        labelRow.setAlignment(Pos.CENTER_LEFT);
        Label lbl = UIHelper.muted(label);
        Region spacer = UIHelper.spacer();
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        labelRow.getChildren().addAll(lbl, spacer, val);

        StackPane track = new StackPane();
        track.setPrefHeight(8);
        track.setMaxHeight(8);
        track.setStyle("-fx-background-color: " + trackColor + "; -fx-background-radius: 4;");

        HBox fill = new HBox();
        fill.setPrefWidth(pct * 220);
        fill.setPrefHeight(8);
        fill.setMaxHeight(8);
        fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        StackPane.setAlignment(fill, javafx.geometry.Pos.CENTER_LEFT);
        track.getChildren().add(fill);

        parent.getChildren().addAll(labelRow, track);
    }

    public ScrollPane getRoot() { return root; }
}
