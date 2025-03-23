package com.example.medassist.ui.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.medassist.databinding.FragmentAppointmentBinding;
import com.example.medassist.databinding.FragmentExerciseBinding;
import com.example.medassist.ui.exercise.ExerciseViewModel;

public class AppointmentFragment extends Fragment {
    private FragmentAppointmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppointmentViewModel appointmentViewModel =
                new ViewModelProvider(this).get(AppointmentViewModel.class);

        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAppointment;
        appointmentViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
