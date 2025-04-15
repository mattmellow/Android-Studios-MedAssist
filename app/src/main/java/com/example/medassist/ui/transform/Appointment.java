package com.example.medassist.ui.transform;

import java.util.Objects;

public class Appointment {
    private final String day;
    private final String date;
    private final String title;
    private final String location;
    private final String time;

    public Appointment(String day, String date, String title, String location, String time) {
        this.day = day;
        this.date = date;
        this.title = title;
        this.location = location;
        this.time = time;}

    // Getters
    public String getDay() { return day; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getTime() { return time; }

    @Override
    public int hashCode() {
        return Objects.hash(day, date, time);
    }
}
