package com.gymquest.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import java.util.HashSet;

public class AppState {

    private static AppState instance;

    // Auth state
    private final StringProperty username = new SimpleStringProperty("");
    private final ObjectProperty<UserType> userType = new SimpleObjectProperty<>(UserType.MEMBER);
    private final BooleanProperty loggedIn = new SimpleBooleanProperty(false);

    // Fitness state
    private final IntegerProperty streak = new SimpleIntegerProperty(12);
    private final ObservableSet<Integer> completedWorkouts =
        FXCollections.observableSet(new HashSet<>(java.util.Set.of(0, 1)));

    // Weekly goal
    private int weeklyGoalDays = 5;
    private int weeklyGoalMinutes = 150;

    public enum UserType { MEMBER, TRAINER, ADMIN }

    private AppState() {}

    public static AppState getInstance() {
        if (instance == null) instance = new AppState();
        return instance;
    }

    public void login(UserType type, String user) {
        userType.set(type);
        username.set(user);
        loggedIn.set(true);
    }

    public void logout() {
        loggedIn.set(false);
        username.set("");
        userType.set(UserType.MEMBER);
    }

    public void completeWorkout(int id) {
        if (!completedWorkouts.contains(id)) {
            completedWorkouts.add(id);
            streak.set(streak.get() + 1);
        }
    }

    // Properties
    public StringProperty usernameProperty() { return username; }
    public ObjectProperty<UserType> userTypeProperty() { return userType; }
    public BooleanProperty loggedInProperty() { return loggedIn; }
    public IntegerProperty streakProperty() { return streak; }
    public ObservableSet<Integer> getCompletedWorkouts() { return completedWorkouts; }

    public String getUsername() { return username.get(); }
    public UserType getUserType() { return userType.get(); }
    public int getStreak() { return streak.get(); }

    public int getWeeklyGoalDays() { return weeklyGoalDays; }
    public int getWeeklyGoalMinutes() { return weeklyGoalMinutes; }
    public void setWeeklyGoal(int days, int minutes) {
        weeklyGoalDays = days;
        weeklyGoalMinutes = minutes;
    }
}
