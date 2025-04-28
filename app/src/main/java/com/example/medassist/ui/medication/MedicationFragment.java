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


public class MedicationFragment extends Fragment implements DatePicker.OnDateSelectedListener {

    private FragmentMedicationBinding binding;
    private DatePicker datePickerFragment;
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
        datePickerFragment = (DatePicker) getChildFragmentManager().findFragmentById(R.id.DatePickerFragment);
        if (datePickerFragment == null) {
            datePickerFragment = new DatePicker();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.DatePickerFragment, datePickerFragment)
                    .commit();
        }

        datePickerFragment.setOnDateSelectedListener(this);
        datePickerFragment.setOnMonthChangeListener((month, year) -> {
            binding.currentMonthTextView.setText(month);
            binding.currentYearTextView.setText(String.valueOf(year));
        });

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
            String id = String.valueOf(System.currentTimeMillis());
            Medication medication = new Medication(id, name, dosage, frequency, times, sideEffects, duration, durationUnit);
            medication.setFoodRelation(foodRelation);
            medication.setDate(datePickerFragment.getSelectedDate());
            medicationViewModel.addMedication(medication);
        });
    }

    @Override
    public void onDateSelected(LocalDate date) {
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        int year = date.getYear();
        binding.currentMonthTextView.setText(month);
        binding.currentYearTextView.setText(String.valueOf(year));
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
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }
        medicationViewModel.loadMedications(selectedDate);
    }

    private void showDeleteConfirmationDialog(Medication medication) {
        DialogHelper.showDeleteConfirmationDialog(getContext(), medication, () -> {
            medicationViewModel.removeMedication(medication);
        });
    }
}
