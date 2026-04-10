package com.oop.gymquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfileView extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 500);

        stage.setTitle("Profile View");
        stage.setScene(scene);
        stage.show();
    }

}
