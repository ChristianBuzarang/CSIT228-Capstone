package com.oop.gymquest.data;

import com.oop.gymquest.data.workoutdata.Exercise;
import com.oop.gymquest.data.workoutdata.Workout;
import com.oop.gymquest.data.workoutdata.WorkoutCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WorkoutDAO
 *
 * ── Bug fixes in this version ──────────────────────────────────────────────
 *  FIX 1 — createCustomWorkout() now also INSERTs each Exercise into the
 *           workout_exercises table.  Previously only the workout header row
 *           was saved, so exercises were lost on app restart.
 *
 *  FIX 2 — loadFromDatabase() reads workout_exercises to rehydrate exercises.
 *           The query now works because DatabaseHandler.init() creates both
 *           the workouts and workout_exercises tables.
 *
 *  FIX 3 — removeWorkout() uses a single DELETE on workouts; the workout_exercises
 *           rows are removed automatically via ON DELETE CASCADE.
 */
public class WorkoutDAO {

    // ── In-memory cache ────────────────────────────────────────────────────
    private static final List<Workout> runtimeCache = new ArrayList<>();
    private static boolean cacheInitialized = false;

    // ── Public API ─────────────────────────────────────────────────────────

    public static List<Workout> getAllWorkouts() {
        if (!cacheInitialized) initializeCache();
        return Collections.unmodifiableList(runtimeCache);
    }

    /**
     * Saves a custom workout (header + all exercises) to the DB and appends
     * it to the runtime cache.
     *
     * FIX: exercises are now persisted to workout_exercises so they survive
     * an app restart.
     */
    public static boolean createCustomWorkout(Workout workout) {
        if (!cacheInitialized) initializeCache();

        workout.setCustom(true);

        boolean dbSuccess = false;

        // ── 1. Insert workout header ───────────────────────────────────────
        String workoutSql =
            "INSERT INTO workouts (title, difficulty, duration, category, description, is_custom, locked)"
          + " VALUES (?, ?, ?, ?, ?, 1, 0)";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(workoutSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, workout.getTitle());
            ps.setString(2, workout.getDifficulty().name());
            ps.setString(3, workout.getDuration());
            ps.setString(4, workout.getCategory().name());
            ps.setString(5, workout.getDescription());
            dbSuccess = ps.executeUpdate() > 0;

            if (dbSuccess) {
                // Back-fill the auto-generated DB id so removeWorkout() matches correctly
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        workout.setId(keys.getInt(1));
                    }
                }

                // ── 2. Insert each exercise ────────────────────────────────
                saveWorkoutExercises(c, workout.getId(), workout.getExercises());
            }

        } catch (Exception e) {
            System.err.println("[WorkoutDAO] DB write failed (in-memory only): " + e.getMessage());
        }

        runtimeCache.add(workout);
        return dbSuccess;
    }

    /**
     * Inserts all exercises for a workout into workout_exercises.
     * Uses a batch insert for efficiency.
     */
    private static void saveWorkoutExercises(Connection c, int workoutId,
                                             List<Exercise> exercises) throws SQLException {
        if (exercises == null || exercises.isEmpty()) return;

        String sql =
            "INSERT INTO workout_exercises (workout_id, name, sets, reps, category, sort_order)"
          + " VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < exercises.size(); i++) {
                Exercise ex = exercises.get(i);
                ps.setInt(1, workoutId);
                ps.setString(2, ex.getName());
                ps.setInt(3, ex.getSets());
                ps.setString(4, ex.getReps());
                ps.setString(5, ex.getCategory() != null ? ex.getCategory() : "strength");
                ps.setInt(6, i);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Removes a workout by id.
     * ON DELETE CASCADE on workout_exercises removes the exercise rows automatically.
     */
    public static boolean removeWorkout(int workoutId) {
        boolean removed = runtimeCache.removeIf(w -> w.getId() == workoutId);

        if (!removed) {
            System.err.println("[WorkoutDAO] removeWorkout: id " + workoutId + " not found in cache.");
            return false;
        }

        String sql = "DELETE FROM workouts WHERE id = ?";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, workoutId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] DB delete failed (removed from cache): " + e.getMessage());
        }

        return true;
    }

    // ── Cache initialization ───────────────────────────────────────────────

    private static synchronized void initializeCache() {
        if (cacheInitialized) return;
        try {
            loadFromDatabase();
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] DB unavailable, starting with empty catalog: " + e.getMessage());
        }
        cacheInitialized = true;
    }

    /**
     * Loads all workouts and their exercises from the DB.
     * FIX: now reads from workout_exercises (was silently skipping due to missing table).
     */
    private static void loadFromDatabase() throws SQLException {
        String workoutSql =
            "SELECT id, title, difficulty, duration, category, description, locked, is_custom"
          + " FROM workouts ORDER BY id";
        String exerciseSql =
            "SELECT id, name, sets, reps, emoji, category"
          + " FROM workout_exercises WHERE workout_id = ? ORDER BY sort_order";

        try (Connection c = MySQLConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(workoutSql)) {

            while (rs.next()) {
                int wid = rs.getInt("id");

                // Load exercises for this workout
                List<Exercise> exercises = new ArrayList<>();
                try (PreparedStatement eps = c.prepareStatement(exerciseSql)) {
                    eps.setInt(1, wid);
                    try (ResultSet ers = eps.executeQuery()) {
                        while (ers.next()) {
                            exercises.add(new Exercise(
                                ers.getInt("id"),
                                ers.getString("name"),
                                ers.getInt("sets"),
                                ers.getString("reps"),
                                ers.getString("category")
                            ));
                        }
                    }
                }

                WorkoutCategory cat  = parseCategory(rs.getString("category"));
                Workout.Difficulty diff = parseDifficulty(rs.getString("difficulty"));

                Workout w = new Workout(
                    wid,
                    rs.getString("title"),
                    diff,
                    rs.getString("duration"),
                    rs.getBoolean("locked"),
                    exercises,
                    cat,
                    rs.getString("description"),
                    null
                );
                w.setCustom(rs.getBoolean("is_custom"));
                runtimeCache.add(w);
            }
        }
    }

    // ── Parse helpers ──────────────────────────────────────────────────────

    private static WorkoutCategory parseCategory(String raw) {
        if (raw == null) return WorkoutCategory.STRENGTH;
        try { return WorkoutCategory.valueOf(raw.toUpperCase()); }
        catch (IllegalArgumentException e) { return WorkoutCategory.STRENGTH; }
    }

    private static Workout.Difficulty parseDifficulty(String raw) {
        if (raw == null) return Workout.Difficulty.BEGINNER;
        try { return Workout.Difficulty.valueOf(raw.toUpperCase()); }
        catch (IllegalArgumentException e) { return Workout.Difficulty.BEGINNER; }
    }
}
