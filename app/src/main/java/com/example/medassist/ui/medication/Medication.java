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
    private String id;
    private String duration;
    private String durationUnit;

    //multiple times added
    public Medication(String id, String name, String dosage, String frequency, List<String> notificationTimes, String sideEffects, String duration, String durationUnit) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.notificationTimes = notificationTimes != null ? notificationTimes : new ArrayList<>();
        this.sideEffects = sideEffects;
        this.duration = duration;
        this.durationUnit = durationUnit;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
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

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

}
