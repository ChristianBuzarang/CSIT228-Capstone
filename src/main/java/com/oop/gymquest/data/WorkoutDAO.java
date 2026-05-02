package com.oop.gymquest.data;

import com.oop.gymquest.data.workoutdata.Exercise;
import com.oop.gymquest.data.workoutdata.Workout;
import com.oop.gymquest.data.workoutdata.WorkoutCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WorkoutDAO {

    // ── In-memory cache ────────────────────────────────────────────────────
    // Mutable so that newly-created custom workouts can be appended at runtime.
    private static final List<Workout> runtimeCache = new ArrayList<>();
    private static boolean cacheInitialized = false;

    // ── Public API ─────────────────────────────────────────────────────────


    public static List<Workout> getAllWorkouts() {
        if (!cacheInitialized) {
            initializeCache();
        }
        return Collections.unmodifiableList(runtimeCache);
    }

    public static boolean createCustomWorkout(Workout workout) {
        // Ensure the cache is ready before appending
        if (!cacheInitialized) initializeCache();

        boolean dbSuccess = false;
        String sql = "INSERT INTO workouts (title, difficulty, duration, category, description, is_custom)"
                   + " VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, workout.getTitle());
            ps.setString(2, workout.getDifficulty().name());
            ps.setString(3, workout.getDuration());
            ps.setString(4, workout.getCategory().name());
            ps.setString(5, workout.getDescription());
            dbSuccess = ps.executeUpdate() > 0;
        } catch (Exception e) {
            // DB not available — still continue so the session works
            System.err.println("[WorkoutDAO] DB write failed (continuing in-memory): " + e.getMessage());
        }

        // Always add to the runtime cache regardless of DB outcome.
        // This is the fix for custom workouts not appearing in the list.
        runtimeCache.add(workout);
        return dbSuccess;
    }

    // ── Cache initialization ───────────────────────────────────────────────

    private static synchronized void initializeCache() {
        if (cacheInitialized) return; // double-checked locking guard

        try {
            loadFromDatabase();
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] DB unavailable, using default catalog: " + e.getMessage());
            loadDefaultWorkouts();
        }
        cacheInitialized = true;
    }


    private static void loadFromDatabase() throws SQLException {
        String sql = "SELECT id, title, difficulty, duration, category, description, locked"
                   + " FROM workouts ORDER BY id";
        try (Connection c = MySQLConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                WorkoutCategory cat = parseCategory(rs.getString("category"));
                Workout.Difficulty diff = parseDifficulty(rs.getString("difficulty"));
                boolean locked = rs.getBoolean("locked");

                Workout w = new Workout(
                    rs.getInt("id"),
                    rs.getString("title"),
                    diff,
                    rs.getString("duration"),
                    locked,
                    new ArrayList<>(),   // exercises — extend with JOIN when ready
                    cat,
                    rs.getString("description"),
                    null                 // imagePath — extend when assets are ready
                );
                runtimeCache.add(w);
            }
        }
    }

    // ── Default catalog (DB fallback) ─────────────────────────────────────

    private static void loadDefaultWorkouts() {
        runtimeCache.add(new Workout(
            0, "Full Body Blast", Workout.Difficulty.BEGINNER, "30 min", false,
            List.of(
                new Exercise(1, "Push-ups",  3, "10-12",   "💪", "strength"),
                new Exercise(2, "Squats",    3, "15",      "🦵", "strength"),
                new Exercise(3, "Plank",     3, "30 sec",  "🧘", "core"),
                new Exercise(4, "Lunges",    3, "10 each", "🏃", "strength")
            ),
            WorkoutCategory.STRENGTH,
            "A complete beginner-friendly routine targeting all major muscle groups "
            + "through fundamental movement patterns.",
            null
        ));

        runtimeCache.add(new Workout(
            1, "Upper Body Power", Workout.Difficulty.BEGINNER, "25 min", false,
            List.of(
                new Exercise(1, "Dumbbell Press", 4, "8-10", "🏋️", "strength"),
                new Exercise(2, "Pull-ups",       3, "6-8",  "💪", "strength"),
                new Exercise(3, "Bicep Curls",    3, "12",   "💪", "strength"),
                new Exercise(4, "Tricep Dips",    3, "10",   "🔥", "strength")
            ),
            WorkoutCategory.STRENGTH,
            "Build upper-body strength and muscle with compound pushing and pulling exercises.",
            null
        ));

        runtimeCache.add(new Workout(
            2, "Core Crusher", Workout.Difficulty.INTERMEDIATE, "20 min", false,
            List.of(
                new Exercise(1, "Crunches",          4, "20",      "🔥", "core"),
                new Exercise(2, "Russian Twists",    3, "15 each", "🌀", "core"),
                new Exercise(3, "Leg Raises",        3, "12",      "🦵", "core"),
                new Exercise(4, "Mountain Climbers", 3, "20",      "⛰️", "cardio")
            ),
            WorkoutCategory.CORE,
            "Intense core-focused routine to build a strong, stable midsection.",
            null
        ));

        runtimeCache.add(new Workout(
            3, "Leg Day Legends", Workout.Difficulty.INTERMEDIATE, "40 min", false,
            List.of(
                new Exercise(1, "Barbell Squats", 4, "8-10", "🏋️", "strength"),
                new Exercise(2, "Deadlifts",      4, "6-8",  "💪", "strength"),
                new Exercise(3, "Leg Press",      3, "12",   "🦵", "strength"),
                new Exercise(4, "Calf Raises",    4, "15",   "🔥", "strength")
            ),
            WorkoutCategory.STRENGTH,
            "Heavy compound lower-body work to build powerful legs and glutes from the ground up.",
            null
        ));

        runtimeCache.add(new Workout(
            4, "Beast Mode HIIT", Workout.Difficulty.ADVANCED, "45 min", true,
            List.of(
                new Exercise(1, "Burpees",           5, "15",     "💥", "cardio"),
                new Exercise(2, "Box Jumps",         4, "12",     "📦", "cardio"),
                new Exercise(3, "Kettlebell Swings", 4, "20",     "⚡", "cardio"),
                new Exercise(4, "Battle Ropes",      4, "30 sec", "🔥", "cardio")
            ),
            WorkoutCategory.HIIT,
            "Maximum-intensity interval training for advanced athletes. Unlock by completing 3 other workouts.",
            null
        ));

        runtimeCache.add(new Workout(
            5, "Champion Circuit", Workout.Difficulty.ADVANCED, "50 min", true,
            List.of(
                new Exercise(1, "Clean & Press",      5, "8",      "🏋️", "strength"),
                new Exercise(2, "Muscle-ups",         4, "5",      "💪", "strength"),
                new Exercise(3, "Pistol Squats",      3, "8 each", "🦵", "strength"),
                new Exercise(4, "Handstand Push-ups", 3, "6",      "🤸", "strength")
            ),
            WorkoutCategory.BALANCE,
            "Elite-level circuit featuring advanced calisthenics and Olympic movements.",
            null
        ));
    }

    // ── Parse helpers ──────────────────────────────────────────────────────

    private static WorkoutCategory parseCategory(String raw) {
        if (raw == null) return WorkoutCategory.STRENGTH;
        try {
            return WorkoutCategory.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return WorkoutCategory.STRENGTH;
        }
    }

    private static Workout.Difficulty parseDifficulty(String raw) {
        if (raw == null) return Workout.Difficulty.BEGINNER;
        try {
            return Workout.Difficulty.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Workout.Difficulty.BEGINNER;
        }
    }
}
