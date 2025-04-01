package com.example.medassist.ui.medication;

public class Medication {
    private String name;
    private String dosage;
    private String frequency;
    private String time;
    private String sideEffects;
    long id;

    public Medication(long id, String name, String dosage, String frequency, String time, String sideEffects ){
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.time = time;
        this.sideEffects = sideEffects;

    }
}
