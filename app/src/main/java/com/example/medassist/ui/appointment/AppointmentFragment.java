package com.example.medassist.ui.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medassist.databinding.FragmentAppointmentBinding;
import com.example.medassist.ui.appointment.AppointmentFormDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
            appointmentFormDialog.setOnAppointmentAddedListener((clinicName, location, appointmentStart, appointmentEnd, frequency, repeatAmount, repeatUnit, description) -> {
                // Handle the form submission logic here (e.g., save the appointment)
                // For now, just show a toast as an example
                // You can implement the save functionality in the listener
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
