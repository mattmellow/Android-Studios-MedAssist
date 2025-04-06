package com.example.medassist.ui.exercise;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ExerciseViewModel extends ViewModel {

    // List of all workouts scheduled this week
    private final MutableLiveData<List<Workout>> scheduledWorkouts;

    public ExerciseViewModel() {
        scheduledWorkouts = new MutableLiveData<>();
        scheduledWorkouts.setValue(new ArrayList<>());
    }

    public LiveData<List<Workout>> getScheduledWorkouts() {
        return scheduledWorkouts;
    }

    /*
     Add a new workout to the schedule.
     */
    public void addWorkout(Workout workout) {
        List<Workout> currentList = scheduledWorkouts.getValue();
        if (currentList != null) {
            currentList.add(workout);
            scheduledWorkouts.setValue(currentList);
        }
    }

    /*
      Remove a workout from the schedule.
     */
    public void removeWorkout(Workout workout) {
        List<Workout> currentList = scheduledWorkouts.getValue();
        if (currentList != null) {
            currentList.remove(workout);
            scheduledWorkouts.setValue(currentList);
        }
    }

    /*
     Mark a single workout as completed or not.
     */
    public void setWorkoutCompleted(Workout workout, boolean completed) {
        List<Workout> currentList = scheduledWorkouts.getValue();
        if (currentList != null) {
            int index = currentList.indexOf(workout);
            if (index >= 0) {
                currentList.get(index).isCompleted = completed;
                scheduledWorkouts.setValue(currentList);
            }
        }
    }

    /*
     Find all workouts scheduled for a particular day (e.g., "Monday").
     */
    public List<Workout> getWorkoutsForDay(String day) {
        List<Workout> dayWorkouts = new ArrayList<>();
        List<Workout> currentList = scheduledWorkouts.getValue();
        if (currentList != null) {
            for (Workout w : currentList) {
                if (w.day.equals(day)) {
                    dayWorkouts.add(w);
                }
            }
        }
        return dayWorkouts;
    }

    /*
      Model for a scheduled workout.
     */
    public static class Workout {
        public String name;
        public String day;
        public int duration;    // minutes
        public boolean isCompleted;

        public Workout(String name, String day, int duration) {
            this.name = name;
            this.day = day;
            this.duration = duration;
            this.isCompleted = false;
        }
    }
}