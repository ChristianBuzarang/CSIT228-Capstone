package com.oop.gymquest.screens.sessions;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SessionsViewApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SessionsViewController.handleAction();
    }

    public static void main(String[] args) { launch(args); }
}