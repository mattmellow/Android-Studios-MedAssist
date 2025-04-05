package com.example.medassist.ui.reminders;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class to handle Firebase operations for reminders
 */
public abstract class ReminderRepository {
    private static final String DB_URL = "https://medassist-fdddd-default-rtdb.asia-southeast1.firebasedatabase.app";
    protected final DatabaseReference mDatabase;
    protected final FirebaseAuth mAuth;
    protected final Context context;

    public ReminderRepository(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();
    }

    // Save reminder to Firebase - common logic
    protected void saveReminder(Map<String, Object> reminderData, String userId, String dateStr, String reminderId, OnOperationCompleteListener listener) {
        if (userId == null) {
            listener.onError("Please log in to save reminder");
            return;
        }

        mDatabase.child("reminders").child(userId).child(dateStr).child(reminderId).setValue(reminderData)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    // Load all reminders (no filtering by date)
    protected void loadAllReminders(String userId, OnRemindersLoadedListener listener) {
        if (userId == null) {
            listener.onError("User not logged in");
            return;
        }

        mDatabase.child("reminders").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Map<String, Object>> remindersList = processDataSnapshot(dataSnapshot);
                        listener.onRemindersLoaded(remindersList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }

    // Abstract method to be implemented by child classes for processing the data
    protected abstract List<Map<String, Object>> processDataSnapshot(DataSnapshot dataSnapshot);

    // Delete reminder from Firebase - common logic
    protected void deleteReminder(String userId, String dateStr, String reminderId, OnOperationCompleteListener listener) {
        if (userId == null) {
            listener.onError("User not logged in");
            return;
        }

        mDatabase.child("reminders").child(userId).child(dateStr).child(reminderId).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onError(String errorMessage);
    }

    public interface OnRemindersLoadedListener {
        void onRemindersLoaded(List<Map<String, Object>> reminders);
        void onError(String errorMessage);
    }
}
