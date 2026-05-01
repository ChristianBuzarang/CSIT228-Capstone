package com.oop.gymquest.screens.dashboard;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        DashboardController.instance.initialize();
    }

    public static void main(String[] args) {
        launch();
    }
}