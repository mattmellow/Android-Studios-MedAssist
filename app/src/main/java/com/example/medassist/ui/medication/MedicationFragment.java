package com.example.medassist.ui.medication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medassist.databinding.FragmentMedicationBinding;
import com.example.medassist.R;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Fragment for medication management
 */
public class MedicationFragment extends Fragment implements DatePickerFragment.OnDateSelectedListener {

    private FragmentMedicationBinding binding;
    private DatePickerFragment datePickerFragment;
    private MedicationViewModel medicationViewModel;
    private MedicationAdapter medicationAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        medicationViewModel = new ViewModelProvider(this).get(MedicationViewModel.class);

        binding = FragmentMedicationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeDatePicker();
        initializeButtons();
        setupMedicationList();
        observeViewModel();

        return root;
    }

    private void initializeDatePicker() {
        datePickerFragment = (DatePickerFragment)
                getChildFragmentManager().findFragmentById(R.id.DatePickerFragment);

        if (datePickerFragment == null) {
            datePickerFragment = new DatePickerFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.DatePickerFragment, datePickerFragment)
                    .commit();
        }

        datePickerFragment.setOnDateSelectedListener(this);
        datePickerFragment.setOnMonthChangeListener((month, year) -> {
            binding.currentMonthTextView.setText(month);
            binding.currentYearTextView.setText(String.valueOf(year));
        });

        // Set initial month and year
        LocalDate currentDate = LocalDate.now();
        String currentMonth = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        int currentYear = currentDate.getYear();

        binding.currentMonthTextView.setText(currentMonth);
        binding.currentYearTextView.setText(String.valueOf(currentYear));
    }

    private void initializeButtons() {
        binding.addMedicationButton.setOnClickListener(v -> showAddMedicationDialog());
    }

    private void showAddMedicationDialog() {
        DialogHelper.showAddMedicationDialog(this, (name, dosage, frequency, times, sideEffects, foodRelation) -> {
            // Generate unique ID
            long id = System.currentTimeMillis();

// Create new medication with the list of times
            Medication medication = new Medication(id, name, dosage, frequency, times, sideEffects);

// Set the food relation
            medication.setFoodRelation(foodRelation);

// Set the date
            medication.setDate(datePickerFragment.getSelectedDate());
            // Add to view model
            medicationViewModel.addMedication(medication);
        });
    }

    @Override
    public void onDateSelected(LocalDate date) {
        // Update month and year display
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        int year = date.getYear();

        binding.currentMonthTextView.setText(month);
        binding.currentYearTextView.setText(String.valueOf(year));

        // Update view model
        medicationViewModel.setSelectedDate(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupMedicationList() {
        // Create adapter with empty list initially
        medicationAdapter = new MedicationAdapter(new ArrayList<>());
        binding.medicationRecyclerView.setAdapter(medicationAdapter);
        binding.medicationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Handle click events
        medicationAdapter.setOnMedicationClickListener(new MedicationAdapter.OnMedicationClickListener() {
            @Override
            public void onMedicationClick(Medication medication) {
                // This could be expanded to show details or edit dialog
                Toast.makeText(getContext(), "Selected: " + medication.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMedicationLongClick(Medication medication, int position) {
                // Show delete option
                showDeleteConfirmationDialog(medication);
            }
        });
    }

    private void showEditMedicationDialog(Medication medication) {
        DialogHelper.showEditMedicationDialog(this, medication, (name, dosage, frequency, times, sideEffects, foodRelation) -> {
            // Update medication with new values
            medication.setName(name);
            medication.setDosage(dosage);
            medication.setFrequency(frequency);
            medication.setNotificationTimes(times);
            medication.setSideEffects(sideEffects);
            medication.setFoodRelation(foodRelation);

            // Update in view model
            medicationViewModel.updateMedication(medication);
        });
    }

    private void observeViewModel() {
        // Observe medications from ViewModel
        medicationViewModel.getMedications().observe(getViewLifecycleOwner(), medications -> {
            medicationAdapter.updateMedications(medications);

            // Toggle empty state visibility
            if (medications.isEmpty()) {
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                binding.medicationRecyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyStateLayout.setVisibility(View.GONE);
                binding.medicationRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Observe loading state
        medicationViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Could show loading indicator here if needed
        });

        // Observe error messages
        medicationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(Medication medication) {
        DialogHelper.showDeleteConfirmationDialog(getContext(), medication, () -> {
            medicationViewModel.removeMedication(medication);
        });
    }
}