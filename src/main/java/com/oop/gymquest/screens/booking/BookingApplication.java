package com.oop.gymquest.screens.booking;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class BookingApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // This calls the method you wrote in the controller to switch the scene
        BookingController.handleAction();
    }

    public static void main(String[] args) {
        launch();
    }
}