package com.example.medassist.ui.medication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.medassist.R;

public class MedicationFormDialog extends DialogFragment {
    private EditText nameEditText;
    private EditText dosageEditText;
    private EditText frequencyEditText;
    private EditText timeEditText;
    private EditText sideEffectsEditText;
    private OnMedicationAddedListener listener;

    public interface OnMedicationAddedListener {
        void onMedicationAdded(String name, String dosage, String frequency, String time, String sideEffects);
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

        nameEditText = view.findViewById(R.id.medicationNameEditText);
        dosageEditText = view.findViewById(R.id.medicationDosageEditText);
        frequencyEditText = view.findViewById(R.id.medicationFrequencyEditText);
        timeEditText = view.findViewById(R.id.medicationTimeEditText);
        sideEffectsEditText = view.findViewById(R.id.medicationSideEffectsEditText);

        builder.setView(view)
                .setTitle("Add Medication")
                .setPositiveButton("Add", (dialog, id) -> {
                    // Get values from the form
                    String name = nameEditText.getText().toString().trim();
                    String dosage = dosageEditText.getText().toString().trim();
                    String frequency = frequencyEditText.getText().toString().trim();
                    String time = timeEditText.getText().toString().trim();
                    String sideEffects = sideEffectsEditText.getText().toString().trim();

                    // Validate inputs
                    if (!name.isEmpty() && !dosage.isEmpty() && !frequency.isEmpty() && !time.isEmpty()) {
                        if (listener != null) {
                            listener.onMedicationAdded(name, dosage, frequency, time, sideEffects);
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    MedicationFormDialog.this.getDialog().cancel();
                });

        return builder.create();
    }
}