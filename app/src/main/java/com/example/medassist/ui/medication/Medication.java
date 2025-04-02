package com.example.medassist.ui.medication;

import java.time.LocalDate;

public class Medication {
    private long medicationId; //UUID for firestore
    private String medicationName; //Medication Name

    //*******************************************************************************************
    // For the following, we broke down intake instructions into 3 parts:                       *
    // (a) Dosage: The amount needed to be taken per intake (e.g. how many pills? how much ml?) *
    // (b) Frequency: The number of intakes per day (e.g. 2 times per day)                      *
    // (c) Side effects: The side effects that the user should take note of, if any             *
    //*******************************************************************************************
    private String dosage;
    private String frequency;
    private String sideEffects;


    private String time; // Format is in "HH:mm" (e.g. "08:00")
    private LocalDate date;

    //***************
    // Constructors *
    //***************
    public Medication(long medicationId, String medicationName, String dosage,
                      String frequency, String sideEffects, String time, LocalDate date ){
        this.medicationId = medicationId;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.sideEffects = sideEffects;
        this.time = time;
        this.date = date;
    }

    //*********************************
    // Empty Constructor for Firebase *
    //*********************************
    public Medication(){}

    //**********************
    // Getters and Setters *
    //**********************
    public long getMedicationId() { return medicationId; }
    public void setMedicationId(long medicationId) { this.medicationId = medicationId; }

    public String getMedicationName(){ return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }

    public String getDosage(){
        return dosage;
    }
    public void setDosage(String dosage){
        this.dosage = dosage;
    }

    public String getFrequency(){
        return frequency;
    }
    public void setFrequency(String frequency){
        this.frequency = frequency;
    }

    public String getSideEffects(){
        return sideEffects;
    }
    public void setSideEffects(String sideEffects){
        this.sideEffects = sideEffects;
    }

    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time = time;
    }

    public LocalDate getDate(){
        return date;
    }
    public void setDate(LocalDate date){
        this.date=date;
    }
}
