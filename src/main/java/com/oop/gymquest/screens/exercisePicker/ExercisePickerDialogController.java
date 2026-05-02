package com.oop.gymquest.screens.exercisePicker;

import com.oop.gymquest.data.workoutdata.Exercise;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ExercisePickerDialogController {
    @FXML private TextField searchField;
    @FXML private FlowPane exerciseGrid;
    @FXML private ToggleGroup categoryGroup;
    private List<Exercise> library;
    private Exercise selected;
    private Button addBtn;

    public void init(List<Exercise> lib, Button btn) {
        this.library = lib; this.addBtn = btn; refresh("");
        searchField.textProperty().addListener((o, old, n) -> refresh(n));
    }

    private void refresh(String query) {
        exerciseGrid.getChildren().clear();
        for (Exercise ex : library) {
            if (!ex.getName().toLowerCase().contains(query.toLowerCase())) continue;
            VBox card = new VBox(5, new Label(ex.getEmoji()), new Label(ex.getName()), new Label(ex.getSets() + "x" + ex.getReps()));
            card.setPrefSize(210, 160); card.setPadding(new Insets(20));
            card.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 15; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-cursor: hand;");
            card.setOnMouseClicked(e -> {
                selected = ex; addBtn.setDisable(false);
                exerciseGrid.getChildren().forEach(n -> n.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 15; -fx-border-color: #bae6fd; -fx-border-width: 2;"));
                card.setStyle("-fx-background-color: #e0f2fe; -fx-background-radius: 15; -fx-border-color: #3b82f6; -fx-border-width: 2;");
            });
            exerciseGrid.getChildren().add(card);
        }
    }
    public Exercise getSelected() { return selected; }
}