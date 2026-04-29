package com.gymquest;

import com.gymquest.model.AppState;
import com.gymquest.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GymQuestApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        AppState state = AppState.getInstance();

        LoginView loginView = new LoginView(state);
        Scene scene = new Scene(loginView.getRoot(), 1280, 800);
        scene.getStylesheets().add(
            getClass().getResource("/com/gymquest/css/styles.css").toExternalForm()
        );

        stage.setTitle("GymQuest");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
