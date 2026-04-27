package com.oop.gymquest.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    public static MainApp instance;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;
        // Load the initial screen
        changeScene("/com/oop/gymquest/dashboardView.fxml", "GymQuest - Dashboard");
    }

    public void changeScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            BorderPane root = loader.load();

            if (loader.getLocation() == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFile);
            }

            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            double windowW = Math.max(1024, Math.min(screen.getWidth()  * 0.92, 1920));
            double windowH = Math.max(640,  Math.min(screen.getHeight() * 0.92, 1080));

            Scene scene = new Scene(root, windowW, windowH);
            root.prefWidthProperty().bind(scene.widthProperty());
            root.prefHeightProperty().bind(scene.heightProperty());

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.err.println("Could not load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
