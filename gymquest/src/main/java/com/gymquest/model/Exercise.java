package com.gymquest.model;

public class Exercise {
    private int id;
    private String name;
    private int sets;
    private String reps;
    private String emoji;
    private String category; // strength, cardio, core, flexibility

    public Exercise(int id, String name, int sets, String reps, String emoji) {
        this.id = id;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.emoji = emoji;
        this.category = "strength";
    }

    public Exercise(int id, String name, int sets, String reps, String emoji, String category) {
        this(id, name, sets, reps, emoji);
        this.category = category;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getSets() { return sets; }
    public String getReps() { return reps; }
    public String getEmoji() { return emoji; }
    public String getCategory() { return category; }

    public void setSets(int sets) { this.sets = sets; }
    public void setReps(String reps) { this.reps = reps; }

    @Override
    public String toString() {
        return name + " - " + sets + " sets × " + reps;
    }
}
