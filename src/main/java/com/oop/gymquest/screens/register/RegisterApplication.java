package com.oop.gymquest.screens.register;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegisterApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        RegisterController.instance.handleRegister();
    }

}