package com.gymquest.controller;

import com.gymquest.GymQuestApp;
import com.gymquest.model.AppState;
import com.gymquest.model.Workout;
import com.gymquest.view.*;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class MainController {

    private final AppState state;
    private final BorderPane root;
    private final HeaderView headerView;
    private final SideMenuView sideMenuView;

    public MainController(AppState state) {
        this.state = state;
        this.root = new BorderPane();
        this.headerView = new HeaderView(state, this);
        this.sideMenuView = new SideMenuView(this);

        root.setTop(headerView.getRoot());
        root.setLeft(sideMenuView.getRoot());
        root.getStyleClass().add("main-root");

        navigateToDefault();
    }

    public BorderPane getRoot() { return root; }

    public void navigateToDefault() {
        switch (state.getUserType()) {
            case ADMIN -> navigateTo("admin");
            case TRAINER -> navigateTo("trainer");
            default -> navigateTo("dashboard");
        }
    }

    public void navigateTo(String page) {
        sideMenuView.setActivePage(page);
        switch (page) {
            case "dashboard" -> root.setCenter(new DashboardView(state, this).getRoot());
            case "workouts" -> root.setCenter(new WorkoutsView(state, this).getRoot());
            case "booking" -> root.setCenter(new BookingView(state, this).getRoot());
            case "community" -> root.setCenter(new CommunityView(state).getRoot());
            case "profile" -> root.setCenter(new ProfileView(state).getRoot());
            case "admin" -> root.setCenter(new AdminView(state).getRoot());
            case "trainer" -> root.setCenter(new TrainerView(state).getRoot());
        }
    }

    public void openWorkoutDetail(Workout workout) {
        WorkoutDetailView detail = new WorkoutDetailView(workout, state, this);
        root.setCenter(detail.getRoot());
    }

    public void openCustomWorkoutCreator() {
        CustomWorkoutCreatorView creator = new CustomWorkoutCreatorView(state, this);
        root.setCenter(creator.getRoot());
    }

    public void showNotifications() {
        NotificationsDialog dialog = new NotificationsDialog(GymQuestApp.primaryStage);
        dialog.show();
    }

    public static void showLoginScreen() {
        AppState state = AppState.getInstance();
        LoginView loginView = new LoginView(state);
        Scene scene = new Scene(loginView.getRoot(), 1280, 800);
        scene.getStylesheets().add(
            MainController.class.getResource("/com/gymquest/css/styles.css").toExternalForm()
        );
        GymQuestApp.primaryStage.setScene(scene);
    }

    public static void showMainScreen() {
        AppState state = AppState.getInstance();
        MainController controller = new MainController(state);
        Scene scene = new Scene(controller.getRoot(), 1280, 800);
        scene.getStylesheets().add(
            MainController.class.getResource("/com/gymquest/css/styles.css").toExternalForm()
        );
        GymQuestApp.primaryStage.setScene(scene);
    }
}
