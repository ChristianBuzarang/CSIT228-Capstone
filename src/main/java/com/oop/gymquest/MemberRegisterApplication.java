package com.oop.gymquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MemberRegisterApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("memberRegisterView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 500);

        stage.setTitle("Member Registration");
        stage.setScene(scene);
        stage.show();
    }

}