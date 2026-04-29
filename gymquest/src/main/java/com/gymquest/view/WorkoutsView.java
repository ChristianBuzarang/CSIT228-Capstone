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

public class WorkoutsView {

    private final ScrollPane root;

    public WorkoutsView(AppState state, MainController controller) {
        VBox content = new VBox(24);
        content.setPadding(new Insets(24));

        // Header
        VBox header = UIHelper.card();
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
            UIHelper.title("All Workouts"),
            UIHelper.subtitle("Choose your challenge and level up!")
        );

        Region spacer = UIHelper.spacer();

        javafx.scene.control.Button customBtn = UIHelper.amberBtn("+ Create Custom");
        customBtn.setOnAction(e -> controller.openCustomWorkoutCreator());

        Label mascot = new Label("🏋️");
        mascot.setStyle("-fx-font-size: 48px;");

        row.getChildren().addAll(titleBox, spacer, customBtn, mascot);
        header.getChildren().add(row);
        content.getChildren().add(header);

        // Grid
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        var workouts = DataStore.getInstance().getWorkouts();
        for (int i = 0; i < workouts.size(); i++) {
            Workout w = workouts.get(i);
            WorkoutCardView card = new WorkoutCardView(w, state.getCompletedWorkouts().contains(w.getId()));
            card.getRoot().setPrefWidth(Double.MAX_VALUE);
            card.setOnAction(() -> {
                if (!w.isLocked()) controller.openWorkoutDetail(w);
            });
            grid.add(card.getRoot(), i % 3, i / 3);
            GridPane.setHgrow(card.getRoot(), Priority.ALWAYS);
        }

        content.getChildren().add(grid);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    public ScrollPane getRoot() { return root; }
}
