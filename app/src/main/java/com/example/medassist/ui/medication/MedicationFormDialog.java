package com.example.medassist.ui.medication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.medassist.R;
import com.example.medassist.ui.reminders.ReminderFormDialog;

import java.util.List;

public class MedicationFormDialog extends ReminderFormDialog {
    private EditText medicationAmountEditText;
    private Spinner doseUnitSpinner;

    private Spinner durationUnitSpinner;
    private EditText sideEffectsEditText;
    private RadioGroup foodRelationRadioGroup;
    private Medication medicationToEdit;  // Store the medication to edit

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout resource for Medication (this refers to add_medication.xml)
        setLayoutResourceId(R.layout.add_medication);  // This is the XML layout for Medication
    }

    @Override
    protected void initializeFormFields(View view) {
        // Call the parent implementation to initialize common fields
        super.initializeFormFields(view);

        // Initialize specific fields for medication
        medicationAmountEditText = view.findViewById(R.id.medicationAmountEditText);
        doseUnitSpinner = view.findViewById(R.id.doseUnitSpinner);
        durationUnitSpinner = view.findViewById(R.id.durationUnitSpinner);
        sideEffectsEditText = view.findViewById(R.id.medicationSideEffectsEditText);
        foodRelationRadioGroup = view.findViewById(R.id.foodRelationRadioGroup);

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

        // If we're editing a medication, pre-fill the form with the existing data
        if (medicationToEdit != null) {
            populateFields(medicationToEdit);
        }
    }

    // Method to pre-fill the form with existing medication data
    private void populateFields(Medication medication) {
        nameEditText.setText(medication.getName());

        String dosage = medication.getDosage();
        if (dosage != null && !dosage.isEmpty()) {
            String[] parts = dosage.split(" ", 2);
            if (parts.length > 0) {
                medicationAmountEditText.setText(parts[0]);
                if (parts.length > 1) {
                    selectSpinnerItemByValue(doseUnitSpinner, parts[1]);
                }
            }
        }

        // Populate frequency spinner
        selectSpinnerItemByValue(frequencySpinner, medication.getFrequency());

        // Populate side effects
        sideEffectsEditText.setText(medication.getSideEffects());

        // Populate food relation
        String foodRelation = medication.getFoodRelation();
        if (foodRelation != null) {
            if (foodRelation.equals("Before")) {
                foodRelationRadioGroup.check(R.id.beforeMealRadioButton);
            } else if (foodRelation.equals("After")) {
                foodRelationRadioGroup.check(R.id.afterMealRadioButton);
            } else {
                foodRelationRadioGroup.check(R.id.naMealRadioButton);
            }
        }

        // Populate notification times if available
        List<String> times = medication.getNotificationTimes();
    }

    // Method to handle the population of spinners based on string value
    private void selectSpinnerItemByValue(Spinner spinner, String value) {
        if (value == null) return;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    protected void submitForm() {
        // Collect form data
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
            // Notify the listener with the form data (pass all 5 arguments)
            if (listener != null) {
                listener.onReminderAdded(name, dosage, frequency, selectedTimes, sideEffects, foodRelation);
                dismiss();
            }
        } else {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to set the medication data for editing
    public void setMedication(Medication medication) {
        this.medicationToEdit = medication;
    }
}