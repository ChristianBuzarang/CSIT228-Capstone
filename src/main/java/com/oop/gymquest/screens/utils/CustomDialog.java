package com.oop.gymquest.screens.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomDialog {

    public static boolean showConfirmation(String title, String message, String confirmText, boolean isDestructive) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        boolean[] result = {false};

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #e2e8f0; -fx-border-width: 1;");

        DropShadow shadow = new DropShadow();
        shadow.setRadius(15); shadow.setOffsetY(5); shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        root.setEffect(shadow);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569; -fx-line-spacing: 4;");
        messageLabel.setPrefWidth(300);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        String cancelStyle = "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 8;";
        String cancelHover = "-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 8;";
        cancelBtn.setStyle(cancelStyle);
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(cancelHover));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(cancelStyle));

        Button confirmBtn = new Button(confirmText);
        String btnColor = isDestructive ? "#ef4444" : "#3b82f6";
        String hoverColor = isDestructive ? "#dc2626" : "#2563eb";

        String confirmStyle = String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 8 20; -fx-background-radius: 8;", btnColor);
        String confirmHover = String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 8 20; -fx-background-radius: 8;", hoverColor);

        confirmBtn.setStyle(confirmStyle);
        confirmBtn.setOnMouseEntered(e -> confirmBtn.setStyle(confirmHover));
        confirmBtn.setOnMouseExited(e -> confirmBtn.setStyle(confirmStyle));

        cancelBtn.setOnAction(e -> { result[0] = false; stage.close(); });
        confirmBtn.setOnAction(e -> { result[0] = true; stage.close(); });

        buttonBox.getChildren().addAll(cancelBtn, confirmBtn);
        root.getChildren().addAll(titleLabel, messageLabel, buttonBox);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.showAndWait();

        return result[0];
    }

    // NEW: Error dialog for validation/security alerts
    public static void showError(String title, String message) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #fca5a5; -fx-border-width: 1;");

        DropShadow shadow = new DropShadow();
        shadow.setRadius(15); shadow.setOffsetY(5); shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        root.setEffect(shadow);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ef4444;"); // Red title

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569; -fx-line-spacing: 4;");
        messageLabel.setPrefWidth(300);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button okBtn = new Button("OK");
        String okStyle = "-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 8 25; -fx-background-radius: 8;";
        String okHover = "-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 8 25; -fx-background-radius: 8;";

        okBtn.setStyle(okStyle);
        okBtn.setOnMouseEntered(e -> okBtn.setStyle(okHover));
        okBtn.setOnMouseExited(e -> okBtn.setStyle(okStyle));
        okBtn.setOnAction(e -> stage.close());

        buttonBox.getChildren().add(okBtn);
        root.getChildren().addAll(titleLabel, messageLabel, buttonBox);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.showAndWait();
    }
}