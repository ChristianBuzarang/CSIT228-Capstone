package com.gymquest.view;

import com.gymquest.model.Workout;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class WorkoutCardView {

    private final VBox root;
    private Runnable onAction;

    public WorkoutCardView(Workout workout, boolean completed) {
        root = new VBox(10);
        root.setPadding(new Insets(16));
        root.setPrefWidth(240);
        root.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);" +
            "-fx-cursor: " + (workout.isLocked() ? "default" : "hand") + ";"
        );

        if (workout.isLocked()) {
            root.setOpacity(0.6);
        }

        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);

        // Icon
        StackPane icon = new StackPane();
        icon.setPrefSize(56, 56);
        icon.setMinSize(56, 56);
        icon.setStyle("-fx-background-color: linear-gradient(to bottom right, #bae6fd, #e0f2fe); -fx-background-radius: 12;");
        Label emoji = new Label(workout.isLocked() ? "🔒" : (completed ? "✅" : "💪"));
        emoji.setStyle("-fx-font-size: 24px;");
        icon.getChildren().add(emoji);

        VBox info = new VBox(6);
        Label title = new Label(workout.getTitle());
        title.setStyle("-fx-text-fill: #1e3a5f; -fx-font-weight: bold; -fx-font-size: 14px;");
        title.setWrapText(true);

        HBox tags = new HBox(8);
        tags.setAlignment(Pos.CENTER_LEFT);
        Label diff = UIHelper.badge(
            workout.getDifficultyLabel(),
            switch (workout.getDifficulty()) {
                case BEGINNER -> "#3b82f6";
                case INTERMEDIATE -> "#d97706";
                case ADVANCED -> "#ef4444";
            },
            "white"
        );
        Label dur = UIHelper.muted(workout.getDuration());
        tags.getChildren().addAll(diff, dur);

        info.getChildren().addAll(title, tags);
        header.getChildren().addAll(icon, info);
        root.getChildren().add(header);

        if (!workout.isLocked()) {
            root.setOnMouseClicked(e -> { if (onAction != null) onAction.run(); });
            root.setOnMouseEntered(e -> root.setStyle(
                "-fx-background-color: white; -fx-background-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 16, 0, 0, 4);" +
                "-fx-cursor: hand; -fx-translate-y: -2;"
            ));
            root.setOnMouseExited(e -> root.setStyle(
                "-fx-background-color: white; -fx-background-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);" +
                "-fx-cursor: hand;"
            ));
        }
    }

    public void setOnAction(Runnable r) { this.onAction = r; }
    public VBox getRoot() { return root; }
}
