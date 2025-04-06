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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

        // Update the path to store under the new "medications" node in the database
        saveReminder(medData, userId, "medications", currentDate, medId, listener);
    }

    // Overloaded method that takes a LocalDate argument for filtering medications
    public void loadMedications(OnRemindersLoadedListener listener, LocalDate selectedDate) {
        Log.d("filter", "i am in repo load med with date: " + selectedDate);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("User not logged in");
            return;
        }

        String userId = currentUser.getUid();
        Log.d("filter", "i am in repo with id: " + userId);

        // Load medications from the "medications" node in the database
        mDatabase.child("reminders").child(userId).child("medications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("filter", "i am in ondatachange: ");
                        List<Map<String, Object>> medicationsList = new ArrayList<>();

                        // Iterate through all dates for the user
                        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                            // Iterate through all medication IDs under each date
                            for (DataSnapshot medSnapshot : dateSnapshot.getChildren()) {
                                // Process the medication details
                                String name = medSnapshot.child("name").getValue(String.class);
                                String dosage = medSnapshot.child("dosage").getValue(String.class);
                                String frequency = medSnapshot.child("frequency").getValue(String.class);
                                String sideEffects = medSnapshot.child("sideEffects").getValue(String.class);
                                String foodRelation = medSnapshot.child("foodRelation").getValue(String.class);
                                String duration = medSnapshot.child("duration").getValue(String.class);  // Duration
                                String durationUnit = medSnapshot.child("durationUnit").getValue(String.class);  // Duration unit
                                Long id = Long.parseLong(medSnapshot.getKey()); // Use the key as the ID
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                LocalDate medStartDate = LocalDate.parse(medSnapshot.child("date").getValue(String.class), formatter);  // Duration unit
                                Log.d("filter", "i am in loop with: " + name + dosage + frequency + sideEffects + foodRelation + duration + durationUnit);

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

                                Log.d("filter", "i am past notif times" + userId);

                                if (name != null && dosage != null && frequency != null && id != null) {
                                    Medication medication = new Medication(id, name, dosage, frequency, notificationTimes, sideEffects, duration, durationUnit);
                                    medication.setFoodRelation(foodRelation);

                                    // Apply filter based on frequency and duration
                                    Log.d("filter", "at fre and dura " + selectedDate);
                                    Log.d("filter", "at fre and dura " + medication);

                                    if (shouldMedicationAppearOnDate(medication, selectedDate, medStartDate)) {
                                        Log.d("filter", "i am in shouldmed condition: " + medication + selectedDate);
                                        Map<String, Object> medData = new HashMap<>();
                                        medData.put("medication", medication); // Store medication object
                                        medicationsList.add(medData);
                                        Log.d("filter", "i am at the end with : " + medData + "123123" + medicationsList);
                                    }
                                    Log.d("filter", "i am past the filter with:" + medicationsList);
                                }
                            }
                        }

                        listener.onRemindersLoaded(medicationsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }

    // Overloaded method that loads all medications without date filtering
    public void loadMedications(OnRemindersLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("User not logged in");
            return;
        }

        String userId = currentUser.getUid();

        // Load medications from the "medications" node in the database
        mDatabase.child("reminders").child(userId).child("medications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Map<String, Object>> medicationsList = new ArrayList<>();

                        // Iterate through all dates for the user
                        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                            // Iterate through all medication IDs under each date
                            for (DataSnapshot medSnapshot : dateSnapshot.getChildren()) {
                                // Process the medication details
                                String name = medSnapshot.child("name").getValue(String.class);
                                String dosage = medSnapshot.child("dosage").getValue(String.class);
                                String frequency = medSnapshot.child("frequency").getValue(String.class);
                                String sideEffects = medSnapshot.child("sideEffects").getValue(String.class);
                                String foodRelation = medSnapshot.child("foodRelation").getValue(String.class);
                                String duration = medSnapshot.child("duration").getValue(String.class);  // Duration
                                String durationUnit = medSnapshot.child("durationUnit").getValue(String.class);  // Duration unit
                                Long id = Long.parseLong(medSnapshot.getKey()); // Use the key as the ID

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

                        listener.onRemindersLoaded(medicationsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }

    // Helper method to apply the frequency and duration filter
    private boolean shouldMedicationAppearOnDate(Medication medication, LocalDate selectedDate, LocalDate medicationStartDate) {
        // Get medication date and frequency
        Log.d("filter", "getting date ");

        String frequency = medication.getFrequency();
        int duration = Integer.parseInt(medication.getDuration());
        String durationUnit = medication.getDurationUnit();

        // Adjust the duration based on the unit
        if (durationUnit.equals("weeks")) {
            duration = duration * 7;  // Convert weeks to days
        } else if (durationUnit.equals("months")) {
            duration = duration * 30;  // Approximate months to days
        }

        // Calculate the end date based on adjusted duration
        LocalDate medicationEndDate = medicationStartDate.plusDays(duration - 1);

        // Check if the selected date is within the medication's active period
        boolean isWithinDuration = !selectedDate.isBefore(medicationStartDate) && !selectedDate.isAfter(medicationEndDate);

        // If the date is outside the duration range, return false
        if (!isWithinDuration) {
            return false;
        }

        // Check frequency conditions
        boolean isValidFrequency = false;
        Log.d("filter", "at fre and dura " + medicationStartDate);

        switch (frequency) {
            case "Once daily":
            case "Twice daily":
            case "Three times daily":
            case "Four times daily":
            case "Every morning":
            case "Every night":
                // These frequencies apply daily, so just check the date range
                isValidFrequency = true;
                break;

            case "Every other day":
                // If the frequency is "Every other day", check if the difference between selectedDate and medicationStartDate is odd or even
                long daysBetween = medicationStartDate.until(selectedDate, ChronoUnit.DAYS);
                if (daysBetween >= 0 && daysBetween % 2 == 0) {
                    isValidFrequency = true;
                }
                break;

            case "Weekly":
                // If the frequency is "Weekly", check if the difference between selectedDate and medicationStartDate is a multiple of 7
                long daysBetweenWeekly = medicationStartDate.until(selectedDate, ChronoUnit.DAYS);
                if (daysBetweenWeekly >= 0 && daysBetweenWeekly % 7 == 0) {
                    isValidFrequency = true;
                }
                break;

            default:
                // If the frequency is unknown, don't show the medication
                isValidFrequency = false;
                break;
        }

        // Return true if the date is within duration range and the frequency is valid
        return isWithinDuration && isValidFrequency;
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
        mDatabase.child("reminders").child(userId).child("medications") // Adjusted to reflect new path
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AtomicBoolean medicationDeleted = new AtomicBoolean(false);

                        // Iterate through all medications for the user
                        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
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

        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
            // Iterate through all medication IDs under each date in "medications"
            for (DataSnapshot medSnapshot : dateSnapshot.getChildren()) {
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
        }

        return medicationsList;
    }


}
