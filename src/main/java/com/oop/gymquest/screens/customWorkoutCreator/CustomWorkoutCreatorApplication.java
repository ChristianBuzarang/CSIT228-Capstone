package com.oop.gymquest.screens.customWorkoutCreator;

import com.oop.gymquest.screens.booking.BookingController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomWorkoutCreatorApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // This calls the method you wrote in the controller to switch the scene
//        BookingController.handleAction();
    }

    public static void main(String[] args) {
        launch();
    }

}
