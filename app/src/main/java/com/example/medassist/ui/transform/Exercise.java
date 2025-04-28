package com.example.medassist.ui.transform;

public class Exercise {
    private final String name1;
    private final String name2;
    private final boolean isCompleted1;
    private final boolean isCompleted2;

    public Exercise(String name1, String name2) {
        this(name1, name2, false, false);
    }

    public Exercise(String name1, String name2, boolean isCompleted1, boolean isCompleted2) {
        this.name1 = name1;
        this.name2 = name2;
        this.isCompleted1 = isCompleted1;
        this.isCompleted2 = isCompleted2;
    }

    // Getters
    public String getName1() { return name1; }
    public String getName2() { return name2; }
    public boolean isCompleted1() { return isCompleted1; }
    public boolean isCompleted2() { return isCompleted2; }

    public int getCompletionPercentage() {
        int count = 0;
        if (isCompleted1) count++;
        if (isCompleted2) count++;
        return (int) ((count / 2.0) * 100);
    }
}
