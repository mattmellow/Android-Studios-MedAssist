package com.example.medassist.ui.allappointments;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.medassist.ui.appointment.Appointment;
import com.example.medassist.ui.appointment.AppointmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllAppointmentsViewModel extends ViewModel {

    private final MutableLiveData<List<Appointment>> appointments;
    private final AppointmentRepository repository;

    public AllAppointmentsViewModel(Context context) {
        this.appointments = new MutableLiveData<>(new ArrayList<>());
        this.repository = new AppointmentRepository(context);
    }

    public LiveData<List<Appointment>> getAppointments() {
        return appointments;
    }

    public void loadAppointments() {
        Log.d("die","die");
        repository.loadAppointments(new AppointmentRepository.OnRemindersLoadedListener() {
            @Override
            public void onRemindersLoaded(List<Map<String, Object>> loadedReminders) {
                Log.d("die","d123123123ie");
                List<Appointment> appointmentList = new ArrayList<>();
                for (Map<String, Object> map : loadedReminders) {
                    Log.d("die","d12312sssssssie");
                    Appointment appt = (Appointment) map.get("appointment");
                    if (appt != null) {
                        appointmentList.add(appt);
                    }
                }
                appointments.setValue(appointmentList);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error (log or UI message)
            }
        });
    }
}
