package com.example.medassist.ui.medication;

import java.time.LocalDate;

public class Medication {
    private String name;
    private String dosage;
    private String frequency;
    private String time;
    private String sideEffects;
    private LocalDate date;
    long id;

    public Medication(long id, String name, String dosage, String frequency, String time, String sideEffects ){
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.time = time;
        this.sideEffects = sideEffects;
        this.date = date;
    }

    public String getName(){
        return name;
    }

    public void setName(){
        this.name=name;
    }

    public String getDosage(){
        return dosage;
    }

    public void setDosage(){
        this.dosage = dosage;
    }

    public String getFrequency(){
        return frequency;
    }

    public void setFrequency(String frequency){
        this.frequency = frequency;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getSideEffects(){
        return sideEffects;
    }

    public void setSideEffects(String sideEffects){
        this.sideEffects = sideEffects;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate(){
        return date;
    }

    public void setDate(LocalDate date){
        this.date=date;
    }
}
