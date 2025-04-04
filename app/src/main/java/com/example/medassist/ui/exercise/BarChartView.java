package com.example.medassist.ui.exercise;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BarChartView extends View {

    // Use 120 minutes (2 hours) as the maximum for the bar scaling.
    private static final int MAX_BAR_MINUTES = 120;
    private Map<String, Integer> exerciseData = new HashMap<>(); // Data for each day of the week

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // Set exercise data dynamically
    public void setExerciseData(Map<String, Integer> data) {
        this.exerciseData = data;
        invalidate(); // Redraw the view when data changes
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Paint for the bars (white for high contrast with blue background)
        Paint barPaint = new Paint();
        barPaint.setColor(Color.WHITE);

        // Paint for the weekday labels (white text for visibility)
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        int leftMargin = 0;         // Reserved margin on the left side
        int totalDays = 7;            // 7 days in a week
        float availableWidth = getWidth() - leftMargin;
        float allocatedWidth = availableWidth / totalDays;
        float barWidth = allocatedWidth * 0.6f; // Each bar is 60% of the allocated width
        int labelMargin = 50;         // Space reserved for weekday labels
        int labelPadding = 10;        // Padding between bar bottom and label
        int maxBarHeight = getHeight() - labelMargin - 20; // Maximum height for bars

        // Fixed order for weekdays and their labels
        String[] fullDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] dayLabels = {"MO", "TU", "WE", "TH", "FR", "SA", "SU"};

        // Draw bars and weekday labels in fixed positions
        for (int i = 0; i < totalDays; i++) {
            String fullDay = fullDays[i];
            String label = dayLabels[i];
            int exerciseDuration = 0;

            if (exerciseData.containsKey(fullDay)) {
                // Clamp the value to 120 minutes for bar scaling.
                exerciseDuration = Math.min(exerciseData.get(fullDay), MAX_BAR_MINUTES);
            }

            // Scale the exercise duration: 120 minutes equals maxBarHeight.
            float barHeight = ((float) exerciseDuration / MAX_BAR_MINUTES) * maxBarHeight;

            // Calculate bar positions.
            float left = leftMargin + i * allocatedWidth + (allocatedWidth - barWidth) / 2;
            float top = getHeight() - labelMargin - barHeight;
            float right = left + barWidth;
            float bottom = getHeight() - labelMargin;

            // Draw the bar for the day.
            canvas.drawRect(left, top, right, bottom, barPaint);

            // Calculate the center for the text label within its allocated column.
            float centerX = leftMargin + i * allocatedWidth + allocatedWidth / 2;
            // Adjust Y coordinate to position the text with a small gap (padding) from bars.
            float textY = getHeight() - labelMargin / 2 + labelPadding;

            // Draw the abbreviated day label (e.g., "MO", "TU", etc.)
            canvas.drawText(label, centerX, textY, textPaint);
        }
    }
}