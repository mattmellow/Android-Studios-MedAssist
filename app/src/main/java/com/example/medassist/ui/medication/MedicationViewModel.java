package com.example.medassist.ui.medication;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicationViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Medication>> medications;
    private final MutableLiveData<LocalDate> selectedDate;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;
    private final MedicationRepository repository;

    public MedicationViewModel(@NonNull Application application) {
        super(application);
        medications = new MutableLiveData<>(new ArrayList<>());
        selectedDate = new MutableLiveData<>(LocalDate.now());
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
        repository = new MedicationRepository(application.getApplicationContext());
    }

    public LiveData<List<Medication>> getMedications() {
        return medications;
    }

    public LiveData<LocalDate> getSelectedDate() {
        return selectedDate;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setSelectedDate(LocalDate date) {
        selectedDate.setValue(date);
        loadMedicationsForDate(date);
    }

    public void addMedication(Medication medication) {
        isLoading.setValue(true);

        repository.saveMedication(medication, new MedicationRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                // The medication will be loaded through the ValueEventListener in the repository
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    public void removeMedication(Medication medication) {
        isLoading.setValue(true);

        repository.deleteMedication(medication, new MedicationRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                // The medication will be removed through the ValueEventListener in the repository
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    private void loadMedicationsForDate(LocalDate date) {
        isLoading.setValue(true);

        repository.loadMedicationsForDate(date, new MedicationRepository.OnMedicationsLoadedListener() {
            @Override
            public void onMedicationsLoaded(List<Medication> loadedMedications) {
                medications.setValue(loadedMedications);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }
}