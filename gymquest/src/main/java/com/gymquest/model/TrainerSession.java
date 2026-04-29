package com.gymquest.model;

public class TrainerSession {
    private int id;
    private String date;
    private String time;
    private int duration;
    private String type;
    private boolean booked;
    private String bookedBy;

    public TrainerSession(int id, String date, String time, int duration, String type, boolean booked, String bookedBy) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.type = type;
        this.booked = booked;
        this.bookedBy = bookedBy;
    }

    public TrainerSession(int id, String date, String time, int duration, String type) {
        this(id, date, time, duration, type, false, null);
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getDuration() { return duration; }
    public String getType() { return type; }
    public boolean isBooked() { return booked; }
    public String getBookedBy() { return bookedBy; }

    public void setBooked(boolean booked) { this.booked = booked; }
    public void setBookedBy(String bookedBy) { this.bookedBy = bookedBy; }
}
