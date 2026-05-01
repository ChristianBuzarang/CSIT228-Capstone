package com.oop.gymquest.screens.community;

import com.oop.gymquest.model.AppState;

import com.oop.gymquest.view.CommunityView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CommunityApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Get the global AppState (mocking a logged in user)
        AppState state = AppState.getInstance();
        state.login(AppState.UserType.MEMBER, "Alex Chen");

        // 2. Initialize the Controller (which creates the View)
        CommunityController controller = new CommunityController(state);

        // 3. Set up the Scene
        // We use controller.getView() which returns the ScrollPane
        Scene scene = new Scene(controller.getView(), 1200, 800);

        // 4. Load styles (ensure the path matches your project structure)
        String css = getClass().getResource("/com/oop/gymquest/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("GymQuest Community Module - Development Mode");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}