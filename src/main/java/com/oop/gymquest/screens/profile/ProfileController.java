package com.oop.gymquest.screens.profile;

import com.oop.gymquest.app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;

public class ProfileController {
    @FXML private Label nameLabel, streakLabel, workoutsLabel, badgesLabel;
    @FXML private FlowPane badgeContainer;

    public static ProfileController instance;

    @FXML
    public void initialize() {
        // 1. Set User Info from Session
        if (MainApp.instance.currentUser != null) {
            nameLabel.setText(MainApp.instance.currentUser.getFullName());
        }

        // 2. Load Badges Dynamically
        loadBadges();
    }

    private void loadBadges() {
        Object[][] badges = {
                {"👟", "First Steps", "Unlocked"},
                {"🔥", "Week Warrior", "Unlocked"},
                {"🌅", "Early Bird", "Unlocked"},
                {"💪", "Iron Will", "Locked"},
                {"💯", "Century Club", "Locked"}
        };

        for (Object[] b : badges) {
            badgeContainer.getChildren().add(createBadgeCard((String)b[0], (String)b[1], (String)b[2]));
        }
    }

    private VBox createBadgeCard(String emoji, String title, String status) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(140, 140);

        boolean isLocked = status.equals("Locked");

        String style = "-fx-background-color: " + (isLocked ? "#f8fafc" : "#fef3c7") + ";" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: " + (isLocked ? "#e2e8f0" : "#f59e0b") + ";" +
                "-fx-border-width: 2; -fx-border-radius: 15;";

        card.setStyle(style);
        if (isLocked) card.setOpacity(0.6);

        Label icon = new Label(emoji); icon.setStyle("-fx-font-size: 35;");
        Label lblTitle = new Label(title); lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        Label lblStatus = new Label(status); lblStatus.setStyle("-fx-font-size: 11; -fx-text-fill: #64748b;");

        card.getChildren().addAll(icon, lblTitle, lblStatus);
        return card;
    }
}