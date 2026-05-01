package com.oop.gymquest.exceptions;

public class BookingConflictException extends Exception {
    public BookingConflictException(String trainerName, String timeSlot) {
        super("Booking Error: Trainer '" + trainerName + "' is already scheduled for the '" + timeSlot + "' session.");
    }
}
