package com.oop.gymquest.screens.workouts;

import javafx.application.Application;
import javafx.stage.Stage;

public class WorkoutsViewApplication extends Application {
    @Override
    public void start(Stage stage) {
        WorkoutsViewController.handleAction();
    }

    public static void main(String[] args) { launch(args); }
}