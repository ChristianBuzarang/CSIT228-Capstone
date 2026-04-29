package com.gymquest.model;

import java.util.List;

public class Workout {
    private int id;
    private String title;
    private Difficulty difficulty;
    private String duration;
    private boolean locked;
    private List<Exercise> exercises;

    public enum Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }

    public Workout(int id, String title, Difficulty difficulty, String duration,
                   boolean locked, List<Exercise> exercises) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.duration = duration;
        this.locked = locked;
        this.exercises = exercises;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getDifficultyLabel() {
        return switch (difficulty) {
            case BEGINNER -> "Beginner";
            case INTERMEDIATE -> "Intermediate";
            case ADVANCED -> "Advanced";
        };
    }
    public String getDuration() { return duration; }
    public boolean isLocked() { return locked; }
    public List<Exercise> getExercises() { return exercises; }
}
