package com.example.medassist.ui.medication;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Medication {
    private String name;
    private String dosage;
    private String frequency;
    private List<String> notificationTimes;
    private String sideEffects;
    private LocalDate date;
    private String foodRelation;
    private long id;

    public Medication(long id, String name, String dosage, String frequency, List<String> notificationTimes, String sideEffects) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.notificationTimes = notificationTimes != null ? notificationTimes : new ArrayList<>();
        this.sideEffects = sideEffects;
    }

    // Legacy constructor for backward compatibility
    public Medication(long id, String name, String dosage, String frequency, String time, String sideEffects) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.notificationTimes = new ArrayList<>();
        if (time != null && !time.isEmpty()) {
            this.notificationTimes.add(time);
        }
        this.sideEffects = sideEffects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<String> getNotificationTimes() {
        return notificationTimes;
    }

    public void setNotificationTimes(List<String> notificationTimes) {
        this.notificationTimes = notificationTimes;
    }

    // Legacy method for backward compatibility
    public String getTime() {
        return notificationTimes != null && !notificationTimes.isEmpty() ?
                notificationTimes.get(0) : "";
    }

    // Legacy method for backward compatibility
    public void setTime(String time) {
        if (this.notificationTimes == null) {
            this.notificationTimes = new ArrayList<>();
        }
        if (!this.notificationTimes.isEmpty()) {
            this.notificationTimes.set(0, time);
        } else {
            this.notificationTimes.add(time);
        }
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getFoodRelation() {
        return foodRelation;
    }

    public void setFoodRelation(String foodRelation) {
        this.foodRelation = foodRelation;
    }

    public boolean shouldAppearOnDate(LocalDate targetDate) {
        // Base case: If this is the medication's original date
        if (this.date != null && this.date.equals(targetDate)) {
            return true;
        }

        // If no original date, can't determine recurrence
        if (this.date == null) {
            return false;
        }

        // Handle different frequencies
        switch (this.frequency) {
            case "Once daily":
            case "Twice daily":
            case "Three times daily":
            case "Four times daily":
            case "Every morning":
            case "Every night":
                // These appear every day
                return !targetDate.isBefore(this.date);

            case "Every other day":
                // Calculate days between dates
                long daysBetween = ChronoUnit.DAYS.between(this.date, targetDate);
                return daysBetween >= 0 && daysBetween % 2 == 0;

            case "Weekly":
                // Same day of week
                return !targetDate.isBefore(this.date) &&
                        targetDate.getDayOfWeek() == this.date.getDayOfWeek();

            case "As needed":
                // Only show on original date
                return this.date.equals(targetDate);

            default:
                // For custom frequencies, only show on original date
                return this.date.equals(targetDate);
        }
    }
}