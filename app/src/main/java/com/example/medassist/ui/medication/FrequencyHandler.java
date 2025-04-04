package com.example.medassist.ui.medication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.medassist.R;
import com.example.medassist.ui.reminders.BaseReminderFrequencyHandler;

/**
 * Handles medication-specific frequency and time logic by extending BaseReminderFrequencyHandler.
 */
public class FrequencyHandler extends BaseReminderFrequencyHandler {

    public FrequencyHandler(Context context, Spinner frequencySpinner, LinearLayout timePickerContainer) {
        super(context, frequencySpinner, timePickerContainer);
    }

    @Override
    protected void handleCustomFrequency() {
        // Custom behavior for medication-specific logic
        addTimePicker("");
        addAddTimeButton();
    }

    private void addAddTimeButton() {
        View addButtonView = LayoutInflater.from(context).inflate(
                R.layout.add_time_button, null);

        addButtonView.findViewById(R.id.addTimeButton).setOnClickListener(v -> {
            addTimePicker("");
            timePickerContainer.removeView(addButtonView); // Remove current add button
            timePickerContainer.addView(addButtonView);    // Add it back at the end
        });

        timePickerContainer.addView(addButtonView);
    }

    // Optionally, you can add other custom methods for medication-specific behavior.
}
