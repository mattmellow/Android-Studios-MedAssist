package com.example.medassist.ui.appointment;

import java.util.List;

public class Appointment {
    private String clinicName;
    private String location;
    private String appointmentStart;  // Start time as a string, e.g., "05:25 PM"
    private String appointmentEnd;    // End time as a string, e.g., "06:25 PM"
    private String frequency;         // Frequency, e.g., "Once", "Weekly", etc.
    private String repeatAmount;      // Repeat amount (if applicable)
    private String repeatUnit;        // Repeat unit (e.g., "days", "weeks")
    private String description;       // Optional description

    private long id;                  // Unique appointment ID

    // Constructor
    public Appointment(long id, String clinicName, String location, String appointmentStart, String appointmentEnd,
                       String frequency, String repeatAmount, String repeatUnit, String description) {
        this.id = id;
        this.clinicName = clinicName;
        this.location = location;
        this.appointmentStart = appointmentStart;
        this.appointmentEnd = appointmentEnd;
        this.frequency = frequency;
        this.repeatAmount = repeatAmount;
        this.repeatUnit = repeatUnit;
        this.description = description;
    }

    // Getter and setter methods for the appointment fields
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAppointmentStart() {
        return appointmentStart;
    }

    public void setAppointmentStart(String appointmentStart) {
        this.appointmentStart = appointmentStart;
    }

    public String getAppointmentEnd() {
        return appointmentEnd;
    }

    public void setAppointmentEnd(String appointmentEnd) {
        this.appointmentEnd = appointmentEnd;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getRepeatAmount() {
        return repeatAmount;
    }

    public void setRepeatAmount(String repeatAmount) {
        this.repeatAmount = repeatAmount;
    }

    public String getRepeatUnit() {
        return repeatUnit;
    }

    public void setRepeatUnit(String repeatUnit) {
        this.repeatUnit = repeatUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Method to check if the appointment should appear on a specific date
    public boolean shouldAppearOnDate(String targetDate) {
        // If the frequency is weekly, check if it should repeat on the same day of the week
        if ("Weekly".equals(this.frequency)) {
            // For simplicity, you can compare the targetDate with the initial appointment start date here
            // This logic can be expanded depending on your needs
            return true;  // Assuming this will be expanded with actual logic for weekly checks
        }

        // For other frequencies (e.g., Once, Twice), we can check the specific repeat conditions here
        return false;
    }
}
