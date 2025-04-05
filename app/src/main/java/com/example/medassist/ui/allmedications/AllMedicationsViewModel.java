package com.example.medassist.ui.allmedications;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.medassist.ui.medication.Medication;
import com.example.medassist.ui.medication.MedicationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllMedicationsViewModel extends ViewModel {

    private final MutableLiveData<List<Medication>> medications;
    private final MedicationRepository repository;

    public AllMedicationsViewModel(Context context) {
        medications = new MutableLiveData<>(new ArrayList<>());
        repository = new MedicationRepository(context); // Initialize the repository with the context
    }

    public LiveData<List<Medication>> getMedications() {
        return medications;
    }

    // Load all medications
    public void loadMedications() {
        repository.loadMedications(new MedicationRepository.OnRemindersLoadedListener() {
            @Override
            public void onRemindersLoaded(List<Map<String, Object>> loadedReminders) {
                List<Medication> loadedMedications = new ArrayList<>();
                for (Map<String, Object> reminderData : loadedReminders) {
                    Medication medication = (Medication) reminderData.get("medication");
                    loadedMedications.add(medication);
                }
                medications.setValue(loadedMedications);  // Update the LiveData
            }

            @Override
            public void onError(String errorMsg) {
                // Handle error
            }
        });
    }

    // Remove medication from Firebase
    public void removeMedication(Medication medication) {
        repository.deleteMedication(medication, new MedicationRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                // Handle successful deletion (e.g., show a Toast message or update the list)
                // Medication should be removed from the LiveData list
                List<Medication> currentMedications = medications.getValue();
                if (currentMedications != null) {
                    currentMedications.remove(medication);
                    medications.setValue(currentMedications);  // Update the LiveData list
                }
            }

            @Override
            public void onError(String errorMsg) {
                // Handle error during deletion
            }
        });
    }
}
