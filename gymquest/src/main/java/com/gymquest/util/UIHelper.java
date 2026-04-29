package com.gymquest.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIHelper {

    public static final String BLUE = "#3b82f6";
    public static final String LIGHT_BLUE = "#60a5fa";
    public static final String AMBER = "#d97706";
    public static final String DARK_BLUE = "#1e3a5f";
    public static final String SLATE = "#64748b";
    public static final String BG_BLUE = "#e0f2fe";
    public static final String BORDER_BLUE = "#bae6fd";
    public static final String BG_APP = "#f0f8ff";
    public static final String GREEN = "#10b981";
    public static final String RED = "#ef4444";

    public static Label title(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 24));
        l.setStyle("-fx-text-fill: " + DARK_BLUE + ";");
        return l;
    }

    public static Label subtitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 14px;");
        return l;
    }

    public static Label heading(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 18));
        l.setStyle("-fx-text-fill: " + DARK_BLUE + ";");
        return l;
    }

    public static Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + DARK_BLUE + ";");
        return l;
    }

    public static Label muted(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 12px;");
        return l;
    }

    public static Button primaryBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: linear-gradient(to right, " + BLUE + ", " + LIGHT_BLUE + ");" +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;" +
            "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-size: 14px;"
        );
        b.setOnMouseEntered(e -> b.setStyle(
            "-fx-background-color: linear-gradient(to right, #2563eb, " + BLUE + ");" +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;" +
            "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-size: 14px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
        ));
        b.setOnMouseExited(e -> b.setStyle(
            "-fx-background-color: linear-gradient(to right, " + BLUE + ", " + LIGHT_BLUE + ");" +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;" +
            "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-size: 14px;"
        ));
        return b;
    }

    public static Button amberBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: linear-gradient(to right, " + AMBER + ", #f59e0b);" +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;" +
            "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-size: 14px;"
        );
        return b;
    }

    public static Button ghostBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + BLUE + ";" +
            "-fx-border-color: " + BORDER_BLUE + "; -fx-border-width: 2;" +
            "-fx-padding: 8 16; -fx-background-radius: 10; -fx-border-radius: 10;" +
            "-fx-cursor: hand; -fx-font-size: 13px;"
        );
        return b;
    }

    public static Button dangerBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + RED + ";" +
            "-fx-border-color: " + RED + "; -fx-border-width: 1;" +
            "-fx-padding: 8 16; -fx-background-radius: 10; -fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        return b;
    }

    public static VBox card(double padding) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(padding));
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        );
        return card;
    }

    public static VBox card() { return card(24); }

    public static HBox statsCard(String label, String value, String bgFrom, String bgTo, String valueColor) {
        VBox content = new VBox(4);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + DARK_BLUE + "; -fx-font-size: 13px;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + valueColor + "; -fx-font-size: 28px; -fx-font-weight: bold;");
        content.getChildren().addAll(lbl, val);

        HBox box = new HBox(content);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + bgFrom + ", " + bgTo + ");" +
            "-fx-background-radius: 16;"
        );
        HBox.setHgrow(content, Priority.ALWAYS);
        return box;
    }

    public static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    public static Region vSpacer() {
        Region r = new Region();
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    public static void setProgressBar(Region bar, double percent, String color) {
        bar.setStyle(
            "-fx-background-color: " + color + "; -fx-background-radius: 4;"
        );
        bar.setPrefWidth(200 * percent);
    }

    public static StackPane avatarCircle(String emoji, double size, String fromColor, String toColor) {
        StackPane circle = new StackPane();
        circle.setPrefSize(size, size);
        circle.setMinSize(size, size);
        circle.setMaxSize(size, size);
        circle.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + fromColor + ", " + toColor + ");" +
            "-fx-background-radius: " + (size / 2) + ";"
        );
        Label lbl = new Label(emoji);
        lbl.setStyle("-fx-font-size: " + (size * 0.4) + "px;");
        circle.getChildren().add(lbl);
        return circle;
    }

    public static Label badge(String text, String bg, String fg) {
        Label l = new Label(text);
        l.setPadding(new Insets(3, 10, 3, 10));
        l.setStyle(
            "-fx-background-color: " + bg + "; -fx-text-fill: " + fg + ";" +
            "-fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;"
        );
        return l;
    }
}
