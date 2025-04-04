package com.example.medassist.ui.medication;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.medassist.R;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Helper class to handle frequency selection and time pickers
 */
public class FrequencyHandler {
    private static final String[] FREQUENCY_OPTIONS = {
            "Once daily",
            "Twice daily",
            "Three times daily",
            "Four times daily",
            "Every morning",
            "Every night",
            "Every other day",
            "Weekly",
            "As needed",
            "Custom..."
    };

    private final Context context;
    private final Spinner frequencySpinner;
    private final LinearLayout notificationTimesContainer;
    private final LinearLayout timePickerContainer;
    private final List<String> selectedTimes = new ArrayList<>();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public FrequencyHandler(Context context, Spinner frequencySpinner,
                            LinearLayout notificationTimesContainer,
                            LinearLayout timePickerContainer) {
        this.context = context;
        this.frequencySpinner = frequencySpinner;
        this.notificationTimesContainer = notificationTimesContainer;
        this.timePickerContainer = timePickerContainer;

        initFrequencySpinner();
    }

    private void initFrequencySpinner() {
        // Setup adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, FREQUENCY_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);

        // Handle frequency selection
        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Clear existing time pickers
                timePickerContainer.removeAllViews();
                selectedTimes.clear();

                // Generate time pickers based on frequency
                switch (position) {
                    case 0: // Once daily
                        addTimePicker("8:00 AM");
                        break;
                    case 1: // Twice daily
                        addTimePickersWithSpacing("8:00 AM", 2); // Will add 8:00 AM and 8:00 PM
                        break;
                    case 2: // Three times daily
                        addTimePickersWithSpacing("8:00 AM", 3); // Will add 8:00 AM, 4:00 PM, 12:00 AM
                        break;
                    case 3: // Four times daily
                        addTimePickersWithSpacing("8:00 AM", 4); // Will add 8:00 AM, 2:00 PM, 8:00 PM, 2:00 AM
                        break;
                    case 4: // Every morning
                        addTimePicker("8:00 AM");
                        break;
                    case 5: // Every night
                        addTimePicker("8:00 PM");
                        break;
                    case 6: // Every other day
                    case 7: // Weekly
                    case 8: // As needed
                        addTimePicker("8:00 AM");
                        break;
                    case 9: // Custom
                        addTimePicker("");
                        addAddTimeButton();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void addTimePicker(String defaultTime) {
        View timePickerView = LayoutInflater.from(context).inflate(
                R.layout.time_picker_item, null);

        TextView timeText = timePickerView.findViewById(R.id.timePickerText);
        ImageButton timeButton = timePickerView.findViewById(R.id.selectTimeButton);

        if (!defaultTime.isEmpty()) {
            timeText.setText(defaultTime);
            selectedTimes.add(defaultTime);
        } else {
            timeText.setText("Select time");
        }

        timeButton.setOnClickListener(v -> {
            // Get initial time values
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // If we have a default time, parse it
            if (!defaultTime.isEmpty()) {
                try {
                    LocalTime time = LocalTime.parse(defaultTime, timeFormatter);
                    hour = time.getHour();
                    minute = time.getMinute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Create and show standard TimePickerDialog
            TimePickerDialog timePicker = new TimePickerDialog(
                    context,
                    (view, hourOfDay, selectedMinute) -> {
                        LocalTime time = LocalTime.of(hourOfDay, selectedMinute);
                        String formattedTime = time.format(timeFormatter);

                        timeText.setText(formattedTime);

                        // Update or add the time
                        int index = timePickerContainer.indexOfChild(timePickerView);
                        if (index < selectedTimes.size()) {
                            selectedTimes.set(index, formattedTime);
                        } else {
                            selectedTimes.add(formattedTime);
                        }
                    },
                    hour,
                    minute,
                    false // 12-hour format
            );

            timePicker.show();
        });

        timePickerContainer.addView(timePickerView);
    }

    private void addTimePickersWithSpacing(String baseTime, int count) {
        try {
            // Parse base time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
            LocalTime time = LocalTime.parse(baseTime, formatter);

            // Calculate spacing in hours (24 hours / count)
            int hourSpacing = 24 / count;

            // Add initial time
            addTimePicker(time.format(timeFormatter));

            // Add subsequent times with appropriate spacing
            for (int i = 1; i < count; i++) {
                time = time.plusHours(hourSpacing);
                addTimePicker(time.format(timeFormatter));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback - add default times
            for (int i = 0; i < count; i++) {
                addTimePicker("8:00 AM");
            }
        }
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

    /**
     * Get the currently selected frequency text
     */
    public String getSelectedFrequency() {
        return (String) frequencySpinner.getSelectedItem();
    }

    /**
     * Get list of selected notification times
     */
    public List<String> getSelectedTimes() {
        return new ArrayList<>(selectedTimes);
    }

    /**
     * Set the frequency selection based on existing medication data
     */
    public void setFrequencyAndTimes(String frequency, List<String> times) {
        // Find frequency in array and set spinner
        for (int i = 0; i < FREQUENCY_OPTIONS.length; i++) {
            if (FREQUENCY_OPTIONS[i].equals(frequency)) {
                frequencySpinner.setSelection(i);
                break;
            }
        }

        // If no match or custom frequency, set to custom and add all times
        if (!frequency.equals(getSelectedFrequency())) {
            frequencySpinner.setSelection(FREQUENCY_OPTIONS.length - 1); // Custom option

            // Clear generated time pickers
            timePickerContainer.removeAllViews();
            selectedTimes.clear();

            // Add time pickers for each time
            for (String time : times) {
                addTimePicker(time);
            }
            addAddTimeButton();
        }
    }
}