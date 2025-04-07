package com.example.medassist.ui.allappointments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AllAppointmentsViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public AllAppointmentsViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AllAppointmentsViewModel(context);
    }
}
