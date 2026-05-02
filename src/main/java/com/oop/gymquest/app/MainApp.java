package com.oop.gymquest.app;

import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.userdata.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainApp extends Application {
    public static MainApp instance;
    private Stage stage;
    public User currentUser; // This will hold an Admin, Trainer, or Member object

    @Override
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;

        // 1. Initialize Database (Creates tables and seeds default admin)
        DatabaseHandler.init();

        // 2. Clear old session files for a fresh start
        File session = new File("session.ser");
        if(session.exists()) session.delete();

        // 3. Start at Login
        changeScene("login.fxml", "GymQuest - Login");
    }

    public void changeScene(String fxmlFile, String title) {
        try {
            // All FXML files are expected to be in resources/com/oop/gymquest/
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/" + fxmlFile));
            Scene scene = new Scene(loader.load());

            // Responsive Logic: Set window size based on screen bounds
            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            stage.setWidth(Math.max(1024, Math.min(screen.getWidth() * 0.92, 1920)));
            stage.setHeight(Math.max(640, Math.min(screen.getHeight() * 0.92, 1080)));

            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Fatal Error: Could not load FXML -> " + fxmlFile);
            e.printStackTrace();
        }
    }

    public void saveSession(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("session.ser"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}