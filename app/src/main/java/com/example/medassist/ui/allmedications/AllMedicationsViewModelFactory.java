package com.example.medassist.ui.allmedications;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AllMedicationsViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public AllMedicationsViewModelFactory(Context context) {
        this.context = context;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AllMedicationsViewModel(context);
    }
}
