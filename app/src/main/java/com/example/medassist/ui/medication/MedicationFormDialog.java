// First, add this dependency to your build.gradle (Module) file if not already present:
// implementation 'com.google.android.material:material:1.9.0' // Use latest stable version

package com.example.medassist.ui.medication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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

public class MedicationFormDialog extends DialogFragment {
    private EditText nameEditText;
    private EditText medicationAmountEditText;
    private Spinner doseUnitSpinner;
    private EditText durationEditText;
    private Spinner durationUnitSpinner;
    private Spinner frequencySpinner;
    private EditText sideEffectsEditText;
    private LinearLayout timePickerContainer;
    private Button addAnotherTimeButton;
    private RadioGroup foodRelationRadioGroup;
    private List<String> selectedTimes;
    private OnMedicationAddedListener listener;

    private FrequencyHandler frequencyHandler;

    public interface OnMedicationAddedListener {
        void onMedicationAdded(String name, String dosage, String frequency, List<String> times, String sideEffects, String foodRelation);
    }

    public void setOnMedicationAddedListener(OnMedicationAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_medication, null);


        // Initialize fields
        foodRelationRadioGroup = view.findViewById(R.id.foodRelationRadioGroup);
        nameEditText = view.findViewById(R.id.medicationNameEditText);
        medicationAmountEditText = view.findViewById(R.id.medicationAmountEditText);
        doseUnitSpinner = view.findViewById(R.id.doseUnitSpinner);
        durationEditText = view.findViewById(R.id.durationEditText);
        durationUnitSpinner = view.findViewById(R.id.durationUnitSpinner);
        frequencySpinner = view.findViewById(R.id.frequencySpinner);
        sideEffectsEditText = view.findViewById(R.id.medicationSideEffectsEditText);
        timePickerContainer = view.findViewById(R.id.timePickerContainer);
        selectedTimes = new ArrayList<>();

        setupRadioButtonListeners();

        // Set up the initial time picker
        setupInitialTimePicker();

        // Set up done button
        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> submitForm());

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        builder.setView(view);

        // Set up dose unit spinner
        ArrayAdapter<CharSequence> doseUnitAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.dose_units, android.R.layout.simple_spinner_item);
        doseUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doseUnitSpinner.setAdapter(doseUnitAdapter);

// Set up duration unit spinner
        ArrayAdapter<CharSequence> durationUnitAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.duration_units, android.R.layout.simple_spinner_item);
        durationUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationUnitSpinner.setAdapter(durationUnitAdapter);

