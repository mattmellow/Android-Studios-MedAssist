package com.example.medassist.ui.allappointments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medassist.databinding.FragmentAllAppointmentsBinding;
import com.example.medassist.ui.appointment.Appointment;
import com.example.medassist.ui.appointment.AppointmentAdapter;

import java.util.ArrayList;

public class AllAppointmentsFragment extends Fragment {

    private FragmentAllAppointmentsBinding binding;
    private AllAppointmentsViewModel allAppointmentsViewModel;
    private AppointmentAdapter appointmentAdapter;

    public static AllAppointmentsFragment newInstance() {
        return new AllAppointmentsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        allAppointmentsViewModel = new ViewModelProvider(this, new AllAppointmentsViewModelFactory(getContext())).get(AllAppointmentsViewModel.class);
        binding = FragmentAllAppointmentsBinding.inflate(inflater, container, false);

        setupAppointmentList();
        observeViewModel();

        return binding.getRoot();
    }

    private void setupAppointmentList() {
        appointmentAdapter = new AppointmentAdapter(new ArrayList<>());
        binding.appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.appointmentRecyclerView.setAdapter(appointmentAdapter);

        appointmentAdapter.setOnAppointmentClickListener(new AppointmentAdapter.OnAppointmentClickListener() {
            @Override
            public void onAppointmentClick(Appointment appointment) {
                Toast.makeText(getContext(), "Selected: " + appointment.getClinicName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAppointmentLongClick(Appointment appointment, int position) {
                // Optional: implement long click logic here
            }
        });

    }

    private void observeViewModel() {
        allAppointmentsViewModel.getAppointments().observe(getViewLifecycleOwner(), appointments -> {
            appointmentAdapter.updateAppointments(appointments);

            if (appointments.isEmpty()) {
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                binding.appointmentRecyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyStateLayout.setVisibility(View.GONE);
                binding.appointmentRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        allAppointmentsViewModel.loadAppointments();
    }
}
