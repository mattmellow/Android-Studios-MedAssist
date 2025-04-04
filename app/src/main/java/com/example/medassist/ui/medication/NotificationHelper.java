// 1. First, update the NotificationHelper.java to handle multiple notification times

package com.example.medassist.ui.medication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class NotificationHelper {

    /**
     * Schedule notifications for a medication with multiple time slots
     *
     * @param context Context
     * @param medication Medication to schedule notifications for
     */
    public static void scheduleMedicationReminder(Context context, Medication medication) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Get medication details
        String name = medication.getName();
        String dosage = medication.getDosage();
        List<String> times = medication.getNotificationTimes();

        // For each notification time
        for (int i = 0; i < times.size(); i++) {
            try {
                // Parse time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                LocalTime time = LocalTime.parse(times.get(i), formatter);

                // Create calendar for alarm
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, time.getHour());
                calendar.set(Calendar.MINUTE, time.getMinute());
                calendar.set(Calendar.SECOND, 0);

                // If time has already passed today, schedule for tomorrow
                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }

                // Create intent for alarm
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra(AlarmReceiver.MEDICATION_NAME, name);
                intent.putExtra(AlarmReceiver.MEDICATION_DOSAGE, dosage);
                intent.putExtra(AlarmReceiver.MEDICATION_TIME_INDEX, i);

                // Generate unique notification ID based on medication ID and time index
                int notificationId = (int) (medication.getId() + i);
                intent.putExtra("notificationId", notificationId);

                // Create pending intent
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        notificationId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                // Schedule appropriate alarm based on frequency
                int intervalType = getIntervalTypeForFrequency(medication.getFrequency());

                // For repeating alarms
                if (intervalType > 0) {
                    alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            intervalType,
                            pendingIntent);
                }
                // For one-time alarms (like "As needed")
                else {
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int getIntervalTypeForFrequency(String frequency) {
        switch (frequency) {
            case "Once daily":
            case "Twice daily":
            case "Three times daily":
            case "Four times daily":
            case "Every morning":
            case "Every night":
                return (int) AlarmManager.INTERVAL_DAY;

            case "Every other day":
                return (int) (AlarmManager.INTERVAL_DAY * 2);

            case "Weekly":
                return (int) (AlarmManager.INTERVAL_DAY * 7);

            case "As needed":
            default:
                return 0; // No repeating
        }
    }

    /**
     * Cancel all scheduled notifications for a medication
     *
     * @param context Context
     * @param medicationId ID of the medication
     */
    public static void cancelMedicationReminder(Context context, long medicationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel all possible time slots (up to 10 should be more than enough)
        for (int i = 0; i < 10; i++) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            int notificationId = (int) (medicationId + i); // Match the ID generation in scheduleMedicationReminder

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }
}