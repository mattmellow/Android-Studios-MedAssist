package com.example.medassist.ui.medication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.medassist.MainActivity;
import com.example.medassist.R;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static final String CHANNEL_ID = "medication_channel";
    public static final String MEDICATION_NAME = "medication_name";
    public static final String MEDICATION_DOSAGE = "medication_dosage";
    public static final String MEDICATION_TIME_INDEX = "medication_time_index";

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicationName = intent.getStringExtra(MEDICATION_NAME);
        String medicationDosage = intent.getStringExtra(MEDICATION_DOSAGE);
        int timeIndex = intent.getIntExtra(MEDICATION_TIME_INDEX, 0);
        int notificationId = intent.getIntExtra("notificationId", 0);

        Log.d(TAG, "Received alarm for " + medicationName + ", dose " + (timeIndex + 1));

        createNotificationChannel(context);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                mainIntent,
                PendingIntent.FLAG_IMMUTABLE);

        String timeDescription = getTimeDescription(timeIndex);

        //UI for the notifs
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_circle_notifications)
                .setContentTitle("Medication Reminder")
                .setContentText("Time to take " + medicationName + " (" + medicationDosage + ") " + timeDescription)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Notification shown for " + medicationName + ", dose " + (timeIndex + 1));
        }

        // Reschedule for tomorrow if this is a daily medication
        // This is necessary because we're using exact alarms which don't repeat automatically
        rescheduleAlarmIfNeeded(context, intent);
    }

    private void rescheduleAlarmIfNeeded(Context context, Intent originalIntent) {
        String action = originalIntent.getAction();
        if (action != null && action.startsWith("com.example.medassist.MEDICATION_REMINDER_")) {
            String[] parts = action.split("_");
            if (parts.length >= 4) {
                try {
                    String medicationId = parts[3];
                    if (parts.length > 5) {
                        // Reconstruct the ID with any additional parts
                        StringBuilder idBuilder = new StringBuilder(parts[3]);
                        for (int i = 4; i < parts.length-1; i++) {
                            idBuilder.append("_").append(parts[i]);
                        }
                        medicationId = idBuilder.toString();
                    }

                    Intent newIntent = new Intent(context, AlarmReceiver.class);
                    newIntent.setAction(originalIntent.getAction());
                    newIntent.putExtra(MEDICATION_NAME, originalIntent.getStringExtra(MEDICATION_NAME));
                    newIntent.putExtra(MEDICATION_DOSAGE, originalIntent.getStringExtra(MEDICATION_DOSAGE));
                    newIntent.putExtra(MEDICATION_TIME_INDEX, originalIntent.getIntExtra(MEDICATION_TIME_INDEX, 0));
                    newIntent.putExtra("notificationId", originalIntent.getIntExtra("notificationId", 0));


                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, 1); // Add one day


                    int timeIndex = originalIntent.getIntExtra(MEDICATION_TIME_INDEX, 0);
                    int notificationId = originalIntent.getIntExtra("notificationId", 0);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            context,
                            notificationId,
                            newIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    //necessary for Andriod o (API 26) and above
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                        Log.d(TAG, "Rescheduled alarm for tomorrow for " +
                                originalIntent.getStringExtra(MEDICATION_NAME) + ", dose " + (timeIndex + 1));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error rescheduling notification", e);
                }
            }
        }
    }

    private String getTimeDescription(int timeIndex) {
        return "for dose #" + (timeIndex + 1);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medication Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for medication reminders");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}