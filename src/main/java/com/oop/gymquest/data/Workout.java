package com.oop.gymquest.data;

import java.util.List;

public class Workout {

    // ── Core fields (unchanged) ────────────────────────────────────────────
    private int id;
    private final String title;
    private final Difficulty  difficulty;
    private final String  duration;
    private final boolean locked;
    private final List<Exercise> exercises;

    // ── Extended fields (new) ──────────────────────────────────────────────
    private WorkoutCategory category;    // training discipline
    private String description; // shown in WorkoutDetailView
    private String imagePath;   // e.g. "/images/workouts/full-body-blast.png"

    // ── Difficulty enum ───────────────────────────────────────────────────
    public enum Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }

    // ── Constructors ──────────────────────────────────────────────────────
    public Workout(int id, String title, Difficulty difficulty, String duration,
                   boolean locked, List<Exercise> exercises,
                   WorkoutCategory category, String description, String imagePath) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.duration = duration;
        this.locked = locked;
        this.exercises = exercises;
        this.category = category;
        this.description = description;
        this.imagePath = imagePath;
    }


    public Workout(int id, String title, Difficulty difficulty, String duration,
                   boolean locked, List<Exercise> exercises) {
        this(id, title, difficulty, duration, locked, exercises,
                WorkoutCategory.STRENGTH, null, null);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public int getId() { return id; }
    public String getTitle() { return title; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getDuration() { return duration; }
    public boolean isLocked() { return locked; }
    public List<Exercise> getExercises() { return exercises; }
    public WorkoutCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public String getImagePath()  { return imagePath; }

    // ── Setters (extended fields only) ────────────────────────────────────
    public void setCategory(WorkoutCategory category) {
        if (category == null) throw new IllegalArgumentException("Category must not be null.");
        this.category = category;
    }

    public void setDescription(String description) { this.description = description; }
    public void setImagePath(String imagePath)     { this.imagePath   = imagePath;   }

    // ── Helpers ────────────────────────────────────────────────────────────
    public String getDifficultyLabel() {
        return switch (difficulty) {
            case BEGINNER     -> "Beginner";
            case INTERMEDIATE -> "Intermediate";
            case ADVANCED     -> "Advanced";
        };
    }

    @Override
    public String toString() {
        return String.format("Workout{id=%d, title='%s', category=%s, difficulty=%s}",
                id, title, category, difficulty);
    }
}
