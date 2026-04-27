package com.oop.gymquest.screens.profile;

import javafx.application.Application;
import javafx.stage.Stage;

public class ProfileViewApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ProfileViewController.handleAction();
    }

    public static void main(String[] args) {
        launch(args);
    }
}