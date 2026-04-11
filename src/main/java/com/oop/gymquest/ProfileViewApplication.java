package com.oop.gymquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ProfileViewApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profileView.fxml"));

        // 1. Load the root as a BorderPane (matches your FXML structure)
        BorderPane root = fxmlLoader.load();

        // 2. Responsive window size: 92% of the primary screen (Matching Dashboard logic)
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double windowW = Math.max(1024, Math.min(screen.getWidth()  * 0.92, 1920));
        double windowH = Math.max(640,  Math.min(screen.getHeight() * 0.92, 1080));

        Scene scene = new Scene(root, windowW, windowH);

        // 3. KEY FIX: bind root pane size to scene size
        // This prevents the "broken" layout by forcing the UI to scale with the window
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        // 4. Stage setup
        stage.setTitle("GymQuest - Member Profile");
        stage.setScene(scene);

        // Setting min dimensions to prevent layout collapse
        stage.setMinWidth(1200);
        stage.setMinHeight(800);

        // 5. Center on screen
        stage.setX(screen.getMinX() + (screen.getWidth()  - windowW) / 2.0);
        stage.setY(screen.getMinY() + (screen.getHeight() - windowH) / 2.0);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}