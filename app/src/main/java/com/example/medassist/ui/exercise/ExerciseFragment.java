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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ExerciseFragment extends Fragment {

    private FragmentExerciseBinding binding;
    private ExerciseViewModel exerciseViewModel;
    private ScheduleAdapter scheduleAdapter;
    private List<ExerciseViewModel.Workout> todayWorkouts;

    // Define the order for days.
    private static final List<String> DAY_ORDER = Arrays.asList(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up the schedule RecyclerView (bottom card)
        scheduleAdapter = new ScheduleAdapter();
        binding.recyclerExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerExercises.setAdapter(scheduleAdapter);

        // Observe changes in the workouts list
        exerciseViewModel.getScheduledWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            // Sort workouts by day order
            List<ExerciseViewModel.Workout> sortedWorkouts = new ArrayList<>(workouts);
            Collections.sort(sortedWorkouts, new Comparator<ExerciseViewModel.Workout>() {
                @Override
                public int compare(ExerciseViewModel.Workout w1, ExerciseViewModel.Workout w2) {
                    int i1 = DAY_ORDER.indexOf(w1.day);
                    int i2 = DAY_ORDER.indexOf(w2.day);
                    return Integer.compare(i1, i2);
                }
            });
            // Update schedule list and bar chart with sorted list.
            scheduleAdapter.setWorkouts(sortedWorkouts);
            binding.barChartView.setWorkouts(sortedWorkouts);
            updateTodayWorkoutSection(sortedWorkouts);
            updateActiveTimeAndProgress(sortedWorkouts);
        });

        // "Add custom workout" button
        binding.btnAddExercise.setOnClickListener(v -> showAddWorkoutDialog());

        // Checkbox for marking today's workout(s) complete.
        binding.chkMarkAsComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (todayWorkouts != null) {
                // When checkbox is toggled, mark all today’s workouts as complete/incomplete.
                for (ExerciseViewModel.Workout w : todayWorkouts) {
                    exerciseViewModel.setWorkoutCompleted(w, isChecked);
                }
            }
        });

        return root;
    }

    /**
     * Show dialog to add a new workout with name, day, and duration.
     */
    private void showAddWorkoutDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_exercise, null);

        final EditText etWorkoutName = dialogView.findViewById(R.id.etWorkoutName);
        final EditText etDuration = dialogView.findViewById(R.id.etDuration);
        final Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);

        // Populate spinner with days of the week
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, DAY_ORDER);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(getContext())
                .setTitle("Add Workout")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String workoutName = etWorkoutName.getText().toString().trim();
                    String durationStr = etDuration.getText().toString().trim();
                    String selectedDay = spinnerDay.getSelectedItem().toString();

                    if (workoutName.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a valid workout name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (durationStr.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a valid duration", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int duration = Integer.parseInt(durationStr);
                    ExerciseViewModel.Workout workout = new ExerciseViewModel.Workout(workoutName, selectedDay, duration);
                    exerciseViewModel.addWorkout(workout);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Update the middle section ("Today's Activity" and checkbox).
     * Gathers all workouts scheduled for today and displays them as text.
     */
    private void updateTodayWorkoutSection(List<ExerciseViewModel.Workout> workouts) {
        String today = getToday();
        todayWorkouts = new ArrayList<>();
        for (ExerciseViewModel.Workout w : workouts) {
            if (w.day.equalsIgnoreCase(today)) {
                todayWorkouts.add(w);
            }
        }
        if (todayWorkouts.isEmpty()) {
            binding.tvTodayActivity.setText("No workout scheduled today");
            binding.chkMarkAsComplete.setVisibility(View.GONE);
        } else {
            // Build a string listing each workout: "Football - 120 mins"
            StringBuilder sb = new StringBuilder();
            for (ExerciseViewModel.Workout w : todayWorkouts) {
                sb.append(w.name).append(" - ").append(w.duration).append(" mins\n");
            }
            binding.tvTodayActivity.setText(sb.toString().trim());
            binding.chkMarkAsComplete.setVisibility(View.VISIBLE);
            // Checkbox is checked only if all today workouts are complete.
            boolean allComplete = true;
            for (ExerciseViewModel.Workout w : todayWorkouts) {
                if (!w.isCompleted) {
                    allComplete = false;
                    break;
                }
            }
            binding.chkMarkAsComplete.setChecked(allComplete);
        }
    }

    /**
     * Update the top card: active time and progress (percentage of days fully completed).
     */
    private void updateActiveTimeAndProgress(List<ExerciseViewModel.Workout> workouts) {
        int totalMinutes = 0;
        for (ExerciseViewModel.Workout w : workouts) {
            totalMinutes += w.duration;
        }
        int hours = totalMinutes / 60;
        int mins = totalMinutes % 60;
        String formattedTime = String.format(Locale.getDefault(), "%d:%02d", hours, mins);
        binding.tvActiveDuration.setText(formattedTime);

        String[] allDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int daysWithWorkouts = 0;
        int daysFullyCompleted = 0;

        for (String day : allDays) {
            List<ExerciseViewModel.Workout> dayWorkouts = new ArrayList<>();
            for (ExerciseViewModel.Workout w : workouts) {
                if (w.day.equalsIgnoreCase(day)) {
                    dayWorkouts.add(w);
                }
            }
            if (!dayWorkouts.isEmpty()) {
                daysWithWorkouts++;
                boolean allCompleted = true;
                for (ExerciseViewModel.Workout w : dayWorkouts) {
                    if (!w.isCompleted) {
                        allCompleted = false;
                        break;
                    }
                }
                if (allCompleted) {
                    daysFullyCompleted++;
                }
            }
        }

        int progressPercent = 0;
        if (daysWithWorkouts > 0) {
            progressPercent = (int) ((daysFullyCompleted / (float) daysWithWorkouts) * 100);
        }
        binding.tvProgressPercentage.setText("Progress: " + progressPercent + "%");

        String message;
        if (progressPercent == 100 && daysWithWorkouts > 0) {
            message = "Good job completing all your workouts!";
        } else if (daysFullyCompleted > 0) {
            message = "Keep going, you're doing great!";
        } else {
            message = "Remember to stay active!";
        }
        binding.tvActivityMessage.setText(message);
    }

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

    // RecyclerView Adapter for the weekly schedule (bottom card)
    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

        private List<ExerciseViewModel.Workout> workouts;

        public void setWorkouts(List<ExerciseViewModel.Workout> workouts) {
            // Already sorted before passing in observer
            this.workouts = workouts;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ScheduleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
            ExerciseViewModel.Workout workout = workouts.get(position);
            holder.bind(workout);
        }

        @Override
        public int getItemCount() {
            return workouts == null ? 0 : workouts.size();
        }

        class ScheduleViewHolder extends RecyclerView.ViewHolder {
            private final android.widget.TextView textView;

            ScheduleViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }

            void bind(ExerciseViewModel.Workout workout) {
                String status = workout.isCompleted ? " (Completed)" : "";
                String display = workout.day + " - " + workout.duration + " mins " + workout.name + status;
                textView.setText(display);

                // Long press to delete workout
                itemView.setOnLongClickListener(v -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Workout")
                            .setMessage("Delete \"" + workout.name + "\" from your schedule?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                exerciseViewModel.removeWorkout(workout);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                });
            }
        }
    }
}