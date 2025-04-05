package com.example.medassist.ui.medication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.medassist.ui.reminders.ReminderRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MedicationRepository handles all Firebase operations for medication reminders
 */
public class MedicationRepository extends ReminderRepository {

    public MedicationRepository(Context context) {
        super(context);
    }

    public void saveMedication(Medication medication, OnOperationCompleteListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("Please log in to save medications");
            return;
        }

        String userId = currentUser.getUid();
        String medId = String.valueOf(medication.getId());

        // Get today's date and add it to the medication data
        String currentDate = LocalDate.now().toString();

        // Create a map of medication-specific data, now including the date, duration, and durationUnit as part of the medication details
        Map<String, Object> medData = new HashMap<>();
        medData.put("name", medication.getName());
        medData.put("dosage", medication.getDosage());
        medData.put("frequency", medication.getFrequency());
        medData.put("notificationTimes", medication.getNotificationTimes());
        medData.put("sideEffects", medication.getSideEffects());
        medData.put("foodRelation", medication.getFoodRelation());
        medData.put("date", currentDate);  // Add today's date as part of the medication details
        medData.put("duration", medication.getDuration());  // Add duration of medication
        medData.put("durationUnit", medication.getDurationUnit());  // Add duration unit (e.g., "days", "weeks")

        // Call the save method from the base class
        saveReminder(medData, userId, currentDate, medId, listener);
    }



    public void loadMedications(OnRemindersLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("User not logged in");
            return;
        }

        String userId = currentUser.getUid();

        // Load all medications (iterate through all dates)
        mDatabase.child("reminders").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Map<String, Object>> medicationsList = new ArrayList<>();

                        // Iterate through all dates for the user
                        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                            // Iterate through all medication IDs under each date
                            for (DataSnapshot medSnapshot : dateSnapshot.getChildren()) {
                                Log.d("MedicationRepository", "Snapshot structure: " + medSnapshot.getValue());
                                // Process the medication details
                                String name = medSnapshot.child("name").getValue(String.class);
                                String dosage = medSnapshot.child("dosage").getValue(String.class);
                                String frequency = medSnapshot.child("frequency").getValue(String.class);
                                String sideEffects = medSnapshot.child("sideEffects").getValue(String.class);
                                String foodRelation = medSnapshot.child("foodRelation").getValue(String.class);
                                String duration = medSnapshot.child("duration").getValue(String.class);  // Duration
                                String durationUnit = medSnapshot.child("durationUnit").getValue(String.class);  // Duration unit
                                Long id = Long.parseLong(medSnapshot.getKey()); // Use the key as the ID

                                Log.d("MedicationRepository", "Medication Name: " + name);
                                Log.d("MedicationRepository", "Dosage: " + dosage);
                                Log.d("MedicationRepository", "id: " + id);
                                Log.d("MedicationRepository", "Frequency: " + frequency);


                                // Notification times
                                List<String> notificationTimes = new ArrayList<>();
                                if (medSnapshot.child("notificationTimes").exists()) {
                                    for (DataSnapshot timeSnapshot : medSnapshot.child("notificationTimes").getChildren()) {
                                        String time = timeSnapshot.getValue(String.class);
                                        if (time != null) {
                                            notificationTimes.add(time);
                                        }
                                    }
                                }

                                if (name != null && dosage != null && frequency != null && id != null) {
                                    Medication medication = new Medication(id, name, dosage, frequency, notificationTimes, sideEffects, duration, durationUnit);
                                    medication.setFoodRelation(foodRelation);
                                    Map<String, Object> medData = new HashMap<>();
                                    medData.put("medication", medication); // Store medication object
                                    medicationsList.add(medData);
                                }
                            }
                        }
                        Log.d("MedicationRepository", "Medications List: " + medicationsList.toString());



                        listener.onRemindersLoaded(medicationsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }


    // Delete medication by ID
    public void deleteMedication(Medication medication, OnOperationCompleteListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null || medication == null) {
            listener.onError("User not logged in or invalid medication ID");
            return;
        }

        String userId = currentUser.getUid();
        String medId = String.valueOf(medication.getId());

        // Start the deletion process
        mDatabase.child("reminders").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AtomicBoolean medicationDeleted = new AtomicBoolean(false);

                        // Iterate through all dates for the user
                        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                            // Iterate through all medication IDs under each date
                            for (DataSnapshot medSnapshot : dateSnapshot.getChildren()) {
                                // Compare the ID of each medication with the provided ID
                                if (medSnapshot.getKey().equals(medId)) {
                                    // Delete the medication by ID
                                    medSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                        medicationDeleted.set(true);
                                    });
                                    break;  // Stop once the medication is found and deleted
                                }
                            }

                            // After deleting a medication, check if the date is empty
                            if (dateSnapshot.getChildrenCount() == 0) {
                                dateSnapshot.getRef().removeValue();  // Delete the date if empty
                            }

                            if (medicationDeleted.get()) {
                                break;  // Stop iterating once the medication has been deleted
                            }
                        }

                        // Check if the medication was successfully deleted
                        if (medicationDeleted.get()) {
                            listener.onSuccess();
                        } else {
                            listener.onError("Medication not found or failed to delete");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }




    // Process DataSnapshot for medications
    @Override
    protected List<Map<String, Object>> processDataSnapshot(DataSnapshot dataSnapshot) {
        List<Map<String, Object>> medicationsList = new ArrayList<>();

        for (DataSnapshot medSnapshot : dataSnapshot.getChildren()) {
            String name = medSnapshot.child("name").getValue(String.class);
            String dosage = medSnapshot.child("dosage").getValue(String.class);
            String frequency = medSnapshot.child("frequency").getValue(String.class);
            String sideEffects = medSnapshot.child("sideEffects").getValue(String.class);
            String foodRelation = medSnapshot.child("foodRelation").getValue(String.class);
            String duration = medSnapshot.child("duration").getValue(String.class);  // Get duration
            String durationUnit = medSnapshot.child("durationUnit").getValue(String.class);  // Get duration unit
            Long id = medSnapshot.child("id").getValue(Long.class);

            // Try to get notification times list
            List<String> notificationTimes = new ArrayList<>();
            if (medSnapshot.child("notificationTimes").exists()) {
                for (DataSnapshot timeSnapshot : medSnapshot.child("notificationTimes").getChildren()) {
                    String time = timeSnapshot.getValue(String.class);
                    if (time != null) {
                        notificationTimes.add(time);
                    }
                }
            }



            if (name != null && dosage != null && frequency != null && id != null) {
                Medication medication = new Medication(id, name, dosage, frequency, notificationTimes, sideEffects, duration, durationUnit);
                medication.setFoodRelation(foodRelation);
                Map<String, Object> medData = new HashMap<>();
                medData.put("medication", medication);
                medicationsList.add(medData);
            }
        }

        return medicationsList;
    }
}
