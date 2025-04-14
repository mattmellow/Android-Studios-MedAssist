package com.example.medassist.ui.reminders;

import android.content.Context;
import android.util.Log;

import com.example.medassist.ui.appointment.Appointment;
import com.example.medassist.ui.appointment.AppointmentRepository;
import com.example.medassist.ui.medication.MedicationRepository;

import java.util.List;
import java.util.Map;

/**
 * Helper class to initialize all reminders and notifications when the app starts
 */
public class NotificationInitializer {
    private static final String TAG = "NotificationInitializer";

    /**
     * Initialize all reminders and schedule notifications
     * @param context Application context
     */
    public static void initializeAllReminders(Context context) {
        initializeMedicationReminders(context);
        initializeAppointmentReminders(context);
    }

    /**
     * Initialize medication reminders
     */
    private static void initializeMedicationReminders(Context context) {
        MedicationRepository medRepository = new MedicationRepository(context);

        medRepository.loadMedications(new ReminderRepository.OnRemindersLoadedListener() {
            @Override
            public void onRemindersLoaded(List<Map<String, Object>> reminders) {
                Log.d(TAG, "Loaded " + reminders.size() + " medications for notification scheduling");
                NotificationManager.scheduleMedicationReminders(context, reminders);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading medications: " + errorMessage);
            }
        });
    }

    /**
     * Initialize appointment reminders
     */
    private static void initializeAppointmentReminders(Context context) {
        AppointmentRepository appointmentRepository = new AppointmentRepository(context);

        appointmentRepository.loadAppointments(new ReminderRepository.OnRemindersLoadedListener() {
            @Override
            public void onRemindersLoaded(List<Map<String, Object>> reminders) {
                Log.d(TAG, "Loaded " + reminders.size() + " appointments for notification scheduling");
                NotificationManager.scheduleAppointmentReminders(context, reminders);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading appointments: " + errorMessage);
            }
        });
    }
}