package com.oop.gymquest.screens.workouts;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.WorkoutDAO;
import com.oop.gymquest.data.workoutdata.Exercise;
import com.oop.gymquest.data.workoutdata.Workout;
import com.oop.gymquest.data.workoutdata.WorkoutCategory;
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

    // ── FXML injections ────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private HBox      filterContainer;
    @FXML private FlowPane  cardContainer;

    // ── State ──────────────────────────────────────────────────────────────
    private WorkoutCategory activeCategory = null;

    // ── Image cache ────────────────────────────────────────────────────────
    // Keyed by WorkoutCategory; loaded once in initialize(), reused everywhere.
    private static final Map<WorkoutCategory, Image> CATEGORY_IMAGES = new HashMap<>();
    // Exercise images keyed by lowercase category string (strength/cardio/core/…)
    private static final Map<String, Image> EXERCISE_IMAGES = new HashMap<>();
    private static Image lockImage;
    private static boolean imagesLoaded = false;

    // ── Lifecycle ──────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        loadAllImages();
        buildCategoryPills();
        applyFilter();
        searchField.textProperty().addListener((obs, o, n) -> applyFilter());
    }

    private void loadAllImages() {
        if (imagesLoaded) return;

        CATEGORY_IMAGES.put(WorkoutCategory.STRENGTH,    img("muscle.png",           40));
        CATEGORY_IMAGES.put(WorkoutCategory.CARDIO,      img("treadmill.png",        40));
        CATEGORY_IMAGES.put(WorkoutCategory.CORE,        img("core.png",             40));
        CATEGORY_IMAGES.put(WorkoutCategory.FLEXIBILITY, img("flexibility.png",      40));
        CATEGORY_IMAGES.put(WorkoutCategory.HIIT,        img("hiit.png",             40));
        CATEGORY_IMAGES.put(WorkoutCategory.BALANCE,     img("tightrope-walker.png", 40));

        EXERCISE_IMAGES.put("strength",    img("muscle.png",           28));
        EXERCISE_IMAGES.put("cardio",      img("treadmill.png",        28));
        EXERCISE_IMAGES.put("core",        img("core.png",             28));
        EXERCISE_IMAGES.put("flexibility", img("flexibility.png",      28));
        EXERCISE_IMAGES.put("hiit",        img("hiit.png",             28));
        EXERCISE_IMAGES.put("balance",     img("tightrope-walker.png", 28));

        lockImage = img("lock.png", 32);

        imagesLoaded = true;
    }

    private Image img(String filename, int size) {
        try {
            var url = getClass().getResource("/com/oop/gymquest/images/" + filename);
            if (url != null) return new Image(url.toExternalForm(), size, size, true, true);
        } catch (Exception e) {
            System.err.println("[Images] Could not load " + filename + ": " + e.getMessage());
        }
        return null;
    }

    public static Image getCategoryImage(WorkoutCategory cat) {
        return cat != null ? CATEGORY_IMAGES.get(cat) : null;
    }

    public static Image getExerciseImage(String category) {
        if (category == null) return EXERCISE_IMAGES.get("strength");
        Image img = EXERCISE_IMAGES.get(category.toLowerCase());
        return img != null ? img : EXERCISE_IMAGES.get("strength");
    }

    private List<Workout> liveWorkouts() {
        // Pass the logged-in user's id so WorkoutDAO returns only:
        //   • All system workouts (createdBy == 0), and
        //   • Custom workouts this specific user created.
        int userId = (MainApp.instance.currentUser != null)
                     ? MainApp.instance.currentUser.getUserId()
                     : 0;
        return WorkoutDAO.getAllWorkouts(userId);
    }

    private void applyFilter() {
        final String kw = searchField.getText() == null ? ""
                        : searchField.getText().toLowerCase();
        final boolean hasKw = !kw.isBlank();

        List<Workout> filtered = liveWorkouts().stream()
            .filter(w -> {
                boolean keyOk = !hasKw
                    || w.getTitle().toLowerCase().contains(kw)
                    || (w.getDescription() != null
                        && w.getDescription().toLowerCase().contains(kw));
                boolean catOk = activeCategory == null || w.getCategory() == activeCategory;
                return keyOk && catOk;
            })
            .toList();

        renderCards(filtered);
    }

    // ── Category pills ─────────────────────────────────────────────────────

    private void buildCategoryPills() {
        filterContainer.getChildren().clear();
        filterContainer.getChildren().add(pillButton("All", null, activeCategory == null));
        for (WorkoutCategory cat : WorkoutCategory.values()) {
            filterContainer.getChildren().add(
                pillButton(getCategoryLabel(cat), cat, cat == activeCategory));
        }
    }

    private Button pillButton(String label, WorkoutCategory cat, boolean active) {
        Button btn = new Button(label);
        applyPillStyle(btn, cat, active);
        btn.setOnAction(e -> {
            activeCategory = (activeCategory == cat) ? null : cat;
            buildCategoryPills();
            applyFilter();
        });
        return btn;
    }

    private static void applyPillStyle(Button btn, WorkoutCategory cat, boolean active) {
        String accent = getCategoryColor(cat);
        if (active) {
            btn.setStyle(
                "-fx-background-color: " + accent + "; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-font-size: 12px;" +
                "-fx-padding: 8 18; -fx-background-radius: 20; -fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #64748b;" +
                "-fx-border-color: #bae6fd; -fx-border-width: 1.5; -fx-font-size: 12px;" +
                "-fx-padding: 8 18; -fx-background-radius: 20; -fx-border-radius: 20;" +
                "-fx-cursor: hand;"
            );
        }
    }

    // ── Card grid ──────────────────────────────────────────────────────────

    private void renderCards(List<Workout> workouts) {
        cardContainer.getChildren().clear();

        if (workouts.isEmpty()) {
            cardContainer.setAlignment(Pos.CENTER);

            VBox empty = new VBox(14);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(60, 0, 60, 0));
            empty.setMaxWidth(Double.MAX_VALUE);

            // No emoji — use a clean text-only empty state
            Label heading = new Label("No Workouts Yet");
            heading.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;"
            );

            boolean isFiltering = (searchField.getText() != null
                                   && !searchField.getText().isBlank())
                                || activeCategory != null;

            Label msg = new Label(isFiltering
                ? "No workouts match your search."
                : "Click \"+ Add Workout\" to build your first workout!");
            msg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            msg.setWrapText(true);
            msg.setMaxWidth(320);
            msg.setAlignment(Pos.CENTER);

            empty.getChildren().addAll(heading, msg);

            if (isFiltering) {
                Button clear = new Button("Clear Filters");
                clear.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #3b82f6;" +
                    "-fx-border-color: #bae6fd; -fx-border-width: 2;" +
                    "-fx-padding: 8 16; -fx-background-radius: 12;" +
                    "-fx-border-radius: 12; -fx-cursor: hand;"
                );
                clear.setOnAction(e -> {
                    searchField.clear();
                    activeCategory = null;
                    buildCategoryPills();
                    applyFilter();
                });
                empty.getChildren().add(clear);
            }

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

    // ── Single card builder ────────────────────────────────────────────────

    private VBox buildCard(Workout workout) {
        VBox card = new VBox(10);
        card.setPrefWidth(270);
        card.setMaxWidth(270);
        card.setPadding(new Insets(20));

        String baseStyle =
            "-fx-background-color: white; -fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 15, 0, 0, 5);";
        card.setStyle(baseStyle +
            " -fx-cursor: " + (workout.isLocked() ? "default" : "hand") + ";");
        if (workout.isLocked()) card.setOpacity(0.55);

        // ── Icon pane: category image instead of emoji ────────────────────
        String catColor = getCategoryColor(workout.getCategory());
        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(56, 56);
        iconPane.setMinSize(56, 56);
        iconPane.setStyle(
            "-fx-background-color: " + catColor + "22; -fx-background-radius: 14;"
        );

        if (workout.isLocked()) {
            // Lock icon: try image first, fall back to text
            if (lockImage != null) {
                ImageView lv = new ImageView(lockImage);
                lv.setFitWidth(28);
                lv.setFitHeight(28);
                lv.setPreserveRatio(true);
                iconPane.getChildren().add(lv);
            } else {
                Label lockLbl = new Label("🔒");
                lockLbl.setStyle("-fx-font-size: 22px;");
                iconPane.getChildren().add(lockLbl);
            }
        } else {
            Image catImg = getCategoryImage(workout.getCategory());
            if (catImg != null) {
                ImageView iv = new ImageView(catImg);
                iv.setFitWidth(36);
                iv.setFitHeight(36);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                iconPane.getChildren().add(iv);
            } else {
                // Image failed to load — show colored square, no emoji
                Label placeholder = new Label("?");
                placeholder.setStyle("-fx-text-fill: " + catColor + "; -fx-font-weight: bold;");
                iconPane.getChildren().add(placeholder);
            }
        }

        // ── Top row: icon + (custom tag + delete btn for custom workouts) ─
        HBox topRow = new HBox(iconPane);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Only the user who created this workout may delete it.
        int currentUserId = (MainApp.instance.currentUser != null)
                            ? MainApp.instance.currentUser.getUserId() : -1;
        boolean isOwner = workout.isCustom() && workout.getCreatedBy() == currentUserId;

        if (isOwner) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label customTag = new Label("Custom");
            customTag.setStyle(
                "-fx-background-color: #fef3c7; -fx-text-fill: #d97706;" +
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-padding: 3 8; -fx-background-radius: 10;"
            );

            Button deleteBtn = new Button("🗑");
            deleteBtn.setStyle(
                "-fx-background-color: #fee2e2; -fx-text-fill: #ef4444;" +
                "-fx-background-radius: 8; -fx-cursor: hand;" +
                "-fx-font-size: 13px; -fx-padding: 4 8;"
            );
            deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(
                "-fx-background-color: #ef4444; -fx-text-fill: white;" +
                "-fx-background-radius: 8; -fx-cursor: hand;" +
                "-fx-font-size: 13px; -fx-padding: 4 8;"
            ));
            deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(
                "-fx-background-color: #fee2e2; -fx-text-fill: #ef4444;" +
                "-fx-background-radius: 8; -fx-cursor: hand;" +
                "-fx-font-size: 13px; -fx-padding: 4 8;"
            ));
            deleteBtn.setOnAction(e -> {
                e.consume();
                confirmAndDelete(workout);
            });

            topRow.getChildren().addAll(spacer, new HBox(6, customTag, deleteBtn));
        }

        card.getChildren().add(topRow);

        // ── Title ──────────────────────────────────────────────────────────
        Label title = new Label(workout.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e3a5f;");
        title.setWrapText(true);

        // ── Badge row ──────────────────────────────────────────────────────
        FlowPane badges = new FlowPane(7, 5);
        badges.setAlignment(Pos.CENTER_LEFT);
        badges.setPrefWrapLength(230);

        badges.getChildren().add(
            makeBadge(workout.getDifficultyLabel(), getDiffColor(workout.getDifficulty()), "white"));

        Label durLabel = new Label("⏱ " + workout.getDuration());
        durLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11.5px;");
        badges.getChildren().add(durLabel);

        if (workout.getCategory() != null) {
            badges.getChildren().add(makeBadge(
                getCategoryLabel(workout.getCategory()), catColor + "20", catColor));
        }

        card.getChildren().addAll(title, badges);

        // ── Description snippet ───────────────────────────────────────────
        if (!workout.isLocked()
                && workout.getDescription() != null
                && !workout.getDescription().isBlank()) {
            Label desc = new Label(workout.getDescription());
            desc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11.5px;");
            desc.setWrapText(true);
            desc.setMaxHeight(30);
            card.getChildren().add(desc);
        }

        // ── Hover + click ─────────────────────────────────────────────────
        if (!workout.isLocked()) {
            card.setOnMouseClicked(e -> {
                if (!(e.getTarget() instanceof Button)) showWorkoutDetail(workout);
            });
            card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 20, 0, 0, 6);" +
                "-fx-cursor: hand; -fx-translate-y: -2;"
            ));
            card.setOnMouseExited(e -> card.setStyle(baseStyle + " -fx-cursor: hand;"));
        }

        return card;
    }

    // ── Delete ─────────────────────────────────────────────────────────────

    private void confirmAndDelete(Workout workout) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Workout");
        confirm.setHeaderText("Delete \"" + workout.getTitle() + "\"?");
        confirm.setContentText(
            "This custom workout will be permanently removed. This cannot be undone.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                WorkoutDAO.removeWorkout(workout.getId());
                applyFilter();
            }
        });
    }

    // ── Workout detail dialog ──────────────────────────────────────────────

    private void showWorkoutDetail(Workout workout) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(workout.getTitle());
        dialog.setHeaderText(null);

        VBox content = new VBox(14);
        content.setPadding(new Insets(20));
        content.setPrefWidth(440);

        // Title + meta row
        Label titleLbl = new Label(workout.getTitle());
        titleLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");

        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);
        meta.getChildren().add(
            makeBadge(workout.getDifficultyLabel(), getDiffColor(workout.getDifficulty()), "white"));
        if (workout.getCategory() != null) {
            String c = getCategoryColor(workout.getCategory());
            meta.getChildren().add(makeBadge(getCategoryLabel(workout.getCategory()), c, "white"));
        }
        Label dur = new Label("⏱ " + workout.getDuration());
        dur.setStyle("-fx-text-fill: #64748b;");
        meta.getChildren().add(dur);
        content.getChildren().addAll(titleLbl, meta);

        // Description
        if (workout.getDescription() != null && !workout.getDescription().isBlank()) {
            VBox descBox = new VBox();
            descBox.setPadding(new Insets(10, 12, 10, 12));
            descBox.setStyle(
                "-fx-background-color: #f0f8ff; -fx-background-radius: 10;" +
                "-fx-border-color: #bae6fd; -fx-border-width: 1; -fx-border-radius: 10;"
            );
            Label desc = new Label(workout.getDescription());
            desc.setWrapText(true);
            desc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
            descBox.getChildren().add(desc);
            content.getChildren().add(descBox);
        }

        // Exercise list
        if (workout.getExercises() != null && !workout.getExercises().isEmpty()) {
            Label exHeader = new Label("Exercises (" + workout.getExercises().size() + ")");
            exHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e3a5f;");
            content.getChildren().add(exHeader);

            for (int i = 0; i < workout.getExercises().size(); i++) {
                Exercise ex = workout.getExercises().get(i);
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                row.setStyle(
                    "-fx-background-color: #f8fafc; -fx-background-radius: 10;" +
                    "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 10;"
                );

                // Step number circle
                Label numLbl = new Label(String.valueOf(i + 1));
                numLbl.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                StackPane numCircle = new StackPane(numLbl);
                numCircle.setPrefSize(28, 28);
                numCircle.setMinSize(28, 28);
                numCircle.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 14;" +
                    "-fx-border-color: #bae6fd; -fx-border-width: 1.5;"
                );

                // Exercise icon: category image instead of emoji
                StackPane exIcon = buildExerciseIcon(ex.getCategory(), 22);

                VBox info = new VBox(2);
                Label nameL = new Label(ex.getName());
                nameL.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3a5f;");
                Label detail = new Label(ex.getSets() + " sets × " + ex.getReps() + " reps");
                detail.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
                info.getChildren().addAll(nameL, detail);

                row.getChildren().addAll(numCircle, exIcon, info);
                content.getChildren().add(row);
            }
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(460);
        scroll.setStyle("-fx-background-color: white; -fx-background: white;");

        dialog.getDialogPane().setContent(scroll);
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public static StackPane buildExerciseIcon(String category, int imageSize) {
        StackPane pane = new StackPane();
        pane.setPrefSize(imageSize + 14, imageSize + 14);
        pane.setMinSize(imageSize + 14, imageSize + 14);
        pane.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 10;");

        Image img = getExerciseImage(category);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(imageSize);
            iv.setFitHeight(imageSize);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            pane.getChildren().add(iv);
        }
        return pane;
    }

    // ── Navigation ─────────────────────────────────────────────────────────

    @FXML
    private void handleCreateCustom() {
        MainApp.instance.changeScene("create_custom_workout.fxml", "GymQuest - Create Custom Workout");
    }

    @FXML
    public static void handleAction() {
        MainApp.instance.changeScene("workouts.fxml", "GymQuest - Workout Library");
    }

    // ── Style helpers (public static — used by CustomWorkoutCreatorController) ─

    private static Label makeBadge(String text, String bg, String fg) {
        Label l = new Label(text);
        l.setPadding(new Insets(3, 10, 3, 10));
        l.setStyle(
            "-fx-background-color: " + bg + "; -fx-text-fill: " + fg + ";" +
            "-fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;"
        );
        return l;
    }

    public static String getCategoryLabel(WorkoutCategory cat) {
        if (cat == null) return "All";
        return switch (cat) {
            case STRENGTH    -> "Strength";
            case CARDIO      -> "Cardio";
            case CORE        -> "Core";
            case FLEXIBILITY -> "Flexibility";
            case HIIT        -> "HIIT";
            case BALANCE     -> "Balance";
        };
    }

    public static String getCategoryColor(WorkoutCategory cat) {
        if (cat == null) return "#3b82f6";
        return switch (cat) {
            case STRENGTH    -> "#3b82f6";
            case CARDIO      -> "#ef4444";
            case CORE        -> "#6366f1";
            case FLEXIBILITY -> "#10b981";
            case HIIT        -> "#f97316";
            case BALANCE     -> "#0d9488";
        };
    }

    private static String getDiffColor(Workout.Difficulty diff) {
        return switch (diff) {
            case BEGINNER     -> "#3b82f6";
            case INTERMEDIATE -> "#d97706";
            case ADVANCED     -> "#ef4444";
        };
    }
}
