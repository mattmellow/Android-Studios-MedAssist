package com.example.medassist.ui.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medassist.databinding.FragmentAppointmentBinding;
import com.example.medassist.ui.reminders.ReminderRepository;
import com.example.medassist.ui.appointment.AppointmentFormDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentFragment extends Fragment {

    private FragmentAppointmentBinding binding;
    private FirebaseAuth mAuth;

    private AppointmentAdapter appointmentAdapter;
    private AppointmentRepository repository;
    private String selectedDate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        appointmentAdapter = new AppointmentAdapter(new ArrayList<>());
        binding.appointmentRecyclerView.setAdapter(appointmentAdapter);
        binding.appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        repository = new AppointmentRepository(requireContext());



        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            binding.addButton.setEnabled(false);
        } else {
            binding.addButton.setEnabled(true);
        }

        binding.calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            binding.selectedDate.setText("Selected Date: " + selectedDate);
            loadAppointmentsForSelectedDate(selectedDate);
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
                                loadAppointmentsForSelectedDate(cleanedDate);
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

    private void loadAppointmentsForSelectedDate(String selectedDate) {
        repository.loadAppointments(selectedDate, new ReminderRepository.OnRemindersLoadedListener() {
            @Override
            public void onRemindersLoaded(List<Map<String, Object>> reminders) {
                List<Appointment> appointmentList = new ArrayList<>();
                for (Map<String, Object> map : reminders) {
                    Appointment appointment = (Appointment) map.get("appointment");
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }

                appointmentAdapter.updateAppointments(appointmentList);

                if (appointmentList.isEmpty()) {
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                    binding.appointmentRecyclerView.setVisibility(View.GONE);
                } else {
                    binding.emptyStateLayout.setVisibility(View.GONE);
                    binding.appointmentRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
