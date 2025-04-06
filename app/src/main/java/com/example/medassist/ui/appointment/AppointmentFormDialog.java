package com.example.medassist.ui.appointment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.medassist.R;
import com.example.medassist.ui.reminders.ReminderFormDialog;

import java.util.ArrayList;

public class AppointmentFormDialog extends ReminderFormDialog {

    private EditText clinicNameEditText;
    private EditText locationEditText;
    private TextView appointmentStartEditText;
    private TextView appointmentEndEditText;
    private Spinner frequencySpinner;
    private EditText repeatAmountEditText;
    private Spinner repeatUnitSpinner;
    private EditText descriptionEditText;

    private OnAppointmentAddedListener listener;

    public interface OnAppointmentAddedListener {
        void onAppointmentAdded(String clinicName, String location, String appointmentStart, String appointmentEnd,
                                String frequency, String repeatAmount, String repeatUnit, String description);
    }

    public void setOnAppointmentAddedListener(OnAppointmentAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResourceId(R.layout.add_appointment); // Link to the correct XML layout
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

        selectedTimes = new ArrayList<>();

        initializeFormFields(view);

        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> submitForm());

        builder.setView(view);
        return builder.create();
    }



    @Override
    protected void initializeFormFields(View view) {
        super.initializeFormFields(view); // Optional base setup (not needed here but safe)

        clinicNameEditText = view.findViewById(R.id.clinicNameEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        appointmentStartEditText = view.findViewById(R.id.appointmentStartEditText);
        appointmentEndEditText = view.findViewById(R.id.appointmentEndEditText);
        repeatAmountEditText = view.findViewById(R.id.repeatAmountEditText);
        repeatUnitSpinner = view.findViewById(R.id.repeatUnitSpinner);
        frequencySpinner = view.findViewById(R.id.frequencySpinner);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);

        // Set up frequency spinner
        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.appointment_frequency_options, android.R.layout.simple_spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(freqAdapter);

        // Set up repeat unit spinner
        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.duration_units, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatUnitSpinner.setAdapter(repeatAdapter);

        // 🕒 Use inherited ReminderFormDialog method to show time picker when clicking the clock icons
        view.findViewById(R.id.selectStartTimeButton).setOnClickListener(v ->
                showMaterialTimePicker(appointmentStartEditText));

        view.findViewById(R.id.selectEndTimeButton).setOnClickListener(v ->
                showMaterialTimePicker(appointmentEndEditText));
    }

    @Override
    protected void submitForm() {
        String clinicName = clinicNameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String appointmentStart = appointmentStartEditText.getText().toString().trim();
        String appointmentEnd = appointmentEndEditText.getText().toString().trim();
        String frequency = frequencySpinner.getSelectedItem().toString().trim();
        String repeatAmount = repeatAmountEditText.getText().toString().trim();
        String repeatUnit = repeatUnitSpinner.getSelectedItem().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (!clinicName.isEmpty() && !location.isEmpty() && !appointmentStart.isEmpty() && !appointmentEnd.isEmpty()) {
            if (listener != null) {
                Log.d("asd", "asd"+listener);
                listener.onAppointmentAdded(clinicName, location, appointmentStart, appointmentEnd, frequency, repeatAmount, repeatUnit, description);
                dismiss();
            }
            else {
                Log.d("asd", "asd");
            }
        } else {
            Toast.makeText(getContext(), "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void populateFields(Appointment appointment) {
        clinicNameEditText.setText(appointment.getClinicName());
        locationEditText.setText(appointment.getLocation());
        appointmentStartEditText.setText(appointment.getAppointmentStart());
        appointmentEndEditText.setText(appointment.getAppointmentEnd());
        frequencySpinner.setSelection(getSpinnerPosition(frequencySpinner, appointment.getFrequency()));
        repeatAmountEditText.setText(appointment.getRepeatAmount());
        repeatUnitSpinner.setSelection(getSpinnerPosition(repeatUnitSpinner, appointment.getRepeatUnit()));
        descriptionEditText.setText(appointment.getDescription());
    }

    private int getSpinnerPosition(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }
}
