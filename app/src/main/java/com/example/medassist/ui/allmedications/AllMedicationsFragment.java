package com.example.medassist.ui.allmedications;

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

import com.example.medassist.databinding.FragmentAllMedicationsBinding;
import com.example.medassist.R;
import com.example.medassist.ui.medication.DialogHelper;
import com.example.medassist.ui.medication.Medication;
import com.example.medassist.ui.medication.MedicationAdapter;

import java.util.ArrayList;

public class AllMedicationsFragment extends Fragment {

    private FragmentAllMedicationsBinding binding;
    private AllMedicationsViewModel allMedicationsViewModel;
    private MedicationAdapter medicationAdapter;

    public static AllMedicationsFragment newInstance() {
        return new AllMedicationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        allMedicationsViewModel = new ViewModelProvider(this, new AllMedicationsViewModelFactory(getContext())).get(AllMedicationsViewModel.class);

        binding = FragmentAllMedicationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupMedicationList();
        observeViewModel();

        return root;
    }

    private void setupMedicationList() {
        medicationAdapter = new MedicationAdapter(new ArrayList<>());
        binding.medicationRecyclerView.setAdapter(medicationAdapter);
        binding.medicationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        medicationAdapter.setOnMedicationClickListener(new MedicationAdapter.OnMedicationClickListener() {
            @Override
            public void onMedicationClick(Medication medication) {
                Toast.makeText(getContext(), "Selected: " + medication.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMedicationLongClick(Medication medication, int position) {
                showDeleteConfirmationDialog(medication);  // Call delete confirmation on long click
            }
        });
    }

    private void observeViewModel() {
        allMedicationsViewModel.getMedications().observe(getViewLifecycleOwner(), medications -> {
            medicationAdapter.updateMedications(medications);

            // Show empty state if no medications
            if (medications.isEmpty()) {
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                binding.medicationRecyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyStateLayout.setVisibility(View.GONE);
                binding.medicationRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Load all medications from the ViewModel
        allMedicationsViewModel.loadMedications();
    }

    // Show delete confirmation dialog and remove medication
    private void showDeleteConfirmationDialog(Medication medication) {
        // This is where the confirmation dialog should show to confirm the delete action
        DialogHelper.showDeleteConfirmationDialog(getContext(), medication, () -> {
            allMedicationsViewModel.removeMedication(medication);  // Remove medication using ViewModel
        });
    }
}
