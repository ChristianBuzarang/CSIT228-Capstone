package com.oop.gymquest.data.workoutdata;

import java.util.List;

public class Workout {
    private int              id;
    private final String     title;
    private final Difficulty difficulty;
    private final String     duration;
    private final boolean    locked;
    private final List<Exercise> exercises;

    private WorkoutCategory  category;
    private String           description;
    private String           imagePath;
    private boolean          custom   = false;

    private int              createdBy = 0;
    public enum Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }

    public Workout(int id, String title, Difficulty difficulty, String duration, boolean locked, List<Exercise> exercises,
                   WorkoutCategory category, String description, String imagePath) {
        this.id          = id;
        this.title       = title;
        this.difficulty  = difficulty;
        this.duration    = duration;
        this.locked      = locked;
        this.exercises   = exercises;
        this.category    = category;
        this.description = description;
        this.imagePath   = imagePath;
    }

    public Workout(int id, String title, Difficulty difficulty, String duration, boolean locked, List<Exercise> exercises) {
        this(id, title, difficulty, duration, locked, exercises, WorkoutCategory.STRENGTH, null, null);
    }

    public int             getId()                  { return id; }
    public String          getTitle()               { return title; }
    public Difficulty      getDifficulty()          { return difficulty; }
    public String          getDuration()            { return duration; }
    public boolean         isLocked()               { return locked; }
    public List<Exercise>  getExercises()           { return exercises; }
    public WorkoutCategory getCategory()            { return category; }
    public String          getDescription()         { return description; }
    public String          getImagePath()           { return imagePath; }
    public boolean         isCustom()               { return custom; }

    public int             getCreatedBy()           { return createdBy; }
    public void            setId(int id)            { this.id = id; }
    public void            setCustom(boolean custom){ this.custom = custom; }
    public void            setCreatedBy(int id)     { this.createdBy = id; }

    public String getDifficultyLabel() {
        return switch (difficulty) {
            case BEGINNER     -> "Beginner";
            case INTERMEDIATE -> "Intermediate";
            case ADVANCED     -> "Advanced";
        };
    }

    @Override
    public String toString() {
        return String.format(
            "Workout{id=%d, title='%s', category=%s, difficulty=%s, custom=%b, createdBy=%d}",
            id, title, category, difficulty, custom, createdBy);
    }
}
