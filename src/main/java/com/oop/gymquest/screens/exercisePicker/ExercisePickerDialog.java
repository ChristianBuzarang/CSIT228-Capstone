package com.oop.gymquest.screens.exercisePicker;

import com.oop.gymquest.data.workoutdata.Exercise;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.util.List;

public class ExercisePickerDialog extends Dialog<Exercise> {
    public ExercisePickerDialog(List<Exercise> library) {
        try {
            // MATCHING THE PATH IN MAINAPP: /com/oop/gymquest/
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/exercise-picker-dialog-view.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("FATAL ERROR: Could not find exercise-picker-dialog-view.fxml at /com/oop/gymquest/");
                return;
            }

            Parent root = loader.load();
            ExercisePickerDialogController controller = loader.getController();
            getDialogPane().setContent(root);

            setTitle("Exercise Library");

            ButtonType addType = new ButtonType("Add to Workout", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

            Button addBtn = (Button) getDialogPane().lookupButton(addType);
            addBtn.setDisable(true);
            addBtn.setStyle("-fx-background-color: #4f92ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");

            controller.init(library, addBtn);
            setResultConverter(bt -> (bt == addType) ? controller.getSelected() : null);

        } catch (Exception e) {
            System.err.println("Error loading Exercise Picker Dialog:");
            e.printStackTrace();
        }
    }
}