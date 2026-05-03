package com.oop.gymquest.screens.register;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegisterApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/register.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("GymQuest - Create Account");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}