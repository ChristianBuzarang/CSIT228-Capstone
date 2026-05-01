package com.oop.gymquest.app;

import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.oop.gymquest.data.DatabaseInit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainApp extends Application {
    public static MainApp instance;
    private Stage stage;
    public User currentUser; // The logged-in user session

    @Override
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;
        DatabaseHandler.init();
        // Lab Requirement: Startup safety check
        File session = new File("session.ser");
        if(session.exists()) session.delete();
        changeScene("login.fxml", "GymQuest - Login");

        // edited - Ericka Fatima
//        changeScene("booking-view.fxml", "GymQuest - Booking Test");
//        changeScene("custom-workout-creator-view.fxml", "GymQuest - Create Custom Workout");
//        changeScene("exercise-picker-dialog-view.fxml", "GymQuest - Exercise Picker Dialog");
//        DatabaseInit.initDatabase();
//        changeScene("/com/oop/gymquest/dashboard.fxml", "GymQuest - Dashboard");
    }

    public void changeScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/" + fxmlFile));
//            BorderPane root = loader.load();
            Scene scene = new Scene(loader.load());

            if (loader.getLocation() == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFile);
            }

            // Your Responsive Logic
            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            stage.setWidth(Math.max(1024, Math.min(screen.getWidth() * 0.92, 1920)));
            stage.setHeight(Math.max(640, Math.min(screen.getHeight() * 0.92, 1080)));

//            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
//            double windowW = Math.max(1024, Math.min(screen.getWidth()  * 0.92, 1920));
//            double windowH = Math.max(640,  Math.min(screen.getHeight() * 0.92, 1080));
//
//            Scene scene = new Scene(root, windowW, windowH);
//            root.prefWidthProperty().bind(scene.widthProperty());
//            root.prefHeightProperty().bind(scene.heightProperty());

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Could not load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public void saveSession(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("session.ser"))) {
            oos.writeObject(user);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) { launch(args); }
}
