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
        List<String> notificationTimes = medication.getNotificationTimes();

        if (notificationTimes == null || notificationTimes.isEmpty()) {
            return;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDate medicationDate = medication.getDate();

        // Schedule a notification for each time
        for (int i = 0; i < notificationTimes.size(); i++) {
            try {
                String timeStr = notificationTimes.get(i);
                LocalTime medicationTime = LocalTime.parse(timeStr, timeFormatter);

                Calendar calendar = Calendar.getInstance();
                // Use the medication date
                calendar.set(Calendar.YEAR, medicationDate.getYear());
                calendar.set(Calendar.MONTH, medicationDate.getMonthValue() - 1); // Calendar months are 0-based
                calendar.set(Calendar.DAY_OF_MONTH, medicationDate.getDayOfMonth());
                calendar.set(Calendar.HOUR_OF_DAY, medicationTime.getHour());
                calendar.set(Calendar.MINUTE, medicationTime.getMinute());
                calendar.set(Calendar.SECOND, 0);

                // If time is in the past today, schedule for tomorrow
                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }

                // Create intent for the alarm with unique ID for each time slot
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra(AlarmReceiver.MEDICATION_NAME, medication.getName());
                intent.putExtra(AlarmReceiver.MEDICATION_DOSAGE, medication.getDosage());
                intent.putExtra(AlarmReceiver.MEDICATION_TIME_INDEX, i);

                // Create unique notification ID by combining medication ID with time index
                int notificationId = (int) medication.getId() * 100 + i;
                intent.putExtra("notificationId", notificationId);

                // Create PendingIntent
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        notificationId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                // Set alarm
                if (alarmManager != null) {
                    // For devices running Android 6.0 (API 23) and above
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent);
                    } else {
                        alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            int notificationId = (int) medicationId * 100 + i;

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