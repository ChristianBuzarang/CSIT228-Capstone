package com.oop.gymquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SessionsViewApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sessionsView.fxml"));
        BorderPane root = fxmlLoader.load();

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double windowW = Math.max(1024, Math.min(screen.getWidth() * 0.92, 1920));
        double windowH = Math.max(640, Math.min(screen.getHeight() * 0.92, 1080));

        Scene scene = new Scene(root, windowW, windowH);
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        stage.setTitle("GymQuest - Fitness Programs");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}