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
    private EditText durationEditText;
    private EditText sideEffectsEditText;
    private RadioGroup foodRelationRadioGroup;
    private Medication medicationToEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResourceId(R.layout.add_medication);
    }

    @Override
    protected void initializeFormFields(View view) {
        super.initializeFormFields(view);
        medicationAmountEditText = view.findViewById(R.id.medicationAmountEditText);
        doseUnitSpinner = view.findViewById(R.id.doseUnitSpinner);
        durationUnitSpinner = view.findViewById(R.id.durationUnitSpinner);
        durationEditText = view.findViewById(R.id.durationEditText);
        sideEffectsEditText = view.findViewById(R.id.medicationSideEffectsEditText);
        foodRelationRadioGroup = view.findViewById(R.id.foodRelationRadioGroup);
        //this is in addition to the normal form view, adding the dropdowns
        ArrayAdapter<CharSequence> doseUnitAdapter = ArrayAdapter.createFromResource(getContext(), R.array.dose_units, android.R.layout.simple_spinner_item);
        doseUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doseUnitSpinner.setAdapter(doseUnitAdapter);
        ArrayAdapter<CharSequence> durationUnitAdapter = ArrayAdapter.createFromResource(getContext(), R.array.duration_units, android.R.layout.simple_spinner_item);
        durationUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationUnitSpinner.setAdapter(durationUnitAdapter);

        // add in option to edit,but this has not been fully done, need to add a trigger
        if (medicationToEdit != null) {
            populateFields(medicationToEdit);
        }
    }

    //this is for edit, to load the current data
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

        selectSpinnerItemByValue(frequencySpinner, medication.getFrequency());
        sideEffectsEditText.setText(medication.getSideEffects());
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
        if (medication.getDuration() != null) {
            durationEditText.setText(String.valueOf(medication.getDuration()));
            selectSpinnerItemByValue(durationUnitSpinner, medication.getDurationUnit());
        }
        List<String> times = medication.getNotificationTimes();
    }


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
        String name = nameEditText.getText().toString().trim();
        String amount = medicationAmountEditText.getText().toString().trim();
        String doseUnit = doseUnitSpinner.getSelectedItem().toString();
        String dosage = amount + " " + doseUnit;
        String frequency = frequencySpinner.getSelectedItem().toString();
        String sideEffects = sideEffectsEditText.getText().toString().trim();

        //food
        String foodRelation = "N.A";
        int selectedId = foodRelationRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.beforeMealRadioButton) {
            foodRelation = "Before";
        } else if (selectedId == R.id.afterMealRadioButton) {
            foodRelation = "After";
        }

        //duration
        String duration = durationEditText.getText().toString().trim();
        String durationUnit = durationUnitSpinner.getSelectedItem().toString();

        if (!name.isEmpty() && !amount.isEmpty() && !selectedTimes.isEmpty() && !duration.isEmpty()) {
            if (listener != null) {
                listener.onReminderAdded(name, dosage, frequency, selectedTimes, sideEffects, foodRelation, duration, durationUnit);
                dismiss();
            }
        } else {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
        }
    }


    //not completed, have yet to add the trigger
    public void setMedication(Medication medication) {
        this.medicationToEdit = medication;
    }
}
