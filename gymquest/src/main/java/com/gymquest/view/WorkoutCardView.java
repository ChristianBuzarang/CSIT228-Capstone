package com.gymquest.view;

import com.gymquest.model.Workout;
import com.gymquest.model.WorkoutCategory;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * WorkoutCardView
 *
 * A self-contained card widget displayed inside the WorkoutsView grid and the
 * DashboardView "Today's Workouts" section.
 *
 * ── Changes from original ──────────────────────────────────────────────────
 *  • Category badge added below the difficulty + duration row.
 *  • Icon gradient is now tinted with the category accent color instead of the
 *    fixed sky-blue gradient, so cards are visually distinct at a glance.
 *  • Description snippet added (2-line truncated preview) when a description
 *    is available on the workout.
 *  • All style strings extracted into named constants for readability.
 */
public class WorkoutCardView {

    // ── Style constants ────────────────────────────────────────────────────
    private static final String CARD_BASE =
        "-fx-background-color: white; -fx-background-radius: 16;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);";

    private static final String CARD_HOVER =
        "-fx-background-color: white; -fx-background-radius: 16;" +
        "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.22), 18, 0, 0, 5);" +
        "-fx-translate-y: -2;";

    private static final String CARD_LOCKED =
        "-fx-background-color: white; -fx-background-radius: 16;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);" +
        "-fx-cursor: default;";

    // ── Instance ───────────────────────────────────────────────────────────
    private final VBox root;
    private Runnable onAction;

    /**
     * @param workout   the workout data to display
     * @param completed whether this user has already completed the workout
     */
    public WorkoutCardView(Workout workout, boolean completed) {

        root = new VBox(10);
        root.setPadding(new Insets(16));
        root.setPrefWidth(240);
        root.setStyle(CARD_BASE + " -fx-cursor: " + (workout.isLocked() ? "default" : "hand") + ";");

        if (workout.isLocked()) root.setOpacity(0.6);

        // ── Icon ──────────────────────────────────────────────────────────
        String catColor = (workout.getCategory() != null)
                ? workout.getCategory().getColor()
                : "#3b82f6";
        String iconGradient = toSoftGradient(catColor);

        StackPane icon = new StackPane();
        icon.setPrefSize(56, 56);
        icon.setStyle("-fx-background-color: " + toSoftGradient(catColor) + "; -fx-background-radius: 12;");

        String emojiText = workout.isLocked() ? "🔒" : (completed ? "✅" : "💪");
        Label emoji = new Label(emojiText);
        emoji.setStyle("-fx-font-size: 24px;");
        icon.getChildren().add(emoji);

        // ── Title + tags ──────────────────────────────────────────────────
        VBox info = new VBox(5);

        Label title = new Label(workout.getTitle());
        title.setStyle("-fx-text-fill: #1e3a5f; -fx-font-weight: bold; -fx-font-size: 14px;");
        title.setWrapText(true);

        // Row 1: difficulty badge + duration
        HBox tagsRow = new HBox(6);
        tagsRow.setAlignment(Pos.CENTER_LEFT);

        String diffColor = switch (workout.getDifficulty()) {
            case BEGINNER     -> "#3b82f6";
            case INTERMEDIATE -> "#d97706";
            case ADVANCED     -> "#ef4444";
        };
        Label diff = UIHelper.badge(workout.getDifficultyLabel(), diffColor, "white");
        Label dur  = UIHelper.muted("⏱ " + workout.getDuration());
        tagsRow.getChildren().addAll(diff, dur);

        // Row 2: category badge (only when category is set)\
        HBox catRow = new HBox();
        catRow.setAlignment(Pos.CENTER_LEFT);

        if (workout.getCategory() != null) {
            // Create a pill-shaped badge using the category label and color
            Label catBadge = UIHelper.badge(
                    workout.getCategory().getLabel(),
                    catColor + "22", // 20% opacity background
                    catColor         // Solid text color
            );
            catRow.getChildren().add(catBadge);
        }

        info.getChildren().addAll(title, tagsRow, catRow);

        // ── Header row: icon + info ────────────────────────────────────────
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(icon, info);
        HBox.setHgrow(info, Priority.ALWAYS);

        root.getChildren().add(header);

        // ── Description snippet ───────────────────────────────────────────
        if (workout.getDescription() != null && !workout.getDescription().isBlank()
                && !workout.isLocked()) {
            Label desc = UIHelper.muted(workout.getDescription());
            desc.setWrapText(true);
            desc.setMaxHeight(34);            // ~2 lines at 12 px
            desc.setStyle(desc.getStyle() + " -fx-font-size: 11.5px;");
            root.getChildren().add(desc);
        }

        // ── Interaction ───────────────────────────────────────────────────
        if (!workout.isLocked()) {
            root.setOnMouseClicked(e -> { if (onAction != null) onAction.run(); });
            root.setOnMouseEntered(e -> root.setStyle(CARD_HOVER + " -fx-cursor: hand;"));
            root.setOnMouseExited(e ->  root.setStyle(CARD_BASE  + " -fx-cursor: hand;"));
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Converts a solid hex color into a soft two-stop gradient string suitable
     * for an icon background.  The first stop adds ~20 % alpha (22 hex) and
     * the second stop is the plain color at 15 % opacity (26 hex).
     */
    private static String toSoftGradient(String hex) {
        return "linear-gradient(to bottom right, " + hex + "22, " + hex + "18)";
    }

    // ── Public API ─────────────────────────────────────────────────────────
    public void setOnAction(Runnable r) { this.onAction = r; }
    public VBox getRoot()               { return root; }
}
