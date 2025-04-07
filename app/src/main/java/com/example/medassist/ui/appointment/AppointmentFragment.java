package com.example.medassist.ui.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medassist.databinding.FragmentAppointmentBinding;
import com.example.medassist.ui.reminders.ReminderRepository;
import com.example.medassist.ui.appointment.AppointmentFormDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class AppointmentFragment extends Fragment {

    private FragmentAppointmentBinding binding;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            binding.addButton.setEnabled(false);
        } else {
            binding.addButton.setEnabled(true);
        }

        binding.calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            binding.selectedDate.setText("Selected Date: " + selectedDate);
        });

        binding.addButton.setOnClickListener(v -> {
            // Open the AppointmentFormDialog to fill in the appointment details
            AppointmentFormDialog appointmentFormDialog = new AppointmentFormDialog();

            // Get selected date
            String selectedDate = binding.selectedDate.getText().toString();
            if (selectedDate.equals("Selected Date: Not Set")) {
                Toast.makeText(getContext(), "Please select a date before adding an appointment.", Toast.LENGTH_SHORT).show();
                return;
            }

            AppointmentRepository repository = new AppointmentRepository(requireContext());

            appointmentFormDialog.setOnAppointmentAddedListener((clinicName, location, appointmentStart, appointmentEnd, frequency, repeatAmount, repeatUnit, description) -> {
                String cleanedDate = selectedDate.replace("Selected Date: ", "");

                repository.saveAppointment(
                        clinicName,
                        location,
                        appointmentStart,
                        appointmentEnd,
                        frequency,
                        repeatAmount,
                        repeatUnit,
                        description,
                        cleanedDate,
                        new ReminderRepository.OnOperationCompleteListener() {
                            @Override
                            public void onSuccess() {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Appointment saved successfully!", Toast.LENGTH_SHORT).show();
                                binding.addButton.setEnabled(true);
                                binding.selectedDate.setText("Selected Date: Not Set");
                            }

                            @Override
                            public void onError(String errorMessage) {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Failed to save appointment: " + errorMessage, Toast.LENGTH_SHORT).show();
                                binding.addButton.setEnabled(true);
                            }
                        }
                );
            });



            appointmentFormDialog.show(getChildFragmentManager(), "appointment_form_dialog");
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
