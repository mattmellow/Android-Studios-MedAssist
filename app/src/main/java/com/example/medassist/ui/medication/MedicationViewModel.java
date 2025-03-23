package com.example.medassist.ui.medication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MedicationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MedicationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is medication fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}