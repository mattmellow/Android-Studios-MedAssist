package com.example.medassist.ui.medication;

import android.content.Context;

import com.example.medassist.ui.reminders.ReminderRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MedicationRepository handles all Firebase operations for medication reminders
 */
public class MedicationRepository extends ReminderRepository {

    public MedicationRepository(Context context) {
        super(context);
    }

    // Save a medication to Firebase
    public void saveMedication(Medication medication, OnOperationCompleteListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("Please log in to save medications");
            return;
        }

        String userId = currentUser.getUid();
        String dateStr = medication.getDate().toString();
        String medId = String.valueOf(medication.getId());

        // Create a map of medication-specific data
        Map<String, Object> medData = new HashMap<>();
        medData.put("name", medication.getName());
        medData.put("dosage", medication.getDosage());
        medData.put("frequency", medication.getFrequency());
        medData.put("notificationTimes", medication.getNotificationTimes());
        medData.put("sideEffects", medication.getSideEffects());
        medData.put("foodRelation", medication.getFoodRelation());

        // Call the save method from the base class
        saveReminder(medData, userId, dateStr, medId, listener);
    }

    // Load medications for a specific date
    public void loadMedicationsForDate(LocalDate date, OnRemindersLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("User not logged in");
            return;
        }

        String userId = currentUser.getUid();
        String dateStr = date.toString();

        loadRemindersForDate(userId, dateStr, listener);
    }

    // Delete medication
    public void deleteMedication(Medication medication, OnOperationCompleteListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null || medication.getDate() == null) {
            listener.onError("User not logged in or invalid medication date");
            return;
        }

        String userId = currentUser.getUid();
        String dateStr = medication.getDate().toString();
        String medId = String.valueOf(medication.getId());

        deleteReminder(userId, dateStr, medId, listener);
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
                Medication medication = new Medication(id, name, dosage, frequency, notificationTimes, sideEffects);
                medication.setFoodRelation(foodRelation);
                // Add the medication data as a Map instead of a Medication object
                Map<String, Object> medData = new HashMap<>();
                medData.put("medication", medication);  // Store the medication object within the map
                medicationsList.add(medData);
            }
        }

        return medicationsList;
    }
}
