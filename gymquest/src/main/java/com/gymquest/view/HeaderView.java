package com.gymquest.view;

import com.gymquest.controller.MainController;
import com.gymquest.model.AppState;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HeaderView {

    private final HBox root;

    public HeaderView(AppState state, MainController controller) {
        root = new HBox(16);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(14, 24, 14, 24));
        root.setStyle(
            "-fx-background-color: rgba(255,255,255,0.95);" +
            "-fx-border-color: #bae6fd; -fx-border-width: 0 0 1 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );

        Label logo = new Label("🏋️  GymQuest");
        logo.setFont(Font.font("System", FontWeight.BOLD, 22));
        logo.setStyle("-fx-text-fill: #3b82f6;");

        Region spacer = UIHelper.spacer();

        // Notification bell
        Button notifBtn = new Button("🔔");
        notifBtn.setStyle(
            "-fx-background-color: transparent; -fx-font-size: 20px;" +
            "-fx-cursor: hand; -fx-padding: 4 8;"
        );
        notifBtn.setOnAction(e -> controller.showNotifications());

        // Badge on bell
        StackPane bellStack = new StackPane(notifBtn);
        Label badge = new Label("2");
        badge.setStyle(
            "-fx-background-color: #ef4444; -fx-text-fill: white;" +
            "-fx-font-size: 9px; -fx-font-weight: bold;" +
            "-fx-background-radius: 10; -fx-padding: 1 4;"
        );
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        bellStack.getChildren().add(badge);

        // Profile
        String avatar = switch (state.getUserType()) {
            case ADMIN -> "👨‍💼";
            case TRAINER -> "🏋️";
            default -> "🎯";
        };

        HBox profileBtn = new HBox(10);
        profileBtn.setAlignment(Pos.CENTER);
        profileBtn.setStyle("-fx-cursor: hand; -fx-padding: 4 10; -fx-background-radius: 20;");
        Label nameLabel = new Label(state.getUsername());
        nameLabel.setStyle("-fx-text-fill: #1e3a5f; -fx-font-weight: bold;");
        StackPane avatarCircle = UIHelper.avatarCircle(avatar, 38, "#3b82f6", "#60a5fa");
        profileBtn.getChildren().addAll(nameLabel, avatarCircle);

        profileBtn.setOnMouseClicked(e -> {
            if (state.getUserType() == AppState.UserType.MEMBER) {
                controller.navigateTo("profile");
            }
        });

        // Logout
        Button logoutBtn = UIHelper.ghostBtn("Logout");
        logoutBtn.setOnAction(e -> {
            state.logout();
            MainController.showLoginScreen();
        });

        root.getChildren().addAll(logo, spacer, bellStack, profileBtn, logoutBtn);
    }

    public HBox getRoot() { return root; }
}
