package com.oop.gymquest.data;
import com.oop.gymquest.data.workoutdata.Workout;

import java.sql.*;

public class WorkoutDAO {
    public static boolean createCustomWorkout(Workout workout) {
        String sql = "INSERT INTO workouts (title, difficulty, duration, category, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setString(1, workout.getTitle());
            preparedStatement.setString(2, workout.getDifficulty().name());
            preparedStatement.setString(3, workout.getDuration());
            preparedStatement.setString(4, workout.getCategory().name());
            preparedStatement.setString(5, workout.getDescription());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}