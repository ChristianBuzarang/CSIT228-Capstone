package com.gymquest.view;

import com.gymquest.model.AppState;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ProfileView {

    private final ScrollPane root;

    private static final Object[][] BADGES = {
        {"👟", "First Steps", "Complete your first workout", true, 0, 0},
        {"🔥", "Week Warrior", "Maintain a 7-day streak", true, 0, 0},
        {"🌅", "Early Bird", "Complete 5 morning workouts", true, 5, 5},
        {"💪", "Iron Will", "Maintain a 30-day streak", false, 12, 30},
        {"💯", "Century Club", "Complete 100 workouts", false, 47, 100},
        {"🦋", "Social Butterfly", "Add 10 friends", false, 6, 10},
        {"👑", "Consistency King", "Maintain a 14-day streak", true, 0, 0},
        {"🏋️", "Power Lifter", "Complete 20 strength workouts", false, 12, 20},
        {"🏃", "Cardio Champion", "Complete 15 cardio workouts", false, 8, 15}
    };

    public ProfileView(AppState state) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        // Profile card
        VBox profileCard = UIHelper.card();
        HBox profileRow = new HBox(24);
        profileRow.setAlignment(Pos.TOP_LEFT);

        StackPane avatar = UIHelper.avatarCircle("🎯", 80, "#3b82f6", "#60a5fa");

        VBox profileInfo = new VBox(8);
        HBox.setHgrow(profileInfo, Priority.ALWAYS);
        Label username = UIHelper.title(state.getUsername());
        Label member = UIHelper.subtitle("Fitness Enthusiast • Member since Jan 2026");

        HBox statsRow = new HBox(16);
        long unlockedCount = java.util.Arrays.stream(BADGES).filter(b -> (boolean) b[3]).count();

        statsRow.getChildren().addAll(
            miniStat("🔥", "Streak", state.getStreak() + " days", "#3b82f6"),
            miniStat("🎯", "Workouts", String.valueOf(state.getCompletedWorkouts().size()), "#d97706"),
            miniStat("🏆", "Badges", unlockedCount + "/" + BADGES.length, "#3b82f6")
        );

        profileInfo.getChildren().addAll(username, member, statsRow);

        Button editBtn = UIHelper.ghostBtn("✏️  Edit Profile");
        profileRow.getChildren().addAll(avatar, profileInfo, editBtn);
        profileCard.getChildren().add(profileRow);
        content.getChildren().add(profileCard);

        // Badges
        VBox badgesCard = UIHelper.card();
        badgesCard.getChildren().add(UIHelper.heading("🏆  Achievements"));

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        for (int i = 0; i < BADGES.length; i++) {
            Object[] b = BADGES[i];
            boolean unlocked = (boolean) b[3];
            int progress = (int) b[4];
            int total = (int) b[5];

            grid.add(buildBadge(b, unlocked, progress, total), i % 3, i / 3);
        }

        badgesCard.getChildren().add(grid);
        content.getChildren().add(badgesCard);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    private VBox miniStat(String icon, String label, String value, String color) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(14));
        box.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0f2fe, #bae6fd); -fx-background-radius: 12;");
        box.setPrefWidth(160);
        HBox lrow = new HBox(6);
        lrow.setAlignment(Pos.CENTER_LEFT);
        Label ic = new Label(icon);
        ic.setStyle("-fx-font-size: 16px;");
        Label lbl = UIHelper.muted(label);
        lrow.getChildren().addAll(ic, lbl);
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        box.getChildren().addAll(lrow, val);
        return box;
    }

    private VBox buildBadge(Object[] b, boolean unlocked, int progress, int total) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setStyle(
            (unlocked
                ? "-fx-background-color: linear-gradient(to bottom right, #fef3c7, #fde68a); -fx-border-color: #d97706;"
                : "-fx-background-color: #f0f8ff; -fx-border-color: #bae6fd; -fx-opacity: 0.7;") +
            " -fx-background-radius: 16; -fx-border-width: 2; -fx-border-radius: 16;"
        );

        Label emoji = new Label((String) b[0]);
        emoji.setStyle("-fx-font-size: 40px;");
        Label name = UIHelper.label((String) b[1]);
        name.setStyle(name.getStyle() + " -fx-font-weight: bold; -fx-font-size: 13px;");
        name.setAlignment(Pos.CENTER);
        Label desc = UIHelper.muted((String) b[2]);
        desc.setWrapText(true);
        desc.setAlignment(Pos.CENTER);

        card.getChildren().addAll(emoji, name, desc);

        if (!unlocked && total > 0) {
            double pct = (double) progress / total;
            StackPane track = new StackPane();
            track.setPrefHeight(6);
            track.setMaxHeight(6);
            track.setPrefWidth(150);
            track.setStyle("-fx-background-color: white; -fx-background-radius: 3;");
            HBox fill = new HBox();
            fill.setPrefWidth(150 * pct);
            fill.setPrefHeight(6);
            fill.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 3;");
            StackPane.setAlignment(fill, Pos.CENTER_LEFT);
            track.getChildren().add(fill);

            Label prog = UIHelper.muted(progress + " / " + total);
            card.getChildren().addAll(track, prog);
        }

        if (unlocked) {
            Label unlockLabel = new Label("🏆 Unlocked!");
            unlockLabel.setStyle("-fx-text-fill: #d97706; -fx-font-size: 12px; -fx-font-weight: bold;");
            card.getChildren().add(unlockLabel);
        }

        return card;
    }

    public ScrollPane getRoot() { return root; }
}
