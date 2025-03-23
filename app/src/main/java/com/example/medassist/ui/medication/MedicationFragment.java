package com.example.medassist.ui.medication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.medassist.databinding.FragmentMedicationBinding;

public class MedicationFragment extends Fragment {

    private FragmentMedicationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MedicationViewModel medicationViewModel =
                new ViewModelProvider(this).get(MedicationViewModel.class);

        binding = FragmentMedicationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMedication;
        medicationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}