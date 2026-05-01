package com.oop.gymquest.screens.dashboard;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // 1. Initialize Database & Seed default admin
        DatabaseHandler.init();

        // 2. Setup MainApp instance (DashboardController depends on this)
        if (MainApp.instance == null) new MainApp();

        // 3. Authenticate using the REAL SQL account seeded in DatabaseHandler.init()
        // Username: 'admin', Password: '1234'
        MainApp.instance.currentUser = DatabaseHandler.authenticate("admin", "1234");

        // 4. Load the actual dashboard UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/dashboard_shell.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);

        stage.setTitle("GymQuest - Dashboard (Admin View)");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}