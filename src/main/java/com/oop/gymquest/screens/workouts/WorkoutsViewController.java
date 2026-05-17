package com.oop.gymquest.screens.workouts;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.WorkoutDAO;
import com.oop.gymquest.data.workoutdata.Exercise;
import com.oop.gymquest.data.workoutdata.Workout;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutsViewController {

    @FXML private TextField searchField;
    @FXML private FlowPane  cardContainer;

    private static final Map<String, Image> EXERCISE_IMAGES = new HashMap<>();
    private static Image lockImage;
    private static boolean imagesLoaded = false;

    @FXML public void initialize() {
        loadAllImages();
        applyFilter();
        searchField.textProperty().addListener((obs, o, n) -> applyFilter());
    }

    private void loadAllImages() {
        if (imagesLoaded) return;
        EXERCISE_IMAGES.put("strength",    img("muscle.png", 28));
        EXERCISE_IMAGES.put("cardio",      img("treadmill.png", 28));
        EXERCISE_IMAGES.put("core",        img("core.png", 28));
        EXERCISE_IMAGES.put("flexibility", img("flexibility.png", 28));
        EXERCISE_IMAGES.put("hiit",        img("hiit.png", 28));
        EXERCISE_IMAGES.put("balance",     img("tightrope-walker.png", 28));
        lockImage = img("lock.png", 32);
        imagesLoaded = true;
    }

    private Image img(String filename, int size) {
        try {
            var url = getClass().getResource("/com/oop/gymquest/images/" + filename);
            if (url != null) return new Image(url.toExternalForm(), size, size, true, true);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static Image getExerciseImage(String category) {
        if (category == null) return EXERCISE_IMAGES.get("strength");
        Image img = EXERCISE_IMAGES.get(category.toLowerCase());
        return img != null ? img : EXERCISE_IMAGES.get("strength");
    }

    private void applyFilter() {
        final String kw = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        final boolean hasKw = !kw.isBlank();
        int currentUserId = MainApp.instance.currentUser.getUserId();
        List<Workout> allWorkouts = WorkoutDAO.getAllWorkouts(currentUserId);
        List<Workout> filtered = WorkoutDAO.filterList(allWorkouts, w ->
                !hasKw || w.getTitle().toLowerCase().contains(kw) ||
                        (w.getDescription() != null && w.getDescription().toLowerCase().contains(kw))
        );
        filtered.sort((w1, w2) -> Integer.compare(w2.getId(), w1.getId()));
        renderCards(filtered);
    }

    private void renderCards(List<Workout> workouts) {
        cardContainer.getChildren().clear();

        if (workouts.isEmpty()) {
            cardContainer.setAlignment(Pos.CENTER);
            VBox empty = new VBox(14);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(60, 0, 60, 0));

            Label heading = new Label("No Workouts Yet");
            heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");

            boolean isFiltering = (searchField.getText() != null && !searchField.getText().isBlank());
            Label msg = new Label(isFiltering ? "No workouts match your search." : "Click \"+ Add Workout\" to build your first workout!");
            msg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            empty.getChildren().addAll(heading, msg);

            StackPane wrapper = new StackPane(empty);
            wrapper.setMaxWidth(Double.MAX_VALUE);
            wrapper.setAlignment(Pos.CENTER);
            wrapper.setMinHeight(340);
            cardContainer.getChildren().add(wrapper);
            return;
        }

        cardContainer.setAlignment(Pos.TOP_LEFT);
        for (Workout w : workouts) {
            cardContainer.getChildren().add(buildCard(w));
        }
    }

    private VBox buildCard(Workout workout) {
        VBox card = new VBox(10);
        card.setPrefWidth(270);
        card.setMaxWidth(270);
        card.setPadding(new Insets(20));

        String baseStyle = "-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 15, 0, 0, 5);";
        card.setStyle(baseStyle + " -fx-cursor: " + (workout.isLocked() ? "default" : "hand") + ";");
        if (workout.isLocked()) card.setOpacity(0.55);

        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(56, 56);
        iconPane.setMinSize(56, 56);
        iconPane.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 14;"); // Unified light blue bg

        if (workout.isLocked() && lockImage != null) {
            ImageView lv = new ImageView(lockImage);
            lv.setFitWidth(28); lv.setFitHeight(28);
            iconPane.getChildren().add(lv);
        } else {
            ImageView iv = new ImageView(getExerciseImage("strength")); // Unified muscle icon
            iv.setFitWidth(36); iv.setFitHeight(36);
            iconPane.getChildren().add(iv);
        }

        HBox topRow = new HBox(iconPane);
        topRow.setAlignment(Pos.CENTER_LEFT);

        if (workout.isCustom()) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label customTag = new Label("Custom");
            customTag.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #d97706; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 10;");

            Button deleteBtn = new Button("🗑");
            deleteBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 4 8;");
            deleteBtn.setOnAction(e -> { e.consume(); confirmAndDelete(workout); });

            topRow.getChildren().addAll(spacer, new HBox(6, customTag, deleteBtn));
        }

        Label title = new Label(workout.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e3a5f;");
        title.setWrapText(true);

        FlowPane badges = new FlowPane(7, 5);
        badges.setAlignment(Pos.CENTER_LEFT);
        String diffColor = workout.getDifficultyLabel().equals("Advanced") ? "#ef4444" : (workout.getDifficultyLabel().equals("Intermediate") ? "#d97706" : "#3b82f6");
        Label diffBadge = new Label(workout.getDifficultyLabel());
        diffBadge.setStyle("-fx-background-color: " + diffColor + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 10;");

        Label durLabel = new Label("⏱ " + workout.getDuration());
        durLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11.5px;");
        badges.getChildren().addAll(diffBadge, durLabel);

        card.getChildren().addAll(topRow, title, badges);

        if (!workout.isLocked()) {
            card.setOnMouseClicked(e -> { if (!(e.getTarget() instanceof Button)) showWorkoutDetail(workout); });
            card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 20, 0, 0, 6); -fx-cursor: hand; -fx-translate-y: -2;"));
            card.setOnMouseExited(e -> card.setStyle(baseStyle + " -fx-cursor: hand;"));
        }

        return card;
    }

    private void confirmAndDelete(Workout workout) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete \"" + workout.getTitle() + "\"? This cannot be undone.");
        confirm.showAndWait().ifPresent(r -> { if (r == ButtonType.OK) { WorkoutDAO.removeWorkout(workout.getId()); applyFilter(); }});
    }

    private void showWorkoutDetail(Workout workout) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(workout.getTitle());
        VBox content = new VBox(14);
        content.setPadding(new Insets(20));
        content.setPrefWidth(440);

        Label titleLbl = new Label(workout.getTitle());
        titleLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");
        content.getChildren().add(titleLbl);

        if (workout.getExercises() != null) {
            for (int i = 0; i < workout.getExercises().size(); i++) {
                Exercise ex = workout.getExercises().get(i);
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

                StackPane exIcon = buildExerciseIcon(ex.getCategory(), 22);
                VBox info = new VBox(2, new Label(ex.getName()), new Label(ex.getSets() + " sets × " + ex.getReps() + " reps"));
                info.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3a5f;");
                info.getChildren().get(1).setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

                row.getChildren().addAll(new Label((i+1)+"."), exIcon, info);
                content.getChildren().add(row);
            }
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true); scroll.setPrefHeight(460);
        dialog.getDialogPane().setContent(scroll);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public static StackPane buildExerciseIcon(String category, int imageSize) {
        StackPane pane = new StackPane();
        pane.setPrefSize(imageSize + 14, imageSize + 14);
        pane.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 10;");
        Image img = getExerciseImage(category);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(imageSize); iv.setFitHeight(imageSize);
            pane.getChildren().add(iv);
        }
        return pane;
    }

    @FXML private void handleCreateCustom() {
        MainApp.instance.changeScene("create_custom_workout.fxml", "GymQuest - Create Custom Workout");
    }
}