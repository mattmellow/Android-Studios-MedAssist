package com.example.medassist.ui.medication;

import android.app.AlertDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MedicationFragment extends Fragment implements WeeklyCalendarFragment.OnDateSelectedListener {

    private FragmentMedicationBinding binding;
    private WeeklyCalendarFragment weeklyCalendarFragment;
    private MedicationViewModel medicationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        medicationViewModel = new ViewModelProvider(this).get(MedicationViewModel.class);

        binding = FragmentMedicationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        weeklyCalendarFragment = (WeeklyCalendarFragment)
                getChildFragmentManager().findFragmentById(R.id.weeklyCalendarFragment);

        if (weeklyCalendarFragment == null) {
            weeklyCalendarFragment = new WeeklyCalendarFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.weeklyCalendarFragment, weeklyCalendarFragment)
                    .commit();
        }

        weeklyCalendarFragment.setOnDateSelectedListener(this);
        weeklyCalendarFragment.setOnMonthChangeListener((month, year) -> {
            binding.currentMonthTextView.setText(month);
            binding.currentYearTextView.setText(String.valueOf(year));
        });

        LocalDate currentDate = LocalDate.now();
        String currentMonth = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        int currentYear = currentDate.getYear();

        binding.currentMonthTextView.setText(currentMonth);
        binding.currentYearTextView.setText(String.valueOf(currentYear));

        binding.addMedicationButton.setOnClickListener(v -> {
            showAddMedicationDialog();
        });

        // Also set up the empty state add button
        binding.addFirstMedicationButton.setOnClickListener(v -> {
            showAddMedicationDialog();
        });

        // Set up RecyclerView for medications
        setupMedicationList();

        // Initialize selected date to today
        medicationViewModel.setSelectedDate(LocalDate.now());

        return root;

    }
    private void saveMedicationToFirebase(Medication medication) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // User not logged in
            Toast.makeText(getContext(), "Please log in to save medications", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://medassist-fdddd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        // Format the date to a string
        String dateStr = medication.getDate().toString();

        // Create a map of medication data
        Map<String, Object> medData = new HashMap<>();
        medData.put("name", medication.getName());
        medData.put("dosage", medication.getDosage());
        medData.put("frequency", medication.getFrequency());
        medData.put("time", medication.getTime());
        medData.put("sideEffects", medication.getSideEffects());
        medData.put("date", dateStr);
        medData.put("id", medication.getId());

        // Save to Firebase - using structure: medications/userId/date/medicationId
        String medId = String.valueOf(medication.getId());
        mDatabase.child("medications").child(userId).child(dateStr).child(medId).setValue(medData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Medication saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save medication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadMedicationsForDate(LocalDate date) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // User not logged in
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://medassist-fdddd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        // Format date to string
        String dateStr = date.toString();

        // Clear existing medications for this date
        medicationViewModel.clearMedications();

        // Query medications for this user and date
        mDatabase.child("medications").child(userId).child(dateStr)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Medication> medicationList = new ArrayList<>();

                        for (DataSnapshot medSnapshot : dataSnapshot.getChildren()) {
                            String name = medSnapshot.child("name").getValue(String.class);
                            String dosage = medSnapshot.child("dosage").getValue(String.class);
                            String frequency = medSnapshot.child("frequency").getValue(String.class);
                            String time = medSnapshot.child("time").getValue(String.class);
                            String sideEffects = medSnapshot.child("sideEffects").getValue(String.class);
                            Long id = medSnapshot.child("id").getValue(Long.class);

                            if (name != null && dosage != null && frequency != null && time != null && id != null) {
                                Medication medication = new Medication(id, name, dosage, frequency, time, sideEffects);
                                medication.setDate(date);
                                medicationList.add(medication);
                            }
                        }

                        // Update the ViewModel with loaded medications
                        for (Medication med : medicationList) {
                            medicationViewModel.addMedication(med);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to load medications: " + databaseError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteMedicationFromFirebase(Medication medication) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null || medication.getDate() == null) {
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://medassist-fdddd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        String dateStr = medication.getDate().toString();
        String medId = String.valueOf(medication.getId());

        mDatabase.child("medications").child(userId).child(dateStr).child(medId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Medication deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete medication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void showAddMedicationDialog() {
        AddMedicationDialog dialog = new AddMedicationDialog();
        dialog.setOnMedicationAddedListener((name, dosage, frequency, time, sideEffects) -> {
            // Generate unique ID (you'll replace this with Firebase push ID later)
            long id = System.currentTimeMillis();

            // Create new medication
            Medication medication = new Medication(id, name, dosage, frequency, time, sideEffects);

            // Set the date to the currently selected date
            medication.setDate(weeklyCalendarFragment.getSelectedDate());

            // Add to view model
            medicationViewModel.addMedication(medication);

            // Save to Firebase (we'll implement this later)
            saveMedicationToFirebase(medication);
        });
        dialog.show(getChildFragmentManager(), "AddMedicationDialog");
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
        // Create adapter with empty list initially
        MedicationAdapter adapter = new MedicationAdapter(new ArrayList<>());
        binding.medicationRecyclerView.setAdapter(adapter);
        binding.medicationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Handle click events
        adapter.setOnMedicationClickListener(new MedicationAdapter.OnMedicationClickListener() {
            @Override
            public void onMedicationClick(Medication medication) {
                // Show details or edit dialog
            }

            @Override
            public void onMedicationLongClick(Medication medication, int position) {
                // Show delete option
                showDeleteConfirmationDialog(medication, position);
            }
        });

        // Observe medications from ViewModel
        medicationViewModel.getMedications().observe(getViewLifecycleOwner(), medications -> {
            adapter.updateMedications(medications);

            // Toggle empty state visibility
            if (medications.isEmpty()) {
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                binding.medicationRecyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyStateLayout.setVisibility(View.GONE);
                binding.medicationRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Observe selected date changes
        medicationViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            // Load medications for this date from Firebase
            loadMedicationsForDate(date);
        });
    }

    private void showDeleteConfirmationDialog(Medication medication, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Medication")
                .setMessage("Are you sure you want to delete " + medication.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove from view model
                    medicationViewModel.removeMedication(medication.getId());

                    // Remove from Firebase
                    deleteMedicationFromFirebase(medication);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}