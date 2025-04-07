package com.example.medassist.ui.appointment;

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

public class AppointmentRepository extends ReminderRepository {

    public AppointmentRepository(Context context) {
        super(context);
    }

    public void saveAppointment(
            String clinicName,
            String location,
            String appointmentStart,
            String appointmentEnd,
            String frequency,
            String repeatAmount,
            String repeatUnit,
            String description,
            String dateStr,
            OnOperationCompleteListener listener
    ) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError("User not logged in");
            return;
        }

        String userId = currentUser.getUid();
        String appointmentId = mDatabase.child("reminders")
                .child(userId)
                .child("appointments")
                .push()
                .getKey();  // No longer tied to date

        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("clinicName", clinicName);
        appointmentData.put("location", location);
        appointmentData.put("appointmentStart", appointmentStart);
        appointmentData.put("appointmentEnd", appointmentEnd);
        appointmentData.put("frequency", frequency);
        appointmentData.put("repeatAmount", repeatAmount);
        appointmentData.put("repeatUnit", repeatUnit);
        appointmentData.put("description", description);
        appointmentData.put("date", dateStr);  // Still useful for querying/filtering

        // Use fixed path: reminders > userId > appointments > appointmentId
        mDatabase.child("reminders")
                .child(userId)
                .child("appointments")
                .child(appointmentId)
                .setValue(appointmentData)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }


    public void loadAppointments(OnRemindersLoadedListener listener) {
        Log.d("die","di22222222222e");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("User not logged in");
            return;
        }

        String userId = currentUser.getUid();

        mDatabase.child("reminders").child(userId).child("appointments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Map<String, Object>> appointmentList = new ArrayList<>();

                        for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                            String clinicName = appointmentSnapshot.child("clinicName").getValue(String.class);
                            String location = appointmentSnapshot.child("location").getValue(String.class);
                            String appointmentStart = appointmentSnapshot.child("appointmentStart").getValue(String.class);
                            String appointmentEnd = appointmentSnapshot.child("appointmentEnd").getValue(String.class);
                            String frequency = appointmentSnapshot.child("frequency").getValue(String.class);
                            String repeatAmountAndUnit = appointmentSnapshot.child("repeatAmount").getValue(String.class)+appointmentSnapshot.child("repeatUnit").getValue(String.class);
                            String description = appointmentSnapshot.child("description").getValue(String.class);
                            String date = appointmentSnapshot.child("date").getValue(String.class);
                            String id = appointmentSnapshot.getKey(); // Use the key as the ID
                            Log.d("die","di12121212e");
                            if (clinicName != null && appointmentStart != null && appointmentEnd != null && date != null) {
                                Log.d("die","dies2s2s2s2s2s2");
                                Appointment appointment = new Appointment(
                                        id,
                                        clinicName,
                                        location,
                                        appointmentStart,
                                        appointmentEnd,
                                        frequency,
                                        repeatAmountAndUnit,
                                        description,
                                        date
                                );
                                Log.d("die","diec2c2c2c2c2");
                                Map<String, Object> appointmentData = new HashMap<>();
                                Log.d("die","dix3333333e");
                                appointmentData.put("appointment", appointment);
                                Log.d("die","die1231231123123");
                                appointmentList.add(appointmentData);
                                Log.d("die","die33333333333333333");
                            }
                        }

                        listener.onRemindersLoaded(appointmentList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }
    public void loadAppointments(String selectedDate, OnRemindersLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onError("User not logged in");
            return;
        }

        String userId = currentUser.getUid();

        mDatabase.child("reminders").child(userId).child("appointments")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Map<String, Object>> filteredAppointments = new ArrayList<>();

                        for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                            String clinicName = appointmentSnapshot.child("clinicName").getValue(String.class);
                            String location = appointmentSnapshot.child("location").getValue(String.class);
                            String appointmentStart = appointmentSnapshot.child("appointmentStart").getValue(String.class);
                            String appointmentEnd = appointmentSnapshot.child("appointmentEnd").getValue(String.class);
                            String frequency = appointmentSnapshot.child("frequency").getValue(String.class);
                            String repeatAmount = appointmentSnapshot.child("repeatAmount").getValue(String.class);
                            String repeatUnit = appointmentSnapshot.child("repeatUnit").getValue(String.class);
                            String description = appointmentSnapshot.child("description").getValue(String.class);
                            String dateStr = appointmentSnapshot.child("date").getValue(String.class);
                            String id = appointmentSnapshot.getKey();

                            if (clinicName == null || appointmentStart == null || appointmentEnd == null || dateStr == null) continue;

                            // Match selectedDate
                            if (isDateInRange(selectedDate, dateStr, frequency, repeatAmount, repeatUnit)) {
                                Appointment appointment = new Appointment(
                                        id,
                                        clinicName,
                                        location,
                                        appointmentStart,
                                        appointmentEnd,
                                        frequency,
                                        repeatAmount + repeatUnit,
                                        description,
                                        dateStr
                                );

                                Map<String, Object> appointmentMap = new HashMap<>();
                                appointmentMap.put("appointment", appointment);
                                filteredAppointments.add(appointmentMap);
                            }
                        }

                        listener.onRemindersLoaded(filteredAppointments);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
    }

    private boolean isDateInRange(String selectedDateStr, String startDateStr, String frequency, String repeatAmount, String repeatUnit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate selectedDate = LocalDate.parse(selectedDateStr, formatter);
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);

        if ("N.A".equals(frequency)) {
            return selectedDate.equals(startDate);
        }

        int amount = 0;
        try {
            amount = Integer.parseInt(repeatAmount);
        } catch (NumberFormatException e) {
            return false;
        }

        LocalDate endDate = startDate;  // default fallback

        switch (repeatUnit.toLowerCase()) {
            case "days":
                endDate = startDate.plusDays(amount);
                break;
            case "weeks":
                endDate = startDate.plusWeeks(amount);
                break;
            case "months":
                endDate = startDate.plusMonths(amount);
                break;
        }


        if (selectedDate.isBefore(startDate) || selectedDate.isAfter(endDate)) return false;

        long daysBetween = ChronoUnit.DAYS.between(startDate, selectedDate);

        switch (frequency) {
            case "Daily":
                return true;
            case "Weekly":
                return daysBetween % 7 == 0;
            case "Biweekly":
                return daysBetween % 14 == 0;
            case "Monthly":
                return selectedDate.getDayOfMonth() == startDate.getDayOfMonth();
            case "Bimonthly":
                return selectedDate.getDayOfMonth() == startDate.getDayOfMonth() &&
                        ChronoUnit.MONTHS.between(startDate, selectedDate) % 2 == 0;
            case "Every 4 Months":
                return selectedDate.getDayOfMonth() == startDate.getDayOfMonth() &&
                        ChronoUnit.MONTHS.between(startDate, selectedDate) % 4 == 0;
            case "Every 6 Months":
                return selectedDate.getDayOfMonth() == startDate.getDayOfMonth() &&
                        ChronoUnit.MONTHS.between(startDate, selectedDate) % 6 == 0;
            case "Annually":
                return selectedDate.getDayOfMonth() == startDate.getDayOfMonth() &&
                        selectedDate.getMonth() == startDate.getMonth();
            default:
                return false;
        }

    }





    @Override
    protected List<Map<String, Object>> processDataSnapshot(com.google.firebase.database.DataSnapshot dataSnapshot) {
        // Implement if needed to retrieve appointment data
        return new ArrayList<>();
    }
}
