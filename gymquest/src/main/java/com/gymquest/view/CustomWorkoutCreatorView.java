package com.gymquest.view;

import com.gymquest.controller.MainController;
import com.gymquest.model.AppState;
import com.gymquest.model.DataStore;
import com.gymquest.model.Exercise;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class CustomWorkoutCreatorView {

    private final ScrollPane root;
    private final List<Exercise> selectedExercises = new ArrayList<>();
    private VBox exerciseListBox;

    public CustomWorkoutCreatorView(AppState state, MainController controller) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.setMaxWidth(700);

        Button back = new Button("← Back to Workouts");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #3b82f6; -fx-cursor: hand; -fx-font-size: 14px;");
        back.setOnAction(e -> controller.navigateTo("workouts"));

        VBox card = UIHelper.card();
        card.getChildren().add(UIHelper.title("Create Custom Workout"));

        // Workout name
        Label nameLabel = UIHelper.label("Workout Name");
        TextField nameField = new TextField();
        nameField.setPromptText("My Awesome Workout");
        nameField.setStyle("-fx-padding: 10 14; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");
        card.getChildren().addAll(nameLabel, nameField);

        // Exercises header
        HBox exHeader = new HBox(12);
        exHeader.setAlignment(Pos.CENTER_LEFT);
        Label exLabel = UIHelper.label("Exercises");
        Region spacer = UIHelper.spacer();
        Button addBtn = UIHelper.primaryBtn("+ Add Exercise");
        exHeader.getChildren().addAll(exLabel, spacer, addBtn);
        card.getChildren().add(exHeader);

        // Exercise list
        exerciseListBox = new VBox(8);
        updateExerciseList();
        card.getChildren().add(exerciseListBox);

        // Add exercise opens dialog
        addBtn.setOnAction(e -> {
            ExercisePickerDialog picker = new ExercisePickerDialog(DataStore.getInstance().getExerciseLibrary());
            picker.showAndWait().ifPresent(ex -> {
                selectedExercises.add(ex);
                updateExerciseList();
            });
        });

        // Save button
        Button saveBtn = UIHelper.primaryBtn("Save Workout");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle(saveBtn.getStyle() + " -fx-padding: 14 20; -fx-font-size: 15px;");
        saveBtn.setOnAction(e -> {
            String title = nameField.getText().trim();
            if (title.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter a workout name.", ButtonType.OK).showAndWait();
                return;
            }
            if (selectedExercises.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please add at least one exercise.", ButtonType.OK).showAndWait();
                return;
            }
            Alert info = new Alert(Alert.AlertType.INFORMATION,
                "Custom workout \"" + title + "\" saved! 🎉", ButtonType.OK);
            info.setHeaderText(null);
            info.showAndWait();
            controller.navigateTo("workouts");
        });

        card.getChildren().add(saveBtn);
        content.getChildren().addAll(back, card);

        HBox centered = new HBox(content);
        centered.setPadding(new Insets(0, 40, 0, 40));
        centered.setAlignment(Pos.TOP_CENTER);

        root = new ScrollPane(centered);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    private void updateExerciseList() {
        exerciseListBox.getChildren().clear();
        if (selectedExercises.isEmpty()) {
            Label placeholder = new Label("Click \"Add Exercise\" to build your custom routine");
            placeholder.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
            VBox empty = new VBox(placeholder);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(20));
            empty.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 10; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-border-style: dashed;");
            exerciseListBox.getChildren().add(empty);
        } else {
            for (int i = 0; i < selectedExercises.size(); i++) {
                final int idx = i;
                Exercise ex = selectedExercises.get(i);
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(12));
                row.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 10; -fx-border-color: #bae6fd; -fx-border-width: 2;");

                Label indexLabel = new Label(String.valueOf(i + 1));
                indexLabel.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                StackPane numCircle = new StackPane(indexLabel);
                numCircle.setPrefSize(30, 30);
                numCircle.setMinSize(30, 30);
                numCircle.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #bae6fd; -fx-border-width: 2;");

                Label emoji = new Label(ex.getEmoji());
                emoji.setStyle("-fx-font-size: 20px;");

                VBox info = new VBox(2);
                Label name = UIHelper.label(ex.getName());
                name.setStyle(name.getStyle() + " -fx-font-weight: bold;");
                Label detail = UIHelper.muted(ex.getSets() + " sets × " + ex.getReps());
                info.getChildren().addAll(name, detail);

                Region spacer = UIHelper.spacer();

                Button removeBtn = new Button("🗑");
                removeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px;");
                removeBtn.setOnAction(e -> {
                    selectedExercises.remove(idx);
                    updateExerciseList();
                });

                row.getChildren().addAll(numCircle, emoji, info, spacer, removeBtn);
                exerciseListBox.getChildren().add(row);
            }
        }
    }

    public ScrollPane getRoot() { return root; }
}
