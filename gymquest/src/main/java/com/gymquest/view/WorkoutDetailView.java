package com.gymquest.view;

import com.gymquest.controller.MainController;
import com.gymquest.model.AppState;
import com.gymquest.model.Exercise;
import com.gymquest.model.Workout;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * WorkoutDetailView
 *
 * Full-screen detail view for a single {@link Workout}.
 * Shown when the user taps a card in WorkoutsView or DashboardView.
 *
 * ── Changes from original ──────────────────────────────────────────────────
 *  • Description section added directly below the title — displays the
 *    workout summary text from {@link Workout#getDescription()}.
 *  • Category + difficulty metadata row added (pill badges under the title).
 *  • Null-safe: description and category sections are conditionally rendered
 *    so workouts without these fields still display cleanly.
 *  • "Complete" button is hidden for locked workouts (defensive guard).
 */
public class WorkoutDetailView {

    private final ScrollPane root;

    public WorkoutDetailView(Workout workout, AppState state, MainController controller) {

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.setMaxWidth(720);

        // ── Back button ───────────────────────────────────────────────────
        Button back = new Button("← Back to Workouts");
        back.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #3b82f6;" +
            "-fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0;"
        );
        back.setOnAction(e -> controller.navigateTo("workouts"));

        // ── Main card ─────────────────────────────────────────────────────
        VBox card = UIHelper.card();

        // Title
        card.getChildren().add(UIHelper.title(workout.getTitle()));

        // Category + difficulty pill row
        HBox metaRow = new HBox(8);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        if (workout.getCategory() != null) {
            String catColor = workout.getCategory().getColor();
            Label catBadge = UIHelper.badge(workout.getCategory().getLabel(), catColor, "white");
            metaRow.getChildren().add(catBadge);
        }

        String diffColor = switch (workout.getDifficulty()) {
            case BEGINNER     -> "#3b82f6";
            case INTERMEDIATE -> "#d97706";
            case ADVANCED     -> "#ef4444";
        };
        metaRow.getChildren().add(UIHelper.badge(workout.getDifficultyLabel(), diffColor, "white"));
        metaRow.getChildren().add(UIHelper.muted("⏱ " + workout.getDuration()));
        card.getChildren().add(metaRow);

        // Description
        if (workout.getDescription() != null && !workout.getDescription().isBlank()) {
            Label desc = new Label(workout.getDescription());
            desc.setWrapText(true);
            desc.setStyle(
                "-fx-text-fill: #64748b; -fx-font-size: 13.5px; -fx-line-spacing: 3;"
            );
            // Subtle description box
            VBox descBox = new VBox(desc);
            descBox.setPadding(new Insets(12, 14, 12, 14));
            descBox.setStyle(
                "-fx-background-color: #f0f8ff; -fx-background-radius: 10;" +
                "-fx-border-color: #bae6fd; -fx-border-width: 1; -fx-border-radius: 10;"
            );
            card.getChildren().add(descBox);
        }

        // Section heading
        card.getChildren().add(UIHelper.heading("Exercises (" + workout.getExercises().size() + ")"));

        // Exercise list
        for (int i = 0; i < workout.getExercises().size(); i++) {
            card.getChildren().add(buildExerciseRow(workout.getExercises().get(i), i + 1));
        }

        // ── Complete button (hidden for locked workouts) ───────────────────
        if (!workout.isLocked()) {
            boolean alreadyDone = state.getCompletedWorkouts().contains(workout.getId());

            Button completeBtn = new Button(alreadyDone ? "✅  Already Completed" : "✅  Complete Workout");
            completeBtn.setMaxWidth(Double.MAX_VALUE);
            completeBtn.setDisable(alreadyDone);
            completeBtn.setStyle(
                "-fx-background-color: " + (alreadyDone
                    ? "linear-gradient(to right, #6b7280, #9ca3af)"
                    : "linear-gradient(to right, #10b981, #059669)") + ";" +
                "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;" +
                "-fx-padding: 14 20; -fx-background-radius: 12; -fx-cursor: " +
                (alreadyDone ? "default" : "hand") + ";"
            );

            if (!alreadyDone) {
                completeBtn.setOnAction(e -> {
                    state.completeWorkout(workout.getId());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "🎉 \"" + workout.getTitle() + "\" completed! Great job!",
                        ButtonType.OK);
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    controller.navigateTo("workouts");
                });

                // Hover glow
                completeBtn.setOnMouseEntered(e -> completeBtn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #059669, #047857);" +
                    "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;" +
                    "-fx-padding: 14 20; -fx-background-radius: 12; -fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(16,185,129,0.35), 12, 0, 0, 3);"
                ));
                completeBtn.setOnMouseExited(e -> completeBtn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #10b981, #059669);" +
                    "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;" +
                    "-fx-padding: 14 20; -fx-background-radius: 12; -fx-cursor: hand;"
                ));
            }

            card.getChildren().add(completeBtn);
        }

        content.getChildren().addAll(back, card);

        // Center the card horizontally
        HBox centered = new HBox(content);
        centered.setPadding(new Insets(0, 40, 24, 40));
        centered.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(content, Priority.ALWAYS);

        root = new ScrollPane(centered);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    // ── Exercise row builder ───────────────────────────────────────────────

    private HBox buildExerciseRow(Exercise ex, int index) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle(
            "-fx-background-color: #f0f8ff; -fx-background-radius: 12;" +
            "-fx-border-color: #bae6fd; -fx-border-width: 2; -fx-border-radius: 12;"
        );

        // Emoji circle
        Label emojiLbl = new Label(ex.getEmoji());
        emojiLbl.setStyle("-fx-font-size: 24px;");
        StackPane emojiCircle = new StackPane(emojiLbl);
        emojiCircle.setPrefSize(44, 44);
        emojiCircle.setMinSize(44, 44);
        emojiCircle.setStyle("-fx-background-color: white; -fx-background-radius: 22;");

        // Name + sets/reps
        VBox info = new VBox(3);
        Label name = UIHelper.label(ex.getName());
        name.setStyle(name.getStyle() + " -fx-font-weight: bold;");
        Label detail = UIHelper.muted(ex.getSets() + " sets × " + ex.getReps() + " reps");
        info.getChildren().addAll(name, detail);

        // Index number circle (right-aligned)
        StackPane numCircle = new StackPane();
        numCircle.setPrefSize(32, 32);
        numCircle.setMinSize(32, 32);
        numCircle.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16;" +
            "-fx-border-color: #bae6fd; -fx-border-width: 2;"
        );
        Label num = new Label(String.valueOf(index));
        num.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 12px; -fx-font-weight: bold;");
        numCircle.getChildren().add(num);

        row.getChildren().addAll(emojiCircle, info, UIHelper.spacer(), numCircle);
        return row;
    }

    public ScrollPane getRoot() { return root; }
}
