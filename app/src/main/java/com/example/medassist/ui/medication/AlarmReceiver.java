package com.example.medassist.ui.medication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.medassist.MainActivity;
import com.example.medassist.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "medication_channel";
    public static final String MEDICATION_NAME = "medication_name";
    public static final String MEDICATION_DOSAGE = "medication_dosage";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get medication info from intent
        String medicationName = intent.getStringExtra(MEDICATION_NAME);
        String medicationDosage = intent.getStringExtra(MEDICATION_DOSAGE);
        int notificationId = intent.getIntExtra("notificationId", 0);

        // Create notification channel for Android O and above
        createNotificationChannel(context);

        // Create intent to open app when notification is tapped
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                mainIntent,
                PendingIntent.FLAG_IMMUTABLE);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_circle_notifications)
                .setContentTitle("Medication Reminder")
                .setContentText("Time to take " + medicationName + " (" + medicationDosage + ")")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
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