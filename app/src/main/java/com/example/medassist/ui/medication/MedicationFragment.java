package com.example.medassist.ui.medication;

import android.os.Bundle;
import android.util.Log;
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
        DialogHelper.showAddMedicationDialog(this, (name, dosage, frequency, times, sideEffects, foodRelation, duration, durationUnit) -> {
            // Generate unique ID
            long id = System.currentTimeMillis();

            // Create new medication with the list of times, duration and duration unit
            Medication medication = new Medication(id, name, dosage, frequency, times, sideEffects, duration, durationUnit);

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
                showDeleteConfirmationDialog(medication);
            }
        });
    }

    private void showEditMedicationDialog(Medication medication) {
        DialogHelper.showEditMedicationDialog(this, medication, (name, dosage, frequency, times, sideEffects, foodRelation, duration, durationUnit) -> {
            // Update medication with new values
            medication.setName(name);
            medication.setDosage(dosage);
            medication.setFrequency(frequency);
            medication.setNotificationTimes(times);
            medication.setSideEffects(sideEffects);
            medication.setFoodRelation(foodRelation);
            medication.setDuration(duration);  // Set new duration value
            medication.setDurationUnit(durationUnit);  // Set new duration unit value

            // Update in view model
            medicationViewModel.updateMedication(medication);
        });
    }

    private void observeViewModel() {
        medicationViewModel.getMedications().observe(getViewLifecycleOwner(), medications -> {
            medicationAdapter.updateMedications(medications);

            if (medications.isEmpty()) {
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                binding.medicationRecyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyStateLayout.setVisibility(View.GONE);
                binding.medicationRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        LocalDate selectedDate = datePickerFragment.getSelectedDate();
        Log.d("filter","i am in med frag observe with date: " + selectedDate);
        // If selectedDate is null, use today's date as a fallback
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }
        Log.d("filter","i am in med frag observe with date: " + selectedDate);
        medicationViewModel.loadMedications(selectedDate);  // Pass the selected date
    }



    private void showDeleteConfirmationDialog(Medication medication) {
        DialogHelper.showDeleteConfirmationDialog(getContext(), medication, () -> {
            medicationViewModel.removeMedication(medication);
        });
    }
}
