package com.example.medassist.ui.medication;

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

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static final String MEDICATION_NAME = "medication_name";
    public static final String MEDICATION_DOSAGE = "medication_dosage";
    public static final String MEDICATION_TIME_INDEX = "medication_time_index";

    public static final String APPOINTMENT_TITLE = "appointment_title";
    public static final String APPOINTMENT_LOCATION = "appointment_location";
    public static final String APPOINTMENT_TIME = "appointment_time";
    public static final String NOTIFICATION_TYPE = "notification_type";

    public static final String TYPE_MEDICATION = "medication";
    public static final String TYPE_APPOINTMENT = "appointment";

    private static final String MEDICATION_CHANNEL_ID = "medication_notifications";
    private static final String APPOINTMENT_CHANNEL_ID = "appointment_notifications";

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(NOTIFICATION_TYPE);
        int notificationId = intent.getIntExtra("notificationId", 1);

        if (TYPE_MEDICATION.equals(type)) {
            handleMedicationNotification(context, intent, notificationId);
        } else if (TYPE_APPOINTMENT.equals(type)) {
            handleAppointmentNotification(context, intent, notificationId);
        }

        // If this is a repeating alarm that needs to be rescheduled because we're using setExactAndAllowWhileIdle
        String medicationId = intent.getStringExtra("medicationId");
        if (medicationId != null) {
            // Implement any rescheduling logic if needed for recurring medications
            // This would be used if you're using setExactAndAllowWhileIdle for precise alarms
        }
    }

    private void handleMedicationNotification(Context context, Intent intent, int notificationId) {
        String medicationName = intent.getStringExtra(MEDICATION_NAME);
        String dosage = intent.getStringExtra(MEDICATION_DOSAGE);
        int timeIndex = intent.getIntExtra(MEDICATION_TIME_INDEX, 0);

        Log.d(TAG, "Showing medication notification: " + medicationName + ", Dose #" + (timeIndex + 1));

        String title = "Time to take your medication";
        String message = medicationName + " - " + dosage;

        // Create a notification channel for Android O and higher
        createNotificationChannel(context, MEDICATION_CHANNEL_ID, "Medication Reminders", "Notifications for medication reminders");

        // Create an intent to open the app when notification is tapped
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.putExtra("openMedication", true);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build and show the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MEDICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }

    private void handleAppointmentNotification(Context context, Intent intent, int notificationId) {
        String appointmentTitle = intent.getStringExtra(APPOINTMENT_TITLE);
        String location = intent.getStringExtra(APPOINTMENT_LOCATION);
        String time = intent.getStringExtra(APPOINTMENT_TIME);

        Log.d(TAG, "Showing appointment notification: " + appointmentTitle + " at " + time);

        String title = "Upcoming Appointment";
        String message = appointmentTitle + " at " + time;
        if (location != null && !location.isEmpty()) {
            message += " - " + location;
        }

        // Create a notification channel for Android O and higher
        createNotificationChannel(context, APPOINTMENT_CHANNEL_ID, "Appointment Reminders", "Notifications for upcoming appointments");

        // Create an intent to open the app when notification is tapped
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.putExtra("openAppointment", true);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build and show the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, APPOINTMENT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(Context context, String channelId, String name, String description) {
        // Create the notification channel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}