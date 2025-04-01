package com.example.medassist.ui.medication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicationViewModel extends ViewModel {
    private final MutableLiveData<List<Medication>> medications;
    private final MutableLiveData<LocalDate> selectedDate;

    public MedicationViewModel() {
        medications = new MutableLiveData<>(new ArrayList<>());
        selectedDate = new MutableLiveData<>(LocalDate.now());
    }

    public LiveData<List<Medication>> getMedications() {
        return medications;
    }

    public void addMedication(Medication medication) {
        List<Medication> currentList = medications.getValue();
        if (currentList != null) {
            currentList.add(medication);
            medications.setValue(currentList);
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