package com.example.medassist.ui.medication;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository class to handle all Firebase operations related to medications
 */
public class MedicationRepository {
    private static final String DB_URL = "https://medassist-fdddd-default-rtdb.asia-southeast1.firebasedatabase.app";
    private final DatabaseReference mDatabase;
    private final FirebaseAuth mAuth;
    private final Context context;

    public interface OnMedicationsLoadedListener {
        void onMedicationsLoaded(List<Medication> medications);
        void onError(String errorMessage);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onError(String errorMessage);
    }

    public MedicationRepository(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();
    }

    /**
     * Save a medication to Firebase
     */
    public void saveMedication(Medication medication, OnOperationCompleteListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("Please log in to save medications");
            return;
        }

        String userId = currentUser.getUid();
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
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    /**
     * Load all medications for a specific date
     */
    public void loadMedicationsForDate(LocalDate date, OnMedicationsLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("User not logged in");
            return;
        }

        String userId = currentUser.getUid();
        String dateStr = date.toString();

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

                        listener.onMedicationsLoaded(medicationList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }

    /**
     * Delete a medication from Firebase
     */
    public void deleteMedication(Medication medication, OnOperationCompleteListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null || medication.getDate() == null) {
            listener.onError("User not logged in or invalid medication date");
            return;
        }

        String userId = currentUser.getUid();
        String dateStr = medication.getDate().toString();
        String medId = String.valueOf(medication.getId());

        mDatabase.child("medications").child(userId).child(dateStr).child(medId).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }
}