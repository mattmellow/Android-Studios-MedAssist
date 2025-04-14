package com.example.medassist.ui.reminders;

import android.content.Context;
import android.util.Log;

import com.example.medassist.ui.appointment.Appointment;
import com.example.medassist.ui.appointment.AppointmentNotificationHelper;
import com.example.medassist.ui.medication.Medication;
import com.example.medassist.ui.medication.NotificationHelper;

import java.util.List;
import java.util.Map;

/**
 * Central manager for all app notification scheduling
 */
public class NotificationManager {
    private static final String TAG = "NotificationManager";

    /**
     * Schedule all reminder notifications for medications
     * @param context The application context
     * @param medications List of medications to schedule notifications for
     */
    public static void scheduleMedicationReminders(Context context, List<Map<String, Object>> medications) {
        if (medications == null || medications.isEmpty()) {
            Log.d(TAG, "No medications to schedule");
            return;
        }

        Log.d(TAG, "Scheduling notifications for " + medications.size() + " medications");

        for (Map<String, Object> medData : medications) {
            if (medData.containsKey("medication")) {
                Medication medication = (Medication) medData.get("medication");
                if (medication != null && medication.getNotificationTimes() != null && !medication.getNotificationTimes().isEmpty()) {
                    Log.d(TAG, "Scheduling notifications for medication: " + medication.getName());
                    NotificationHelper.scheduleMedicationReminder(context, medication);
                }
            }
        }
    }

    /**
     * Schedule reminder notification for a single medication
     * @param context The application context
     * @param medication The medication to schedule
     */
    public static void scheduleMedicationReminder(Context context, Medication medication) {
        if (medication == null || medication.getNotificationTimes() == null || medication.getNotificationTimes().isEmpty()) {
            Log.d(TAG, "No notification times for medication: " +
                    (medication != null ? medication.getName() : "null"));
            return;
        }

        Log.d(TAG, "Scheduling notifications for medication: " + medication.getName());
        NotificationHelper.scheduleMedicationReminder(context, medication);
    }

    /**
     * Cancel all reminder notifications for a medication
     * @param context The application context
     * @param medicationId ID of the medication
     */
    public static void cancelMedicationReminder(Context context, String medicationId) {
        if (medicationId == null || medicationId.isEmpty()) {
            return;
        }

        NotificationHelper.cancelMedicationReminder(context, medicationId);
    }

    /**
     * Schedule all reminder notifications for appointments
     * @param context The application context
     * @param appointments List of appointments to schedule notifications for
     */
    public static void scheduleAppointmentReminders(Context context, List<Map<String, Object>> appointments) {
        if (appointments == null || appointments.isEmpty()) {
            Log.d(TAG, "No appointments to schedule");
            return;
        }

        Log.d(TAG, "Scheduling notifications for " + appointments.size() + " appointments");

        for (Map<String, Object> appointmentData : appointments) {
            if (appointmentData.containsKey("appointment")) {
                Appointment appointment = (Appointment) appointmentData.get("appointment");
                if (appointment != null) {
                    Log.d(TAG, "Scheduling notification for appointment: " + appointment.getClinicName());
                    AppointmentNotificationHelper.scheduleAppointmentReminder(context, appointment);
                }
            }
        }
    }

    /**
     * Schedule reminder notification for a single appointment
     * @param context The application context
     * @param appointment The appointment to schedule
     */
    public static void scheduleAppointmentReminder(Context context, Appointment appointment) {
        if (appointment == null) {
            return;
        }

        Log.d(TAG, "Scheduling notification for appointment: " + appointment.getClinicName());
        AppointmentNotificationHelper.scheduleAppointmentReminder(context, appointment);
    }

    /**
     * Cancel a scheduled appointment reminder
     * @param context The application context
     * @param appointmentId ID of the appointment
     */
    public static void cancelAppointmentReminder(Context context, String appointmentId) {
        if (appointmentId == null || appointmentId.isEmpty()) {
            return;
        }

        AppointmentNotificationHelper.cancelAppointmentReminder(context, appointmentId);
    }
}