package com.example.medassist.ui.reminders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
        // Add frequency as a parameter here
        void onReminderAdded(String name, String dosage, String frequency, List<String> times, String sideEffects, String foodRelation);
    }


    public void setOnReminderAddedListener(OnReminderAddedListener listener) {
        this.listener = listener;
    }

    // Set the layout resource ID dynamically based on the specific reminder type
    public void setLayoutResourceId(int layoutResourceId) {
        this.layoutResourceId = layoutResourceId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Validate that the layout resource ID is set before inflating
        if (layoutResourceId == 0) {
            throw new IllegalStateException("Layout resource ID must be set before creating dialog.");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(layoutResourceId, null);

        // Initialize common fields
        nameEditText = view.findViewById(R.id.reminderNameEditText);
        frequencySpinner = view.findViewById(R.id.frequencySpinner);
        timePickerContainer = view.findViewById(R.id.timePickerContainer);
        selectedTimes = new ArrayList<>();

        // Set up frequency spinner (this is common for all types)
        ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.frequency_options, android.R.layout.simple_spinner_item);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(frequencyAdapter);

        // Set up initial time picker
        setupInitialTimePicker();

        // Set up done button
        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> submitForm());

        builder.setView(view);
        return builder.create();
    }

    private void setupInitialTimePicker() {
        // Initialize the first time picker view
        View timePickerView = timePickerContainer.getChildAt(0);
        if (timePickerView != null) {
            MaterialTimePicker timePicker = createTimePicker(timePickerView);
            timePickerView.findViewById(R.id.selectTimeButton).setOnClickListener(v -> showMaterialTimePicker(timePicker));
        }

        // Button to add another time picker
        addAnotherTimeButton = new Button(getContext());
        addAnotherTimeButton.setText("+ Add Another Time");
        addAnotherTimeButton.setOnClickListener(v -> addNewTimePicker());
        timePickerContainer.addView(addAnotherTimeButton);
    }

    private MaterialTimePicker createTimePicker(View timePickerView) {
        return new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select Time")
                .build();
    }

    private void addNewTimePicker() {
        // Inflate a new time picker view and add it to the container
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View newTimePickerView = inflater.inflate(R.layout.time_picker_item, null);
        timePickerContainer.addView(newTimePickerView, timePickerContainer.getChildCount() - 1);

        MaterialTimePicker timePicker = createTimePicker(newTimePickerView);
        newTimePickerView.findViewById(R.id.selectTimeButton).setOnClickListener(v -> showMaterialTimePicker(timePicker));
    }

    private void showMaterialTimePicker(MaterialTimePicker timePicker) {
        timePicker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = timePicker.getHour();
            int selectedMinute = timePicker.getMinute();
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedTime.set(Calendar.MINUTE, selectedMinute);

            // Format the time
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTime = sdf.format(selectedTime.getTime());

            // Store the selected time
            if (!selectedTimes.contains(formattedTime)) {
                selectedTimes.add(formattedTime);
            }
        });

        timePicker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    // Abstract method to submit the form, to be implemented by subclasses
    protected abstract void submitForm();
}
