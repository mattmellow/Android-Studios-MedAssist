package com.example.medassist.ui.reminders;
import com.example.medassist.R;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * Base class to handle frequency and time picker logic for all reminder types.
 */
public abstract class BaseReminderFrequencyHandler {
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

    protected final Context context;
    protected final Spinner frequencySpinner;
    protected final LinearLayout timePickerContainer;
    protected final List<String> selectedTimes = new ArrayList<>();
    protected final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public BaseReminderFrequencyHandler(Context context, Spinner frequencySpinner, LinearLayout timePickerContainer) {
        this.context = context;
        this.frequencySpinner = frequencySpinner;
        this.timePickerContainer = timePickerContainer;

        initFrequencySpinner();
    }

    // Abstract method to be implemented by subclasses to handle custom logic
    protected abstract void handleCustomFrequency();

    private void initFrequencySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, FREQUENCY_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);

        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timePickerContainer.removeAllViews();
                selectedTimes.clear();

                switch (position) {
                    case 0: // Once daily
                        addTimePicker("8:00 AM");
                        break;
                    case 1: // Twice daily
                        addTimePickersWithSpacing("8:00 AM", 2);
                        break;
                    case 2: // Three times daily
                        addTimePickersWithSpacing("8:00 AM", 3);
                        break;
                    case 3: // Four times daily
                        addTimePickersWithSpacing("8:00 AM", 4);
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
                        handleCustomFrequency();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    protected void addTimePicker(String defaultTime) {
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

        timeButton.setOnClickListener(v -> showTimePicker(timeText, defaultTime, timePickerView));
        timePickerContainer.addView(timePickerView);
    }

    private void showTimePicker(TextView timeText, String defaultTime, View timePickerView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (!defaultTime.isEmpty()) {
            try {
                LocalTime time = LocalTime.parse(defaultTime, timeFormatter);
                hour = time.getHour();
                minute = time.getMinute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TimePickerDialog timePicker = new TimePickerDialog(
                context,
                (view, hourOfDay, selectedMinute) -> {
                    LocalTime time = LocalTime.of(hourOfDay, selectedMinute);
                    String formattedTime = time.format(timeFormatter);
                    timeText.setText(formattedTime);

                    int index = timePickerContainer.indexOfChild(timePickerView);
                    if (index < selectedTimes.size()) {
                        selectedTimes.set(index, formattedTime);
                    } else {
                        selectedTimes.add(formattedTime);
                    }
                },
                hour,
                minute,
                false
        );
        timePicker.show();
    }

    protected void addTimePickersWithSpacing(String baseTime, int count) {
        try {
            LocalTime time = LocalTime.parse(baseTime, DateTimeFormatter.ofPattern("h:mm a"));
            int hourSpacing = 24 / count;

            addTimePicker(time.format(timeFormatter));

            for (int i = 1; i < count; i++) {
                time = time.plusHours(hourSpacing);
                addTimePicker(time.format(timeFormatter));
            }
        } catch (Exception e) {
            e.printStackTrace();
            for (int i = 0; i < count; i++) {
                addTimePicker("8:00 AM");
            }
        }
    }

    // Getters for selected values
    public String getSelectedFrequency() {
        return (String) frequencySpinner.getSelectedItem();
    }

    public List<String> getSelectedTimes() {
        return new ArrayList<>(selectedTimes);
    }
}
