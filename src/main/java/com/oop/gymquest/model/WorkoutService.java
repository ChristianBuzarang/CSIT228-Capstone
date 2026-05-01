package com.oop.gymquest.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class WorkoutService {

    // ── Singleton ──────────────────────────────────────────────────────────
    private static WorkoutService instance;



    public static synchronized WorkoutService getInstance() {
        if (instance == null) instance = new WorkoutService();
        return instance;
    }

    // Private — use getInstance()
    private WorkoutService() {}

    // ── Data access ────────────────────────────────────────────────────────

    public List<Workout> getAllWorkouts() {
        return DataStore.getInstance().getWorkouts();
    }

    public void addWorkout(Workout workout) {
        if (workout.getId() <= 0) {
            int nextId = getAllWorkouts().stream()
                    .mapToInt(Workout::getId)
                    .max()
                    .orElse(0) + 1;
        }
        DataStore.getInstance().addWorkout(workout);
    }

    // ── Query methods ──────────────────────────────────────────────────────

    /**
     * Searches the catalog by {@code keyword}, matching against the workout's
     * title and description (case-insensitive substring match).
     * Passing {@code null} or a blank string returns the full catalog.
     *
     * @param keyword search term
     * @return new list of matching workouts; never {@code null}
     */
    public List<Workout> searchWorkouts(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllWorkouts();
        final String lower = keyword.toLowerCase();
        return getAllWorkouts().stream()
                .filter(w -> w.getTitle().toLowerCase().contains(lower)
                        || (w.getDescription() != null
                            && w.getDescription().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    /**
     * Filters the catalog to workouts belonging to {@code category}.
     * Passing {@code null} returns the full catalog (treated as "All").
     *
     * @param category target {@link WorkoutCategory}, or {@code null} for all
     * @return new list of matching workouts; never {@code null}
     */
    public List<Workout> filterByCategory(WorkoutCategory category) {
        if (category == null) return getAllWorkouts();
        return getAllWorkouts().stream()
                .filter(w -> w.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Combined search + category filter in a single stream pass.
     * This is the primary method used by {@link com.gymquest.view.WorkoutsView}
     * so that typing in the search box and clicking a category pill trigger one
     * efficient operation instead of two sequential filters.
     *
     * @param keyword  search term (blank → match all titles/descriptions)
     * @param category category filter ({@code null} → no category restriction)
     * @return new filtered list; never {@code null}
     */
    public List<Workout> searchAndFilter(String keyword, WorkoutCategory category) {
        final boolean hasKeyword = keyword != null && !keyword.isBlank();
        final String  lower      = hasKeyword ? keyword.toLowerCase() : "";

        return getAllWorkouts().stream()
                .filter(w -> {
                    // keyword check
                    boolean keywordOk = !hasKeyword
                            || w.getTitle().toLowerCase().contains(lower)
                            || (w.getDescription() != null
                                && w.getDescription().toLowerCase().contains(lower));
                    // category check
                    boolean categoryOk = category == null || w.getCategory() == category;
                    return keywordOk && categoryOk;
                })
                .collect(Collectors.toList());
    }

    /**
     * Finds a single workout by its numeric id.
     *
     * @param id the integer id from {@link Workout#getId()}
     * @return {@link Optional} containing the match, or empty if not found
     */
    public Optional<Workout> findById(int id) {
        return getAllWorkouts().stream()
                .filter(w -> w.getId() == id)
                .findFirst();
    }

    /**
     * Total number of workouts in the catalog.
     *
     * @return catalog size
     */
    public int getTotalCount() {
        return getAllWorkouts().size();
    }
}
