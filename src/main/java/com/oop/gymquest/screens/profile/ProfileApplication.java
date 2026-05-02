package com.oop.gymquest.screens.profile;

import javafx.application.Application;
import javafx.stage.Stage;

public class ProfileApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ProfileController.instance.initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}