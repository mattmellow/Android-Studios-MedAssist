package com.example.medassist.ui.medication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class NotificationHelper {

    /**
     * Schedule notification for a medication
     *
     * @param context Context
     * @param medication Medication to schedule notification for
     */
    public static void scheduleMedicationReminder(Context context, Medication medication) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        try {
            // Parse the time string from medication (assuming format like "08:00 AM")
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime medicationTime = LocalTime.parse(medication.getTime(), timeFormatter);

            // Combine with the medication date
            LocalDate medicationDate = medication.getDate();

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

            // Create intent for the alarm
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.MEDICATION_NAME, medication.getName());
            intent.putExtra(AlarmReceiver.MEDICATION_DOSAGE, medication.getDosage());
            intent.putExtra("notificationId", (int) medication.getId());

            // Create PendingIntent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (int) medication.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Set alarm
            if (alarmManager != null) {
                // For devices running Android 6.0 (API 23) and above, we need to consider Doze mode
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

    /**
     * Cancel scheduled notification for a medication
     *
     * @param context Context
     * @param medicationId ID of the medication
     */
    public static void cancelMedicationReminder(Context context, long medicationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) medicationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}