//package com.oop.gymquest.model;
//
//import java.util.*;
//
//
//public class DataStore {
//
//    // ── Singleton ──────────────────────────────────────────────────────────
//    private static DataStore instance;
//
//    public static synchronized DataStore getInstance() {
//        if (instance == null) instance = new DataStore();
//        return instance;
//    }
//
//    // ── Collections ────────────────────────────────────────────────────────
//    private final List<Workout>        workouts;
//    private final List<User>           users;
//    private final List<Post>           posts;
//    private final List<TrainerSession> sessions;
//    private final List<Exercise>       exerciseLibrary;
//
//
//    private DataStore() {
//        workouts = new ArrayList<>(buildWorkouts());
//        users = buildUsers();
//        posts = buildPosts();
//        sessions = buildSessions();
//        exerciseLibrary = new ArrayList<>(buildExerciseLibrary());
//    }
//
//    public void addWorkout(Workout w) {
//        this.workouts.add(w);
//    }
//
//    public void addExercise(Exercise exercise) {
//        this.exerciseLibrary.add(exercise);
//    }
//
//    // ── Public accessors ───────────────────────────────────────────────────
//    public List<Workout>        getWorkouts()        { return workouts; }
//    public List<User>           getUsers()           { return new ArrayList<>(users); }
//    public List<Post>           getPosts()           { return posts; }
//    public List<TrainerSession> getSessions()        { return sessions; }
//    public List<Exercise>       getExerciseLibrary() { return exerciseLibrary; }
//
//    // ── Builders ───────────────────────────────────────────────────────────
//
//
//
//    private List<Workout> buildWorkouts() {
//        return List.of(
//
//                // ── Beginner ──────────────────────────────────────────────────
//                new Workout(
//                        0, "Full Body Blast", Workout.Difficulty.BEGINNER, "30 min", false,
//                        List.of(
//                                new Exercise(1, "Push-ups",  3, "10-12",   "💪"),
//                                new Exercise(2, "Squats",    3, "15",      "🦵"),
//                                new Exercise(3, "Plank",     3, "30 sec",  "🧘"),
//                                new Exercise(4, "Lunges",    3, "10 each", "🏃")
//                        ),
//                        WorkoutCategory.STRENGTH,
//                        "A complete beginner-friendly routine targeting all major muscle groups " +
//                                "through fundamental movement patterns. Perfect for building a solid " +
//                                "foundation of strength and body control.",
//                        "/images/workouts/full-body-blast.png"
//                ),
//
//                new Workout(
//                        1, "Upper Body Power", Workout.Difficulty.BEGINNER, "25 min", false,
//                        List.of(
//                                new Exercise(1, "Dumbbell Press", 4, "8-10", "🏋️"),
//                                new Exercise(2, "Pull-ups",       3, "6-8",  "💪"),
//                                new Exercise(3, "Bicep Curls",    3, "12",   "💪"),
//                                new Exercise(4, "Tricep Dips",    3, "10",   "🔥")
//                        ),
//                        WorkoutCategory.STRENGTH,
//                        "Build upper-body strength and muscle with compound pushing and pulling " +
//                                "exercises. Targets chest, back, biceps, and triceps for balanced " +
//                                "upper-body development.",
//                        "/images/workouts/upper-body-power.png"
//                ),
//
//                // ── Intermediate ──────────────────────────────────────────────
//                new Workout(
//                        2, "Core Crusher", Workout.Difficulty.INTERMEDIATE, "20 min", false,
//                        List.of(
//                                new Exercise(1, "Crunches",          4, "20",     "🔥"),
//                                new Exercise(2, "Russian Twists",    3, "15 each","🌀"),
//                                new Exercise(3, "Leg Raises",        3, "12",     "🦵"),
//                                new Exercise(4, "Mountain Climbers", 3, "20",     "⛰️")
//                        ),
//                        WorkoutCategory.CORE,
//                        "Intense core-focused routine to build a strong, stable midsection. " +
//                                "Hits the rectus abdominis, obliques, and deep stabilizers for " +
//                                "functional abdominal strength that carries over to every lift.",
//                        "/images/workouts/core-crusher.png"
//                ),
//
//                new Workout(
//                        3, "Leg Day Legends", Workout.Difficulty.INTERMEDIATE, "40 min", false,
//                        List.of(
//                                new Exercise(1, "Barbell Squats", 4, "8-10", "🏋️"),
//                                new Exercise(2, "Deadlifts",      4, "6-8",  "💪"),
//                                new Exercise(3, "Leg Press",      3, "12",   "🦵"),
//                                new Exercise(4, "Calf Raises",    4, "15",   "🔥")
//                        ),
//                        WorkoutCategory.STRENGTH,
//                        "Heavy compound lower-body work to build powerful legs and glutes from " +
//                                "the ground up. Squats and deadlifts form the backbone of this session — " +
//                                "expect to earn every rep.",
//                        "/images/workouts/leg-day-legends.png"
//                ),
//
//                // ── Advanced (locked) ─────────────────────────────────────────
//                new Workout(
//                        4, "Beast Mode HIIT", Workout.Difficulty.ADVANCED, "45 min", true,
//                        List.of(
//                                new Exercise(1, "Burpees",           5, "15",     "💥"),
//                                new Exercise(2, "Box Jumps",         4, "12",     "📦"),
//                                new Exercise(3, "Kettlebell Swings", 4, "20",     "⚡"),
//                                new Exercise(4, "Battle Ropes",      4, "30 sec", "🔥")
//                        ),
//                        WorkoutCategory.HIIT,
//                        "Maximum-intensity interval training for advanced athletes. Explosive " +
//                                "compound movements push your cardiovascular capacity and muscular " +
//                                "endurance to their absolute limits. Not for the faint-hearted.",
//                        "/images/workouts/beast-mode-hiit.png"
//                ),
//
//                new Workout(
//                        5, "Champion Circuit", Workout.Difficulty.ADVANCED, "50 min", true,
//                        List.of(
//                                new Exercise(1, "Clean & Press",       5, "8",      "🏋️"),
//                                new Exercise(2, "Muscle-ups",          4, "5",      "💪"),
//                                new Exercise(3, "Pistol Squats",       3, "8 each", "🦵"),
//                                new Exercise(4, "Handstand Push-ups",  3, "6",      "🤸")
//                        ),
//                        WorkoutCategory.BALANCE,
//                        "Elite-level circuit featuring advanced calisthenics and Olympic " +
//                                "movements. Demands superior balance, proprioception, and full-body " +
//                                "strength. Reserved for athletes at peak conditioning.",
//                        "/images/workouts/champion-circuit.png"
//                )
//        );
//    }
//
//    // ── Remaining builders (unchanged) ─────────────────────────────────────
//
//    private List<User> buildUsers() {
//        return new ArrayList<>(List.of(
////                new User(1, "Alex Chen",      "alex@email.com",        "member",  "🧑"),
////                new User(2, "Sarah Johnson",  "sarah@email.com",       "member",  "👩"),
////                new User(3, "Mike Davis",     "mike@email.com",        "member",  "👨"),
////                new User(4, "Coach Alex",     "coach.alex@email.com",  "trainer", "🏋️"),
////                new User(5, "Coach Sam",      "coach.sam@email.com",   "trainer", "🏃"),
////                new User(6, "Coach Jordan",   "coach.jordan@email.com","trainer", "🧘")
//        ));
//    }
//
//    private List<Post> buildPosts() {
//        return new ArrayList<>(List.of(
//                new Post(1, "Alex Chen",      "🧑", Post.PostType.STREAK,  "reached a 30-day streak!",                    "30 🔥", "2h ago", 24, false),
//                new Post(2, "Sarah Johnson",  "👩",  Post.PostType.BADGE,   "unlocked the \"Iron Will\" badge!",           "💪",    "5h ago", 18, true),
//                new Post(3, "Mike Davis",     "👨",  Post.PostType.WORKOUT, "completed 100 total workouts!",               "💯",    "1d ago", 42, false),
//                new Post(4, "Emma Wilson",    "👧",  Post.PostType.GOAL,    "crushed this week's goal - 5 workouts in 5 days!", "🎯","1d ago", 31, false),
//                new Post(5, "Chris Lee",      "🧔",  Post.PostType.STREAK,  "hit a 15-day streak!",                        "15 🔥", "2d ago", 15, true),
//                new Post(6, "Jessica Park",   "👩",  Post.PostType.WORKOUT, "completed Beast Mode HIIT for the first time!", "⚡",  "2d ago", 28, false)
//        ));
//    }
//
//    private List<TrainerSession> buildSessions() {
//        return new ArrayList<>(List.of(
//                new TrainerSession(1, "2026-05-02", "10:00 AM", 60, "Strength Training", true,  "Alex Chen"),
//                new TrainerSession(2, "2026-05-02", "2:00 PM",  45, "HIIT Session",      false, null),
//                new TrainerSession(3, "2026-05-03", "9:00 AM",  60, "Personal Training", false, null),
//                new TrainerSession(4, "2026-05-04", "3:00 PM",  45, "HIIT Session",      true,  "Sarah Johnson")
//        ));
//    }
//
//    private List<Exercise> buildExerciseLibrary() {
//        return List.of(
//                new Exercise(1,  "Push-ups",           3, "10-12",  "💪", "strength"),
//                new Exercise(2,  "Squats",             3, "15",     "🦵", "strength"),
//                new Exercise(3,  "Plank",              3, "30 sec", "🧘", "core"),
//                new Exercise(4,  "Lunges",             3, "10 each","🏃", "strength"),
//                new Exercise(5,  "Burpees",            4, "10",     "💥", "cardio"),
//                new Exercise(6,  "Mountain Climbers",  3, "20",     "⛰️","cardio"),
//                new Exercise(7,  "Dumbbell Press",     4, "8-10",   "🏋️","strength"),
//                new Exercise(8,  "Pull-ups",           3, "6-8",    "💪", "strength"),
//                new Exercise(9,  "Bicep Curls",        3, "12",     "💪", "strength"),
//                new Exercise(10, "Tricep Dips",        3, "10",     "🔥", "strength"),
//                new Exercise(11, "Crunches",           4, "20",     "🔥", "core"),
//                new Exercise(12, "Russian Twists",     3, "15 each","🌀", "core"),
//                new Exercise(13, "Leg Raises",         3, "12",     "🦵", "core"),
//                new Exercise(14, "Jumping Jacks",      3, "30",     "⚡", "cardio"),
//                new Exercise(15, "Running",            1, "20 min", "🏃", "cardio"),
//                new Exercise(16, "Jump Rope",          3, "1 min",  "🪢", "cardio"),
//                new Exercise(17, "Yoga Stretches",     1, "10 min", "🧘", "flexibility"),
//                new Exercise(18, "Hamstring Stretch",  2, "30 sec", "🦵", "flexibility"),
//                new Exercise(19, "Shoulder Stretch",   2, "30 sec", "💪", "flexibility"),
//                new Exercise(20, "Deadlifts",          4, "6-8",    "🏋️","strength")
//        );
//    }
//}
