package com.gymquest.view;

import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class NotificationsDialog {

    private final Stage stage;

    private static final Object[][] NOTIFICATIONS = {
        {"achievement", "You earned the \"Week Warrior\" badge! 🏆", "2h ago", false},
        {"booking",     "Your session with Coach Alex is tomorrow at 10:00 AM", "3h ago", false},
        {"friend",      "Alex just beat your record on Full Body Blast!", "5h ago", true},
        {"reminder",    "Don't forget your workout today! Keep that streak alive! 🔥", "1d ago", true},
        {"friend",      "Sarah started following you", "2d ago", true},
        {"achievement", "You completed 10 workouts! Amazing progress! 💯", "3d ago", true}
    };

    public NotificationsDialog(Stage owner) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Notifications");

        VBox root = new VBox(0);
        root.setPrefWidth(400);
        root.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        // Header
        HBox header = new HBox(12);
        header.setPadding(new Insets(20, 20, 16, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color: #bae6fd; -fx-border-width: 0 0 1 0;");
        Label title = UIHelper.heading("Notifications");
        Region sp = UIHelper.spacer();
        Button close = new Button("✕");
        close.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-text-fill: #64748b;");
        close.setOnAction(e -> stage.close());
        header.getChildren().addAll(title, sp, close);

        // Notifications list
        VBox list = new VBox(8);
        list.setPadding(new Insets(16));

        for (Object[] n : NOTIFICATIONS) {
            list.getChildren().add(buildNotifRow(n));
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(420);
        scroll.setStyle("-fx-background-color: transparent;");

        root.getChildren().addAll(header, scroll);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            getClass().getResource("/com/gymquest/css/styles.css").toExternalForm()
        );
        scene.setFill(null);
        stage.setScene(scene);

        // Position near top-right
        stage.setOnShown(e -> {
            stage.setX(owner.getX() + owner.getWidth() - 420);
            stage.setY(owner.getY() + 70);
        });
    }

    private HBox buildNotifRow(Object[] n) {
        String type = (String) n[0];
        String msg = (String) n[1];
        String time = (String) n[2];
        boolean read = (boolean) n[3];

        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle(
            (read ? "-fx-background-color: white;" : "-fx-background-color: #e0f2fe;") +
            " -fx-background-radius: 12; -fx-border-color: #bae6fd; -fx-border-width: 1; -fx-border-radius: 12;"
        );

        String icon = switch (type) {
            case "achievement" -> "🏆";
            case "friend" -> "👥";
            case "reminder" -> "⏰";
            case "booking" -> "📅";
            default -> "🔔";
        };
        StackPane iconCircle = UIHelper.avatarCircle(icon, 36, "#3b82f6", "#60a5fa");

        VBox content = new VBox(3);
        HBox.setHgrow(content, Priority.ALWAYS);
        Label msgLabel = UIHelper.label(msg);
        msgLabel.setWrapText(true);
        msgLabel.setStyle(msgLabel.getStyle() + " -fx-font-size: 13px;");
        Label timeLabel = UIHelper.muted(time);
        content.getChildren().addAll(msgLabel, timeLabel);

        row.getChildren().addAll(iconCircle, content);

        if (!read) {
            StackPane dot = new StackPane();
            dot.setPrefSize(8, 8);
            dot.setMinSize(8, 8);
            dot.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 4;");
            row.getChildren().add(dot);
        }

        return row;
    }

    public void show() { stage.show(); }
}
