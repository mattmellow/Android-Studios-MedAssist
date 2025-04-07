package com.example.medassist.ui.exercise;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarChartView extends View {

    private List<ExerciseViewModel.Workout> workouts;

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instead of separate exerciseData/completionData, we now pass the entire list of workouts.
     */
    public void setWorkouts(List<ExerciseViewModel.Workout> workouts) {
        this.workouts = workouts;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // We'll treat each day as a single bar:
        // the bar "height" can be the sum of durations (capped at 120 if you like).
        // color depends on whether all workouts for that day are completed, or not.

        Paint barPaint = new Paint();
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        int currentDayIndex = getCurrentDayIndex();

        // We'll define the 7 days:
        String[] fullDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] dayLabels = {"MO", "TU", "WE", "TH", "FR", "SA", "SU"};

        // Calculate total durations per day, also track if all completed
        Map<String, Integer> dayDurations = new HashMap<>();
        Map<String, Boolean> dayAllCompleted = new HashMap<>();

        for (String day : fullDays) {
            dayDurations.put(day, 0);
            dayAllCompleted.put(day, true); // if no workouts, we can keep it as "true" or handle separately
        }

        if (workouts != null) {
            for (ExerciseViewModel.Workout w : workouts) {
                int oldValue = dayDurations.get(w.day) != null ? dayDurations.get(w.day) : 0;
                dayDurations.put(w.day, oldValue + w.duration);

                // If any workout is incomplete, dayAllCompleted becomes false
                if (!w.isCompleted) {
                    dayAllCompleted.put(w.day, false);
                }
            }
        }

        // Now we draw bars:
        int totalDays = 7;
        int labelMargin = 50;   // space for day labels
        int labelPadding = 10;
        int maxBarHeight = getHeight() - labelMargin - 20;

        int leftMargin = 0;
        float availableWidth = getWidth() - leftMargin;
        float allocatedWidth = availableWidth / totalDays;
        float barWidth = allocatedWidth * 0.6f;

        // We'll cap each day's total minutes at 120 for bar height
        int MAX_BAR_MINUTES = 120;

        for (int i = 0; i < totalDays; i++) {
            String dayName = fullDays[i];
            String label = dayLabels[i];

            int totalMinutes = dayDurations.get(dayName);
            int clampedMinutes = Math.min(totalMinutes, MAX_BAR_MINUTES);
            float barHeight = (clampedMinutes / (float) MAX_BAR_MINUTES) * maxBarHeight;

            // Decide color
            boolean hasWorkouts = (totalMinutes > 0);
            boolean allCompleted = dayAllCompleted.get(dayName);

            if (!hasWorkouts) {
                // No workouts -> draw a 0 height bar in a neutral color (gray or skip).
                barPaint.setColor(Color.LTGRAY);
            } else {
                if (allCompleted) {
                    // All workouts completed -> green
                    barPaint.setColor(Color.GREEN);
                } else {
                    // Some or all workouts incomplete -> red or yellow depending on day index
                    if (i < currentDayIndex) {
                        barPaint.setColor(Color.RED);
                    } else {
                        barPaint.setColor(Color.YELLOW);
                    }
                }
            }

            float left = leftMargin + i * allocatedWidth + (allocatedWidth - barWidth) / 2;
            float top = getHeight() - labelMargin - barHeight;
            float right = left + barWidth;
            float bottom = getHeight() - labelMargin;

            canvas.drawRect(left, top, right, bottom, barPaint);

            float centerX = leftMargin + i * allocatedWidth + allocatedWidth / 2;
            float textY = getHeight() - labelMargin / 2 + labelPadding;
            canvas.drawText(label, centerX, textY, textPaint);
        }
    }

    /**
     * Return index 0..6 where Monday=0, Tuesday=1, ... Sunday=6
     */
    private int getCurrentDayIndex() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // Sunday=1, Monday=2, ...
        return (dayOfWeek + 5) % 7; // shift so Monday=0
    }
}