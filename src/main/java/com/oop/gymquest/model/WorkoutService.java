//package com.oop.gymquest.model;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//
//public class WorkoutService {
//
//    // ── Singleton ──────────────────────────────────────────────────────────
//    private static WorkoutService instance;
//
//    public static synchronized WorkoutService getInstance() {
//        if (instance == null) instance = new WorkoutService();
//        return instance;
//    }
//
//    // Private — use getInstance()
//    private WorkoutService() {}
//
//    // ── Data access ────────────────────────────────────────────────────────
//
//    public List<Workout> getAllWorkouts() {
//        return DataStore.getInstance().getWorkouts();
//    }
//
//    public void addWorkout(Workout workout) {
//        if (workout.getId() <= 0) {
//            int nextId = getAllWorkouts().stream()
//                    .mapToInt(Workout::getId)
//                    .max()
//                    .orElse(0) + 1;
//        }
//        DataStore.getInstance().addWorkout(workout);
//    }
//
//    // ── Query methods ──────────────────────────────────────────────────────
//
//    public List<Workout> searchWorkouts(String keyword) {
//        if (keyword == null || keyword.isBlank()) return getAllWorkouts();
//        final String lower = keyword.toLowerCase();
//        return getAllWorkouts().stream()
//                .filter(w -> w.getTitle().toLowerCase().contains(lower)
//                        || (w.getDescription() != null
//                            && w.getDescription().toLowerCase().contains(lower)))
//                .collect(Collectors.toList());
//    }
//
//
//    public List<Workout> filterByCategory(WorkoutCategory category) {
//        if (category == null) return getAllWorkouts();
//        return getAllWorkouts().stream()
//                .filter(w -> w.getCategory() == category)
//                .collect(Collectors.toList());
//    }
//
//
//    public List<Workout> searchAndFilter(String keyword, WorkoutCategory category) {
//        final boolean hasKeyword = keyword != null && !keyword.isBlank();
//        final String  lower      = hasKeyword ? keyword.toLowerCase() : "";
//
//        return getAllWorkouts().stream()
//                .filter(w -> {
//                    // keyword check
//                    boolean keywordOk = !hasKeyword
//                            || w.getTitle().toLowerCase().contains(lower)
//                            || (w.getDescription() != null
//                                && w.getDescription().toLowerCase().contains(lower));
//                    // category check
//                    boolean categoryOk = category == null || w.getCategory() == category;
//                    return keywordOk && categoryOk;
//                })
//                .collect(Collectors.toList());
//    }
//
//
//    public Optional<Workout> findById(int id) {
//        return getAllWorkouts().stream()
//                .filter(w -> w.getId() == id)
//                .findFirst();
//    }
//
//    public int getTotalCount() {
//        return getAllWorkouts().size();
//    }
//}
