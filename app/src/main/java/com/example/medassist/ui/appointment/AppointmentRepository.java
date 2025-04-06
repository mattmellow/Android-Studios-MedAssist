package com.example.medassist.ui.appointment;

import android.content.Context;

import com.example.medassist.ui.reminders.ReminderRepository;
import com.google.firebase.auth.FirebaseUser;

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
                .getKey();  // ✅ No longer tied to date

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

        // ✅ Use fixed path: reminders > userId > appointments > appointmentId
        mDatabase.child("reminders")
                .child(userId)
                .child("appointments")
                .child(appointmentId)
                .setValue(appointmentData)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }


    @Override
    protected List<Map<String, Object>> processDataSnapshot(com.google.firebase.database.DataSnapshot dataSnapshot) {
        // Implement if needed to retrieve appointment data
        return new ArrayList<>();
    }
}
