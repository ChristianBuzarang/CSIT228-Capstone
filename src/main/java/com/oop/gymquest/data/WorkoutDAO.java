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
 * In-memory + database-backed catalog of workouts.
 *
 * ── Design decisions ───────────────────────────────────────────────────────
 *  • No hardcoded default workouts.  The catalog starts empty and is populated
 *    entirely from the database (when connected) or by the user via the Custom
 *    Workout Creator.  This reflects the project requirement that users create
 *    their own workouts.
 *  • Runtime cache — mutations (add / remove) apply to the in-memory list
 *    immediately so the UI always shows the current state without reloading.
 *  • DB writes are best-effort: if MySQL is unavailable the in-memory operation
 *    still succeeds so the session keeps working.
 */
public class WorkoutDAO {

    // ── In-memory cache ────────────────────────────────────────────────────
    private static final List<Workout> runtimeCache = new ArrayList<>();
    private static boolean cacheInitialized = false;

    // ── Public API ─────────────────────────────────────────────────────────

    /**
     * Returns the live workout catalog.
     * Tries the database on the first call; stays empty if DB is unavailable —
     * no hardcoded defaults are injected.
     */
    public static List<Workout> getAllWorkouts() {
        if (!cacheInitialized) initializeCache();
        return Collections.unmodifiableList(runtimeCache);
    }

    /**
     * Persists a custom workout to the DB and immediately adds it to the
     * runtime cache so the workouts view shows it on navigation back.
     * {@code workout.setCustom(true)} is called here regardless of DB outcome.
     *
     * @return {@code true} if the DB write succeeded
     */
    public static boolean createCustomWorkout(Workout workout) {
        if (!cacheInitialized) initializeCache();

        workout.setCustom(true);

        boolean dbSuccess = false;
        String sql =
            "INSERT INTO workouts (title, difficulty, duration, category, description, is_custom, locked)"
          + " VALUES (?, ?, ?, ?, ?, 1, 0)";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, workout.getTitle());
            ps.setString(2, workout.getDifficulty().name());
            ps.setString(3, workout.getDuration());
            ps.setString(4, workout.getCategory().name());
            ps.setString(5, workout.getDescription());
            dbSuccess = ps.executeUpdate() > 0;

            // Back-fill the auto-generated DB id into the object so future
            // deletes can match by the real DB id.
            if (dbSuccess) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) workout.setId(keys.getInt(1));
                }
            }
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] DB write failed (in-memory only): " + e.getMessage());
        }

        runtimeCache.add(workout);
        return dbSuccess;
    }

    /**
     * Removes a custom workout by id from both the runtime cache and the DB.
     *
     * Called as {@code WorkoutDAO.removeWorkout(id)} — method name matches
     * what {@link com.oop.gymquest.screens.workouts.WorkoutsViewController}
     * uses in {@code confirmAndDelete()}.
     *
     * @param workoutId the {@link Workout#getId()} value to remove
     * @return {@code true} if the workout was found and removed from the cache
     */
    public static boolean removeWorkout(int workoutId) {
        boolean removed = runtimeCache.removeIf(w -> w.getId() == workoutId);

        if (!removed) {
            System.err.println("[WorkoutDAO] removeWorkout: id " + workoutId + " not found in cache.");
            return false;
        }

        // Best-effort DB removal
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
            // DB not available — start with empty list; user creates their own workouts
            System.err.println("[WorkoutDAO] DB unavailable, starting with empty catalog: " + e.getMessage());
        }
        cacheInitialized = true;
    }

    /**
     * Queries the {@code workouts} table and populates the cache.
     * Exercises are fetched via a second query keyed on workout_id.
     */
    private static void loadFromDatabase() throws SQLException {
        String workoutSql =
            "SELECT id, title, difficulty, duration, category, description, locked, is_custom"
          + " FROM workouts ORDER BY id";
        String exerciseSql =
            "SELECT id, name, sets, reps, emoji, category FROM workout_exercises"
          + " WHERE workout_id = ? ORDER BY sort_order";

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
                                ers.getString("emoji"),
                                ers.getString("category")
                            ));
                        }
                    }
                } catch (SQLException ex) {
                    // workout_exercises table may not exist yet — skip exercises
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
