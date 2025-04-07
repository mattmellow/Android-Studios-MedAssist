package com.example.medassist.ui.medication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    /**
     * Schedule notifications for a medication with multiple time slots
     *
     * @param context Context
     * @param medication Medication to schedule notifications for
     */
    public static void scheduleMedicationReminder(Context context, Medication medication) {
        // First cancel any existing reminders to avoid duplicates
        cancelMedicationReminder(context, medication.getId());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }

        // Get medication details
        String name = medication.getName();
        String dosage = medication.getDosage();
        List<String> times = medication.getNotificationTimes();

        Log.d(TAG, "Scheduling " + times.size() + " reminders for " + name);

        // For each notification time
        for (int i = 0; i < times.size(); i++) {
            try {
                // Parse time string to LocalTime
                LocalTime time;
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                    time = LocalTime.parse(times.get(i), formatter);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing time: " + times.get(i), e);
                    continue;
                }

                Log.d(TAG, "Setting reminder " + (i+1) + " at " + time + " for " + name);

                // Create calendar for alarm
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, time.getHour());
                calendar.set(Calendar.MINUTE, time.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // If time has already passed today, schedule for tomorrow
                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }

                // For testing/debugging - set alarm to trigger soon
                // Uncomment this section to test notifications quickly
                /*
                Calendar testCalendar = Calendar.getInstance();
                testCalendar.add(Calendar.MINUTE, i + 1);  // Set to trigger in 1, 2, 3... minutes
                calendar = testCalendar;
                */

                // Create intent for alarm with UNIQUE action to prevent intent reuse
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.setAction("com.example.medassist.MEDICATION_REMINDER_" + medication.getId() + "_" + i);
                intent.putExtra(AlarmReceiver.MEDICATION_NAME, name);
                intent.putExtra(AlarmReceiver.MEDICATION_DOSAGE, dosage);
                intent.putExtra(AlarmReceiver.MEDICATION_TIME_INDEX, i);  // This is critical - it's the dose number

                // Generate consistent notification ID based on medication ID and time index
                int notificationId = generateNotificationId(medication.getId(), i);
                intent.putExtra("notificationId", notificationId);

                Log.d(TAG, "Notification ID for " + name + " dose " + (i+1) + ": " + notificationId);

                // Create pending intent with FLAG_UPDATE_CURRENT to ensure it's unique
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        notificationId,  // Using the same notificationId as the requestCode ensures uniqueness
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                // Schedule appropriate alarm based on frequency
                int intervalType = getIntervalTypeForFrequency(medication.getFrequency());

                // For repeating alarms
                if (intervalType > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                        // For Android 12+ if exact alarms permission not granted
                        alarmManager.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                intervalType,
                                pendingIntent);
                    } else {
                        // Set exact and allow while idle for reliability
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent);

                        // For repeating notifications, we'll reset them when one triggers
                        // This is handled in AlarmReceiver (will need to be added there)
                    }

                    Log.d(TAG, "Set repeating alarm for " + name + " dose " + (i+1) +
                            " at time " + time + " with interval " + intervalType);
                } else {
                    // For one-time alarms, always use exact and allow while idle
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent);

                    Log.d(TAG, "Set one-time alarm for " + name + " dose " + (i+1) +
                            " at time " + time);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error scheduling notification for " + name + " at index " + i, e);
            }
        }
    }

    /**
     * Generate a consistent notification ID
     *
     * @param medicationId Medication ID
     * @param timeIndex Time slot index
     * @return Unique notification ID
     */
    private static int generateNotificationId(String medicationId, int timeIndex) {
        return medicationId.hashCode() * 31 + timeIndex;
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
    public static void cancelMedicationReminder(Context context, String medicationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Log.d(TAG, "Cancelling all reminders for medication ID: " + medicationId);

        // Cancel all possible time slots (up to 10 should be more than enough)
        for (int i = 0; i < 10; i++) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction("com.example.medassist.MEDICATION_REMINDER_" + medicationId + "_" + i);
            int notificationId = generateNotificationId(medicationId, i);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Cancelled reminder with ID: " + notificationId);
        }
    }
}