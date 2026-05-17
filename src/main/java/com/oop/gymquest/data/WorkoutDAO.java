package com.oop.gymquest.data;

import com.oop.gymquest.data.workoutdata.Exercise;
import com.oop.gymquest.data.workoutdata.Workout;
import com.oop.gymquest.data.workoutdata.WorkoutCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkoutDAO {
    private static final List<Workout> runtimeCache = new ArrayList<>();
    private static boolean cacheInitialized = false;

    public static List<Workout> getAllWorkouts(int userId) {
        if (!cacheInitialized) initializeCache();

        return runtimeCache.stream()
                .filter(w -> !w.isCustom() || w.getCreatedBy() == userId)
                .toList();
    }

    @Deprecated
    public static List<Workout> getAllWorkouts() {
        if (!cacheInitialized) initializeCache();
        return Collections.unmodifiableList(runtimeCache);
    }

    public static boolean createCustomWorkout(Workout workout, int userId) {
        if (!cacheInitialized) initializeCache();

        workout.setCustom(true);
        workout.setCreatedBy(userId);

        boolean dbSuccess = false;

        String workoutSql =
                "INSERT INTO workouts "
                        + "(title, difficulty, duration, category, description, is_custom, locked, created_by) "
                        + "VALUES (?, ?, ?, ?, ?, 1, 0, ?)";

        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(workoutSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, workout.getTitle());
            ps.setString(2, workout.getDifficulty().name());
            ps.setString(3, workout.getDuration());
            ps.setString(4, workout.getCategory().name());
            ps.setString(5, workout.getDescription());

            if (userId > 0) {
                ps.setInt(6, userId);
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            dbSuccess = ps.executeUpdate() > 0;

            if (dbSuccess) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) workout.setId(keys.getInt(1));
                }
                saveWorkoutExercises(c, workout.getId(), workout.getExercises());
            }

        } catch (Exception e) {
            System.err.println("[WorkoutDAO] DB write failed: " + e.getMessage());
        }

        runtimeCache.add(workout);
        return dbSuccess;
    }

    @Deprecated
    public static boolean createCustomWorkout(Workout workout) {
        return createCustomWorkout(workout, 0);
    }

    public static boolean attachWorkoutToSlot(int workoutId, int slotId, int requesterId) {
        // Guard: requester must be the booked member for this slot
        if (!isBookedMember(slotId, requesterId)) {
            System.err.println("[WorkoutDAO] attachWorkoutToSlot denied: user "
                    + requesterId + " is not the booked member for slot " + slotId);
            return false;
        }

        String sql = "INSERT IGNORE INTO slot_workouts (slot_id, workout_id) VALUES (?, ?)";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            ps.setInt(2, workoutId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] attachWorkoutToSlot DB error: " + e.getMessage());
            return false;
        }
    }

    public static boolean detachWorkoutFromSlot(int workoutId, int slotId, int requesterId) {
        if (!canAccessSlot(slotId, requesterId)) {
            System.err.println("[WorkoutDAO] detachWorkoutFromSlot denied for user " + requesterId);
            return false;
        }
        String sql = "DELETE FROM slot_workouts WHERE slot_id = ? AND workout_id = ?";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            ps.setInt(2, workoutId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] detachWorkoutFromSlot DB error: " + e.getMessage());
            return false;
        }
    }

    public static List<Workout> getWorkoutsForSlot(int slotId, int requesterId) {
        if (!canAccessSlot(slotId, requesterId)) {
            System.err.println("[WorkoutDAO] getWorkoutsForSlot denied for user " + requesterId);
            return Collections.emptyList();
        }

        List<Workout> result = new ArrayList<>();

        String sql =
                "SELECT w.id, w.title, w.difficulty, w.duration, w.category, "
                        + "       w.description, w.locked, w.is_custom, w.created_by "
                        + "FROM workouts w "
                        + "JOIN slot_workouts sw ON sw.workout_id = w.id "
                        + "WHERE sw.slot_id = ?";

        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapWorkoutRow(c, rs));
                }
            }
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] getWorkoutsForSlot DB error: " + e.getMessage());
        }

        return Collections.unmodifiableList(result);
    }

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
            System.err.println("[WorkoutDAO] DB delete failed: " + e.getMessage());
        }
        return true;
    }

    public static synchronized void invalidateCache() {
        runtimeCache.clear();
        cacheInitialized = false;
    }

    private static synchronized void initializeCache() {
        if (cacheInitialized) return;
        try {
            loadFromDatabase();
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] DB unavailable, starting with empty catalog: "
                    + e.getMessage());
        }
        cacheInitialized = true;
    }

    private static void loadFromDatabase() throws SQLException {
        String workoutSql =
                "SELECT id, title, difficulty, duration, category, description, "
                        + "       locked, is_custom, created_by "
                        + "FROM workouts ORDER BY id";

        try (Connection c = MySQLConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(workoutSql)) {

            while (rs.next()) {
                runtimeCache.add(mapWorkoutRow(c, rs));
            }
        }
    }

    private static Workout mapWorkoutRow(Connection c, ResultSet rs) throws SQLException {
        int wid = rs.getInt("id");
        List<Exercise> exercises = loadExercisesForWorkout(c, wid);
        WorkoutCategory    cat  = parseCategory  (rs.getString("category"));
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
        w.setCustom   (rs.getBoolean("is_custom"));
        w.setCreatedBy(rs.getInt    ("created_by"));
        return w;
    }

    private static List<Exercise> loadExercisesForWorkout(Connection c, int workoutId)
            throws SQLException {
        String sql =
                "SELECT id, name, sets, reps, emoji, category "
                        + "FROM workout_exercises WHERE workout_id = ? ORDER BY sort_order";

        List<Exercise> exercises = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, workoutId);
            try (ResultSet ers = ps.executeQuery()) {
                while (ers.next()) {
                    exercises.add(new Exercise(
                            ers.getInt   ("id"),
                            ers.getString("name"),
                            ers.getInt   ("sets"),
                            ers.getString("reps"),
                            ers.getString("emoji"),
                            ers.getString("category")
                    ));
                }
            }
        }
        return exercises;
    }

    private static boolean isBookedMember(int slotId, int userId) {
        String sql = "SELECT 1 FROM trainer_slots WHERE slot_id = ? AND member_id = ?";
        return slotAccessCheck(sql, slotId, userId);
    }

    private static boolean canAccessSlot(int slotId, int userId) {
        String sql =
                "SELECT 1 FROM trainer_slots "
                        + "WHERE slot_id = ? AND (member_id = ? OR trainer_id = ?)";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] canAccessSlot error: " + e.getMessage());
            return false;
        }
    }

    private static boolean slotAccessCheck(String sql, int slotId, int userId) {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("[WorkoutDAO] slotAccessCheck error: " + e.getMessage());
            return false;
        }
    }

    private static void saveWorkoutExercises(Connection c, int workoutId, List<Exercise> exercises) throws SQLException {
        if (exercises == null || exercises.isEmpty()) return;
        String sql =
                "INSERT INTO workout_exercises "
                        + "(workout_id, name, sets, reps, emoji, category, sort_order) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < exercises.size(); i++) {
                Exercise ex = exercises.get(i);
                ps.setInt   (1, workoutId);
                ps.setString(2, ex.getName());
                ps.setInt   (3, ex.getSets());
                ps.setString(4, ex.getReps());
                ps.setString(5, ex.getEmoji());
                ps.setString(6, ex.getCategory() != null ? ex.getCategory() : "strength");
                ps.setInt   (7, i);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

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