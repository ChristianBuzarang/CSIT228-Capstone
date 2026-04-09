package com.oop.gymquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import java.io.IOException;

public class GymQuestApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                GymQuestApplication.class.getResource("gymQuestDashboard.fxml"));

        // ── Load root ────────────────────────────────────────────────────────
        BorderPane root = fxmlLoader.load();

        // ── Responsive window size: 92% of the primary screen ───────────────
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double windowW = Math.max(1024, Math.min(screen.getWidth()  * 0.92, 1920));
        double windowH = Math.max(640,  Math.min(screen.getHeight() * 0.92, 1080));

        Scene scene = new Scene(root, windowW, windowH);

        // ── KEY FIX: bind root pane size to scene size ───────────────────────
        // This ensures the BorderPane (and everything inside it) always
        // fills the scene exactly — no overflow, no clipping, no blank space.
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        // ── Stage setup ─────────────────────────────────────────────────────
        stage.setTitle("GymQuest");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(580);

        // Center on screen
        stage.setX(screen.getMinX() + (screen.getWidth()  - windowW) / 2.0);
        stage.setY(screen.getMinY() + (screen.getHeight() - windowH) / 2.0);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}