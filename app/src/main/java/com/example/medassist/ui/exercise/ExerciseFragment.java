package com.example.medassist.ui.exercise;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medassist.R;
import com.example.medassist.databinding.FragmentExerciseBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class ExerciseFragment extends Fragment {

    private FragmentExerciseBinding binding;
    private ExerciseViewModel exerciseViewModel;
    // DAILY_TARGET_MINUTES remains the overall maximum per day (12 hours)
    private static final int DAILY_TARGET_MINUTES = 720; // 12 hours

    // List of quick-add exercises (name and default duration)
    private final ArrayList<QuickExercise> quickExercises = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Populate quick exercise list (could be fetched from a resource file or backend)
        quickExercises.add(new QuickExercise("Swimming", 60));
        quickExercises.add(new QuickExercise("Jogging", 30));
        quickExercises.add(new QuickExercise("Badminton", 60));

        // Setup RecyclerView with quick exercises
        QuickExerciseAdapter adapter = new QuickExerciseAdapter(quickExercises);
        binding.recyclerExercises.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.recyclerExercises.setAdapter(adapter);

        // Observe the exercise data in the ViewModel
        exerciseViewModel.getExerciseData().observe(getViewLifecycleOwner(), data -> {
            // Update the BarChartView with the latest exercise data
            binding.barChartView.setExerciseData(data);
            updateActiveTimeDisplay(data);
        });

        // Set click listener for adding new (custom) exercise with confirmation and cap check
        binding.btnAddExercise.setOnClickListener(v -> showAddExerciseDialog());

        // Add regular exercise buttons with confirmation pop-ups
        binding.btnRunning.setOnClickListener(v -> showConfirmationDialog("Running (30 mins)", 30));
        binding.btnSwimming.setOnClickListener(v -> showConfirmationDialog("Swimming (60 mins)", 60));
        binding.btnGym.setOnClickListener(v -> showConfirmationDialog("Gym (60 mins)", 60));

        return root;
    }

    private void showConfirmationDialog(String exerciseName, int minutesToAdd) {
        String today = getToday();
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Addition")
                .setMessage("Are you sure you want to add " + exerciseName + " for " + today + "?")
                .setPositiveButton("Yes", (dialog, which) -> updateExerciseData(today, minutesToAdd))
                .setNegativeButton("No", null)
                .show();
    }

    private void showAddExerciseDialog() {
        // Inflate the custom layout for the dialog (from dialog_add_exercise.xml)
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_exercise, null);

        final EditText etDuration = dialogView.findViewById(R.id.etDuration);
        final Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);

        // Setup spinner for day selection
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, daysOfWeek);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(getContext())
                .setTitle("Add Exercise")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String durationStr = etDuration.getText().toString().trim();
                    String selectedDay = spinnerDay.getSelectedItem().toString();
                    if (!durationStr.isEmpty()) {
                        int exerciseDuration = Integer.parseInt(durationStr);
                        updateExerciseData(selectedDay, exerciseDuration);
                    } else {
                        Toast.makeText(getContext(), "Please enter a valid duration", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // This method now checks if the new addition would exceed 12 hours;
    // if so, a warning dialog is shown instead of adding.
    private void updateExerciseData(String day, int exerciseDuration) {
        Map<String, Integer> currentData = exerciseViewModel.getExerciseData().getValue();
        int current = currentData != null ? currentData.getOrDefault(day, 0) : 0;
        if (current >= DAILY_TARGET_MINUTES) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Warning")
                    .setMessage("Slow down so you don't get an injury.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        exerciseViewModel.addExerciseData(day, exerciseDuration);
    }

    private void updateActiveTimeDisplay(Map<String, Integer> exerciseData) {
        // Update active time display (total minutes)
        int totalMinutes = 0;
        for (int duration : exerciseData.values()) {
            totalMinutes += duration;
        }
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        String formattedTime = String.format(Locale.getDefault(), "%d:%02d", hours, minutes);
        binding.tvActiveDuration.setText(formattedTime);

        // Calculate weekly progress from Monday up to today
        // For weekly progress, only count up to 2 hours (120 minutes) per day.
        String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String today = getToday();
        int todayIndex = 0;
        for (int i = 0; i < weekDays.length; i++) {
            if (weekDays[i].equals(today)) {
                todayIndex = i;
                break;
            }
        }
        int weekMinutes = 0;
        for (int i = 0; i <= todayIndex; i++) {
            // Only count a maximum of 120 minutes per day for weekly percentage
            int dayMinutes = exerciseData.getOrDefault(weekDays[i], 0);
            weekMinutes += Math.min(dayMinutes, 120);
        }
        // The weekly target is now 2 hours per day up to today
        int weekTarget = 120 * (todayIndex + 1);
        int percentage = (int) ((weekMinutes / (float) weekTarget) * 100);
        binding.tvProgressPercentage.setText("Progress: " + percentage + "%");

        // Get today's exercise minutes (actual, not capped)
        int todayMinutes = exerciseData.getOrDefault(today, 0);

        // Set motivational message based on conditions:
        // If weekly progress is 100%: "Good job staying active!"
        // Else if today is above 2 hours (120 minutes): "Good job staying active today!"
        // Otherwise: "Remember to stay active!"
        String message;
        if (percentage == 100) {
            message = "Good job staying active!";
        } else if (todayMinutes >= 120) {
            message = "Good job staying active today!";
        } else {
            message = "Remember to stay active!";
        }
        binding.tvActivityMessage.setText(message);
    }

    // Helper method to get today’s day as a String (e.g., "Monday")
    private String getToday() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return dayFormat.format(calendar.getTime());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // --- QuickExercise class ---
    public static class QuickExercise {
        String name;
        int defaultDuration;

        public QuickExercise(String name, int defaultDuration) {
            this.name = name;
            this.defaultDuration = defaultDuration;
        }
    }

    // --- RecyclerView Adapter for quick-add exercises ---
    private class QuickExerciseAdapter extends RecyclerView.Adapter<QuickExerciseAdapter.QuickExerciseViewHolder> {

        private final ArrayList<QuickExercise> exercises;

        QuickExerciseAdapter(ArrayList<QuickExercise> exercises) {
            this.exercises = exercises;
        }

        @NonNull
        @Override
        public QuickExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new QuickExerciseViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull QuickExerciseViewHolder holder, int position) {
            QuickExercise exercise = exercises.get(position);
            holder.bind(exercise);
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        class QuickExerciseViewHolder extends RecyclerView.ViewHolder {

            private final android.widget.TextView textView;

            QuickExerciseViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }

            void bind(QuickExercise exercise) {
                textView.setText(exercise.name);
                itemView.setOnClickListener(v -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Add Exercise")
                            .setMessage("Add " + exercise.name + " (" + exercise.defaultDuration + " minutes) for today?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                String today = getToday();
                                updateExerciseData(today, exercise.defaultDuration);
                                Toast.makeText(getContext(), exercise.name + " added", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("No", null)
                            .show();
                });
            }
        }
    }
}