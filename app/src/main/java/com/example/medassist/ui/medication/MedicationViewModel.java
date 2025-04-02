package com.example.medassist.ui.medication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicationViewModel extends ViewModel {
    // LiveData for UI
    private final MutableLiveData<List<Medication>> medications = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<LocalDate> selectedDate = new MutableLiveData<>(LocalDate.now());

    // Firebase references
    private final DatabaseReference databaseReference;
    private final FirebaseUser currentUser;

    public MedicationViewModel() {
        // Initialize Firebase
        FirebaseDatabase database = Firebase.database.getInstance();
        databaseReference = database.getReference("medications");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initiliaze with empty list
        medications.setValue(new ArrayList<>());

        // Load medications when selected date changes
        selectedDate.observeForever(this::loadMedicationsForDate);
    }

//    public LiveData<List<Medication>> getMedications() {
//        return medications;
//    }

    public void addMedication(Medication medication) {
        // Adding to local LiveData
        List<Medication> currentList = medications.getValue();
        if (currentList != null) {
            currentList.add(medication);
            medications.setValue(currentList);
        }

        // Saving to Firebase
        if (currentUser != null) {
            String dateKey = medication.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            Map<String, Object> medMap = new HashMap<>();
            medMap.put("medicationId", medication.getMedicationId());
            medMap.put("medicationName", medication.getMedicationName());

        }
    }

    public void removeMedication(long id) {
        List<Medication> currentList = medications.getValue();
        if (currentList != null) {
            currentList.removeIf(med -> med.id == id);
            medications.setValue(currentList);
        }
    }

    public LiveData<LocalDate> getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate date) {
        selectedDate.setValue(date);
    }

    public void clearMedications() {
        medications.setValue(new ArrayList<>());
    }
}