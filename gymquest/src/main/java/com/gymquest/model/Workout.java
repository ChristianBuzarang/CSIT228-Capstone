package com.gymquest.model;

import java.util.List;

/**
 * Workout
 *
 * Represents a structured workout program available in the GymQuest catalog.
 *
 * ── Changes from original ───────────────────────────────────────────────────
 *  Three new fields were added to support the WorkoutService search / filter
 *  pipeline and the enhanced card / detail views:
 *   • {@code category}    – broad training discipline ({@link WorkoutCategory})
 *   • {@code description} – plain-text summary shown in the detail view
 *   • {@code imagePath}   – classpath-relative path to the exercise illustration
 *
 *  The original 6-arg constructor is preserved unchanged so existing callers
 *  (e.g. DataStore pre-migration) continue to compile; the new 9-arg form is
 *  the preferred constructor for newly created workouts.
 */
public class Workout {

    // ── Core fields (unchanged) ────────────────────────────────────────────
    private final int         id;
    private final String      title;
    private final Difficulty  difficulty;
    private final String      duration;
    private final boolean     locked;
    private final List<Exercise> exercises;

    // ── Extended fields (new) ──────────────────────────────────────────────
    private WorkoutCategory   category;    // training discipline
    private String            description; // shown in WorkoutDetailView
    private String            imagePath;   // e.g. "/images/workouts/full-body-blast.png"

    // ── Difficulty enum ───────────────────────────────────────────────────
    public enum Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }

    // ── Constructors ──────────────────────────────────────────────────────

    /**
     * Full constructor – preferred form.
     * Used by {@link DataStore} after the category integration.
     */
    public Workout(int id, String title, Difficulty difficulty, String duration,
                   boolean locked, List<Exercise> exercises,
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

    /**
     * Legacy constructor – preserved for backward compatibility.
     * Category defaults to {@link WorkoutCategory#STRENGTH}, description and
     * imagePath are left {@code null} until set explicitly.
     */
    public Workout(int id, String title, Difficulty difficulty, String duration,
                   boolean locked, List<Exercise> exercises) {
        this(id, title, difficulty, duration, locked, exercises,
             WorkoutCategory.STRENGTH, null, null);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public int    getId()         { return id; }
    public String getTitle()      { return title; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getDuration()   { return duration; }
    public boolean isLocked()     { return locked; }
    public List<Exercise> getExercises() { return exercises; }

    /** @return the training category; never {@code null} after migration */
    public WorkoutCategory getCategory() { return category; }

    /** @return multi-sentence description, or {@code null} if not set */
    public String getDescription() { return description; }

    /** @return classpath-relative image path, or {@code null} if not set */
    public String getImagePath()  { return imagePath; }

    // ── Setters (extended fields only) ────────────────────────────────────

    public void setCategory(WorkoutCategory category) {
        if (category == null) throw new IllegalArgumentException("Category must not be null.");
        this.category = category;
    }

    public void setDescription(String description) { this.description = description; }
    public void setImagePath(String imagePath)     { this.imagePath   = imagePath;   }

    // ── Helpers ────────────────────────────────────────────────────────────

    /** @return human-readable difficulty label for badge display */
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
