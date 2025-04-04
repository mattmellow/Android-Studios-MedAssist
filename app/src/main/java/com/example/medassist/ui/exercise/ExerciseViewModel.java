package com.example.medassist.ui.exercise;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class ExerciseViewModel extends ViewModel {

    private final MutableLiveData<Map<String, Integer>> exerciseData;

    public ExerciseViewModel() {
        exerciseData = new MutableLiveData<>();
        exerciseData.setValue(new HashMap<>());
    }

    public LiveData<Map<String, Integer>> getExerciseData() {
        return exerciseData;
    }

    public void addExerciseData(String day, int duration) {
        Map<String, Integer> currentData = exerciseData.getValue();
        if (currentData != null) {
            int currentDuration = currentData.getOrDefault(day, 0);
            // Enforce a maximum of 12 hours (720 minutes) per day
            currentData.put(day, Math.min(currentDuration + duration, 720));
            exerciseData.setValue(currentData);
        }
    }
}