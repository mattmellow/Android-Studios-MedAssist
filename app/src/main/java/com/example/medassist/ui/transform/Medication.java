package com.example.medassist.ui.transform;

public class Medication {
    private final String name;
    private final String time;
    private final String warning;

    public Medication(String name, String time, String warning) {
        this.name = name;
        this.time = time;
        this.warning = warning;
    }

    public String getName() { return name; }
    public String getTime() { return time; }
    public String getWarning() { return warning; }

    @Override
    public String toString() {
        return name + " at " + time + (warning.isEmpty() ? "" : " (" + warning + ")");
    }
}
