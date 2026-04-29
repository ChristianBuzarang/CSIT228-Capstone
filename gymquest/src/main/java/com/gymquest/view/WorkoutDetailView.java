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

public class WorkoutDetailView {

    private final ScrollPane root;

    public WorkoutDetailView(Workout workout, AppState state, MainController controller) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.setMaxWidth(700);

        // Back button
        Button back = new Button("← Back to Workouts");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #3b82f6; -fx-cursor: hand; -fx-font-size: 14px;");
        back.setOnAction(e -> controller.navigateTo("workouts"));

        VBox card = UIHelper.card();
        card.getChildren().add(UIHelper.title(workout.getTitle()));

        // Exercise list
        for (int i = 0; i < workout.getExercises().size(); i++) {
            Exercise ex = workout.getExercises().get(i);
            HBox row = buildExerciseRow(ex, i + 1);
            card.getChildren().add(row);
        }

        // Complete button
        Button completeBtn = new Button("✅  Complete Workout");
        completeBtn.setMaxWidth(Double.MAX_VALUE);
        completeBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #10b981, #059669);" +
            "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;" +
            "-fx-padding: 14 20; -fx-background-radius: 12; -fx-cursor: hand;"
        );
        completeBtn.setOnAction(e -> {
            state.completeWorkout(workout.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "🎉 Workout \"" + workout.getTitle() + "\" completed! Great job!", ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
            controller.navigateTo("workouts");
        });

        card.getChildren().add(completeBtn);
        content.getChildren().addAll(back, card);

        HBox centered = new HBox(content);
        centered.setPadding(new Insets(0, 40, 0, 40));
        centered.setAlignment(Pos.TOP_CENTER);

        root = new ScrollPane(centered);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    private HBox buildExerciseRow(Exercise ex, int index) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 12; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-border-radius: 12;");

        Label emoji = new Label(ex.getEmoji());
        emoji.setStyle("-fx-font-size: 24px;");
        StackPane emojiCircle = new StackPane(emoji);
        emojiCircle.setPrefSize(44, 44);
        emojiCircle.setMinSize(44, 44);
        emojiCircle.setStyle("-fx-background-color: white; -fx-background-radius: 22;");

        VBox info = new VBox(3);
        Label name = UIHelper.label(ex.getName());
        name.setStyle(name.getStyle() + " -fx-font-weight: bold;");
        Label detail = UIHelper.muted(ex.getSets() + " sets × " + ex.getReps() + " reps");
        info.getChildren().addAll(name, detail);

        Region spacer = UIHelper.spacer();

        StackPane numCircle = new StackPane();
        numCircle.setPrefSize(32, 32);
        numCircle.setMinSize(32, 32);
        numCircle.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #bae6fd; -fx-border-width: 2;");
        Label num = new Label(String.valueOf(index));
        num.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 12px; -fx-font-weight: bold;");
        numCircle.getChildren().add(num);

        row.getChildren().addAll(emojiCircle, info, spacer, numCircle);
        return row;
    }

    public ScrollPane getRoot() { return root; }
}