// Set up frequency spinner
        String[] frequencies = {"Once daily", "Twice daily", "Three times daily",
                "Four times daily", "Every morning", "Every night",
                "Every other day", "Weekly", "As needed", "Custom..."};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, frequencies);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(frequencyAdapter);

        return builder.create();
    }
    private void setupRadioButtonListeners() {
        // Get references to the radio buttons
        RadioButton beforeRadio = foodRelationRadioGroup.findViewById(R.id.beforeMealRadioButton);
        RadioButton afterRadio = foodRelationRadioGroup.findViewById(R.id.afterMealRadioButton);
        RadioButton naRadio = foodRelationRadioGroup.findViewById(R.id.naMealRadioButton);

        // List of all radio buttons for easy iteration
        RadioButton[] radioButtons = {beforeRadio, afterRadio, naRadio};

        // Set initial state (in case of editing a medication)
        updateRadioButtonStyles();

        // Add listener to change colors when selection changes
        foodRelationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateRadioButtonStyles();
        });
    }

    private void updateRadioButtonStyles() {
        RadioButton beforeRadio = foodRelationRadioGroup.findViewById(R.id.beforeMealRadioButton);
        RadioButton afterRadio = foodRelationRadioGroup.findViewById(R.id.afterMealRadioButton);
        RadioButton naRadio = foodRelationRadioGroup.findViewById(R.id.naMealRadioButton);

        // Update each radio button's text color based on selection state
        updateRadioButtonStyle(beforeRadio);
        updateRadioButtonStyle(afterRadio);
        updateRadioButtonStyle(naRadio);
    }

    private void updateRadioButtonStyle(RadioButton radioButton) {
        if (radioButton.isChecked()) {
            radioButton.setTextColor(getResources().getColor(android.R.color.white));
            // You could also change the background here if you want to do it programmatically
            // radioButton.setBackgroundResource(R.drawable.selected_radio_background);
        } else {
            radioButton.setTextColor(getResources().getColor(android.R.color.black));
            // radioButton.setBackgroundResource(R.drawable.unselected_radio_background);
        }
    }

    private void submitForm() {
        // Get values from the form
        String name = nameEditText.getText().toString().trim();
        String amount = medicationAmountEditText.getText().toString().trim();
        String doseUnit = doseUnitSpinner.getSelectedItem().toString();
        String dosage = amount + " " + doseUnit;
        String frequency = frequencySpinner.getSelectedItem().toString();
        String sideEffects = sideEffectsEditText.getText().toString().trim();

        // Get food relation
        String foodRelation = "N.A";
        int selectedId = foodRelationRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.beforeMealRadioButton) {
            foodRelation = "Before";
        } else if (selectedId == R.id.afterMealRadioButton) {
            foodRelation = "After";
        }

        // Validate inputs
        if (!name.isEmpty() && !amount.isEmpty() && !selectedTimes.isEmpty()) {
            if (listener != null) {
                listener.onMedicationAdded(name, dosage, frequency, selectedTimes, sideEffects, foodRelation);
                dismiss();
            }
        } else {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupInitialTimePicker() {
        // Get the first time picker view
        View timePickerView = timePickerContainer.getChildAt(0);
        if (timePickerView != null) {
            TextView timeText = timePickerView.findViewById(R.id.timePickerText);
            ImageButton selectTimeButton = timePickerView.findViewById(R.id.selectTimeButton);

            selectTimeButton.setOnClickListener(v -> showMaterialTimePicker(timeText));
        }

        // Add button for adding another time (since it's missing in your layout)
        Button addTimeButton = new Button(getContext());
        addTimeButton.setText("+ Add Another Time");
        addTimeButton.setOnClickListener(v -> addNewTimePicker());
        timePickerContainer.addView(addTimeButton);
        addAnotherTimeButton = addTimeButton;
    }

    private void addNewTimePicker() {
        // Inflate a new time picker view
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

        // Make remove button visible (it's hidden in the layout by default)
        removeTimeButton.setVisibility(View.VISIBLE);

        // Add the new time picker to the container before the "add" button
        timePickerContainer.addView(newTimePickerView, timePickerContainer.getChildCount() - 1);
    }

    private void showMaterialTimePicker(TextView targetTextView) {
        // Get current time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create Material TimePicker
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Select Medication Time")
                .build();

        // Set listener for when time is selected
        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = materialTimePicker.getHour();
            int selectedMinute = materialTimePicker.getMinute();

            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedTime.set(Calendar.MINUTE, selectedMinute);

            // Format the time
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTime = sdf.format   (selectedTime.getTime());

            // Update the text view
            targetTextView.setText(formattedTime);

            // Store the selected time
            String currentText = targetTextView.getText().toString();
            if (!currentText.equals("Enter time") && selectedTimes.contains(currentText)) {
                selectedTimes.remove(currentText);
            }
            selectedTimes.add(formattedTime);
        });

        // Show the picker
        materialTimePicker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");
    }
    private Medication medicationToEdit;

    public void setMedication(Medication medication) {
        this.medicationToEdit = medication;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // If we're editing a medication, pre-fill the form
        if (medicationToEdit != null) {
            nameEditText.setText(medicationToEdit.getName());

            // Parse dosage to get amount and unit
            String dosage = medicationToEdit.getDosage();
            if (dosage != null && !dosage.isEmpty()) {
                String[] parts = dosage.split(" ", 2);
                if (parts.length > 0) {
                    medicationAmountEditText.setText(parts[0]);
                    if (parts.length > 1) {
                        // Find and select the matching unit in the spinner
                        selectSpinnerItemByValue(doseUnitSpinner, parts[1]);
                    }
                }
            }

            // Set frequency
            selectSpinnerItemByValue(frequencySpinner, medicationToEdit.getFrequency());

            // Set notification times
            List<String> times = medicationToEdit.getNotificationTimes();
            if (times != null && !times.isEmpty()) {
                // Clear default time picker
                timePickerContainer.removeAllViews();

                // Add time pickers for each saved time
                for (String time : times) {
                    View timePickerView = getLayoutInflater().inflate(R.layout.time_picker_item, null);
                    TextView timeText = timePickerView.findViewById(R.id.timePickerText);
                    ImageButton selectTimeButton = timePickerView.findViewById(R.id.selectTimeButton);
                    ImageButton removeTimeButton = timePickerView.findViewById(R.id.removeTimeButton);

                    timeText.setText(time);
                    selectedTimes.add(time);

                    selectTimeButton.setOnClickListener(v -> showMaterialTimePicker(timeText));

                    if (times.size() > 1) {
                        removeTimeButton.setVisibility(View.VISIBLE);
                        removeTimeButton.setOnClickListener(v -> {
                            timePickerContainer.removeView(timePickerView);
                            selectedTimes.remove(time);
                        });
                    }

                    timePickerContainer.addView(timePickerView);
                }

            }

            // Set side effects
            sideEffectsEditText.setText(medicationToEdit.getSideEffects());

            // Set food relation
            String foodRelation = medicationToEdit.getFoodRelation();
            if (foodRelation != null) {
                if (foodRelation.equals("Before")) {
                    foodRelationRadioGroup.check(R.id.beforeMealRadioButton);
                } else if (foodRelation.equals("After")) {
                    foodRelationRadioGroup.check(R.id.afterMealRadioButton);
                } else {
                    foodRelationRadioGroup.check(R.id.naMealRadioButton);
                }
            }
        }
    }

    // Helper method to select an item in a spinner by its string value
    private void selectSpinnerItemByValue(Spinner spinner, String value) {
        if (value == null) return;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}