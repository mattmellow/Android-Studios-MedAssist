package com.example.medassist.ui.medication;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.medassist.ui.reminders.ReminderRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Log.d("filter","i am in viewmodel with date: " + date);
        loadMedications(date);  // Call the new loadMedications method with LocalDate
    }

    public void addMedication(Medication medication) {
        isLoading.setValue(true);

        repository.saveMedication(medication, new MedicationRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                //NotificationHelper.scheduleMedicationReminder(getApplication(), medication);
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
                //NotificationHelper.cancelMedicationReminder(getApplication(), medication.getId());
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    public void updateMedication(Medication medication) {
        isLoading.setValue(true);

        repository.saveMedication(medication, new MedicationRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                //NotificationHelper.cancelMedicationReminder(getApplication(), medication.getId());
                NotificationHelper.scheduleMedicationReminder(getApplication(), medication);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    // using the one that filters by date also
    public void loadMedications(LocalDate selectedDate) {
        isLoading.setValue(true);

        repository.loadMedications(new ReminderRepository.OnRemindersLoadedListener() {
            @Override
            public void onRemindersLoaded(List<Map<String, Object>> loadedReminders) {
                List<Medication> loadedMedications = new ArrayList<>();
                for (Map<String, Object> reminderData : loadedReminders) {
                    Medication medication = (Medication) reminderData.get("medication");
                    loadedMedications.add(medication);
                }
                medications.setValue(loadedMedications);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        }, selectedDate);
    }
}
