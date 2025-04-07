package com.example.medassist.ui.appointment;

public class Appointment {
    private String id;
    private String clinicName;
    private String location;
    private String appointmentStart;
    private String appointmentEnd;
    private String frequency;
    private String repeatAmountAndUnit; // Combined field
    private String description;
    private String date; // Newly added field

    // Updated constructor
    public Appointment(String id, String clinicName, String location, String appointmentStart, String appointmentEnd,
                       String frequency, String repeatAmountAndUnit, String description, String date) {
        this.id = id;
        this.clinicName = clinicName;
        this.location = location;
        this.appointmentStart = appointmentStart;
        this.appointmentEnd = appointmentEnd;
        this.frequency = frequency;
        this.repeatAmountAndUnit = repeatAmountAndUnit;
        this.description = description;
        this.date = date;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getRepeatAmountAndUnit() {
        return repeatAmountAndUnit;
    }

    public void setRepeatAmountAndUnit(String repeatAmountAndUnit) {
        this.repeatAmountAndUnit = repeatAmountAndUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Placeholder logic for whether the appointment appears on a certain date
    public boolean shouldAppearOnDate(String targetDate) {
        // Expand logic as needed
        return targetDate.equals(this.date);
    }
}
