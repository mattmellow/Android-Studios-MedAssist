package com.example.medassist.ui.appointment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.medassist.databinding.FragmentAppointmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import com.example.medassist.databinding.FragmentAppointmentBinding;

public class AppointmentFragment extends Fragment {

    private FragmentAppointmentBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://medassist-fdddd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            binding.saveResult.setText("Please sign in to book an appointment.");
            binding.confirmButton.setEnabled(false);
        } else {
            binding.confirmButton.setEnabled(true);
        }

        binding.calendarView.setOnDateChangeListener(new android.widget.CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull android.widget.CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                binding.selectedDate.setText("Selected Date: " + selectedDate);
                binding.saveResult.setText("");
            }
        });

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointmentToFirebase();
            }
        });

        return view;
    }

    private void saveAppointmentToFirebase() {
        String selectedDate = binding.selectedDate.getText().toString();
        if (selectedDate.equals("Selected Date: Not Set")) {
            binding.saveResult.setText("Please select a date before confirming.");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.confirmButton.setEnabled(false);
        binding.saveResult.setText("");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            binding.progressBar.setVisibility(View.GONE);
            binding.confirmButton.setEnabled(true);
            binding.saveResult.setText("Error: User not authenticated.");
            return;
        }

        String userId = currentUser.getUid();
        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("date", selectedDate.replace("Selected Date: ", "")); // Remove the prefix
        appointmentData.put("timestamp", System.currentTimeMillis());
        appointmentData.put("status", "Confirmed");


        String appointmentId = mDatabase.child("appointments").child(userId).push().getKey();
        mDatabase.child("appointments").child(userId).child(appointmentId).setValue(appointmentData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully saved
                        binding.progressBar.setVisibility(View.GONE);
                        binding.confirmButton.setEnabled(true);
                        binding.saveResult.setText("Appointment saved successfully!");
                        // Optionally reset the selected date
                        binding.selectedDate.setText("Selected Date: Not Set");
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to save
                        binding.progressBar.setVisibility(View.GONE);
                        binding.confirmButton.setEnabled(true);
                        binding.saveResult.setText("Failed to save appointment: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}