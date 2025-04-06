package com.example.medassist.ui.reminders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.medassist.R;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public abstract class ReminderFormDialog extends DialogFragment {
    // Common fields for all reminder types
    protected EditText nameEditText;
    protected Spinner frequencySpinner;
    protected LinearLayout timePickerContainer;
    protected Button addAnotherTimeButton;
    protected List<String> selectedTimes;
    protected OnReminderAddedListener listener;

    // Layout resource ID
    protected int layoutResourceId;

    public interface OnReminderAddedListener {
        void onReminderAdded(String name, String dosage, String frequency, List<String> times, String sideEffects, String foodRelation, String duration, String durationUnit);
    }

    public void setOnReminderAddedListener(OnReminderAddedListener listener) {
        this.listener = listener;
    }

    public void setLayoutResourceId(int layoutResourceId) {
        this.layoutResourceId = layoutResourceId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (layoutResourceId == 0) {
            throw new IllegalStateException("Layout resource ID must be set before creating dialog.");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(layoutResourceId, null);

        // Initialize selected times list
        selectedTimes = new ArrayList<>();

        // Initialize form fields (can be overridden by subclasses)
        initializeFormFields(view);

        setupInitialTimePicker();

        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> submitForm());

        builder.setView(view);
        return builder.create();
    }

    protected void initializeFormFields(View view) {
        nameEditText = view.findViewById(R.id.reminderNameEditText);
        frequencySpinner = view.findViewById(R.id.frequencySpinner);
        timePickerContainer = view.findViewById(R.id.timePickerContainer);

        // Set up frequency spinner (common for all types)
        ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.frequency_options, android.R.layout.simple_spinner_item);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(frequencyAdapter);
    }

    private void setupInitialTimePicker() {
        // Set up the initial time picker view
        View timePickerView = timePickerContainer.getChildAt(0);
        if (timePickerView != null) {
            TextView timeText = timePickerView.findViewById(R.id.timePickerText);
            ImageButton selectTimeButton = timePickerView.findViewById(R.id.selectTimeButton);

            selectTimeButton.setOnClickListener(v -> showMaterialTimePicker(timeText));
        }

        // Add button for adding another time
        addAnotherTimeButton = new Button(getContext());
        addAnotherTimeButton.setText("+ Add Another Time");
        addAnotherTimeButton.setOnClickListener(v -> addNewTimePicker());
        timePickerContainer.addView(addAnotherTimeButton);
    }

    private void addNewTimePicker() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View newTimePickerView = inflater.inflate(R.layout.time_picker_item, null);

        TextView timeText = newTimePickerView.findViewById(R.id.timePickerText);
        ImageButton selectTimeButton = newTimePickerView.findViewById(R.id.selectTimeButton);
        ImageButton removeTimeButton = newTimePickerView.findViewById(R.id.removeTimeButton);

        // Set up click listeners
        selectTimeButton.setOnClickListener(v -> showMaterialTimePicker(timeText));
        removeTimeButton.setOnClickListener(v -> {
            timePickerContainer.removeView(newTimePickerView);
            String time = timeText.getText().toString();
            if (!time.equals("Enter time")) {
                selectedTimes.remove(time);
            }
        });

        removeTimeButton.setVisibility(View.VISIBLE);

        // Add the new time picker to the container before the "add" button
        timePickerContainer.addView(newTimePickerView, timePickerContainer.getChildCount() - 1);
    }

    protected void showMaterialTimePicker(TextView targetTextView) {
        Log.d("TimePicker", "Target view: " + targetTextView);
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Select Time")
                .build();

        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = materialTimePicker.getHour();
            int selectedMinute = materialTimePicker.getMinute();
            Log.d("TimePicker", "Selected hour: " + selectedHour + ", minute: " + selectedMinute);

            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedTime.set(Calendar.MINUTE, selectedMinute);

            // Format the time
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTime = sdf.format(selectedTime.getTime());
            Log.d("TimePicker", "Formatted time: " + formattedTime);


            // Update the text view
            targetTextView.setText(formattedTime);
            Log.d("TimePicker", "in the between: " + targetTextView.getText());
            // Store the selected time
            String currentText = "" + targetTextView.getText();
            Log.d("TimePicker", "in the asdfaewfawefawef: " + currentText);
            Log.d("TimePicker", "im herasdasdasde: "+!currentText.equals("Enter time")+selectedTimes.contains(currentText));
            if (!currentText.equals("Enter time") && selectedTimes.contains(currentText)) {
                Log.d("TimePicker", "inside condition with: ");
                selectedTimes.remove(currentText);
            }
            selectedTimes.add(formattedTime);
        });
        Log.d("TimePicker", "im here: ");
        materialTimePicker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");
        Log.d("TimePicker", "inside coasdasdsadndition with: ");
    }

    protected abstract void submitForm();
}
