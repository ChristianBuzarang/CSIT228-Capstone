package com.gymquest.model;

/**
 * WorkoutCategory
 *
 * Classifies a {@link Workout} into one of six training disciplines.
 * Each constant carries both a human-readable label (for UI display) and
 * a hex accent color (used by cards and filter pills throughout the app).
 *
 * Keeping these values on the enum avoids scattered switch statements in
 * every view that needs to style a category differently.
 */
public enum WorkoutCategory {

    STRENGTH    ("Strength",    "#3b82f6"),   // blue
    CARDIO      ("Cardio",      "#ef4444"),   // red
    CORE        ("Core",        "#6366f1"),   // indigo
    FLEXIBILITY ("Flexibility", "#10b981"),   // emerald
    HIIT        ("HIIT",        "#f97316"),   // orange
    BALANCE     ("Balance",     "#0d9488");   // teal

    // ── Fields ────────────────────────────────────────────────────────────
    private final String label;
    private final String color;   // CSS hex for badge backgrounds, filter pills, etc.

    // ── Constructor ───────────────────────────────────────────────────────
    WorkoutCategory(String label, String color) {
        this.label = label;
        this.color = color;
    }

    // ── Getters ───────────────────────────────────────────────────────────

    /**
     * Human-readable display name, e.g. {@code "Strength"}.
     * Prefer this over {@link #name()} for any UI label.
     */
    public String getLabel() { return label; }

    /**
     * CSS hex accent color for this category, e.g. {@code "#3b82f6"}.
     * Used directly in {@code -fx-background-color} style strings.
     */
    public String getColor() { return color; }

    @Override
    public String toString() { return label; }
}
