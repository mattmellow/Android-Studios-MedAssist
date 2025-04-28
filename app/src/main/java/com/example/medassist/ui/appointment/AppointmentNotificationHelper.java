package com.example.medassist.ui.appointment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.medassist.ui.medication.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentNotificationHelper {
    private static final String TAG = "AppointmentNotificationHelper";

    // Time in minutes before appointment to show notification
    private static final int NOTIFICATION_LEAD_TIME = 60; // Default: 1 hour before

    /**
     * Schedule a notification for an appointment
     *
     * @param context Context
     * @param appointment Appointment to schedule notification for
     */
    public static void scheduleAppointmentReminder(Context context, Appointment appointment) {
        cancelAppointmentReminder(context, appointment.getId());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }

        try {
            String dateStr = appointment.getDate();
            String timeStr = appointment.getAppointmentStart();

            // Parse the date
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            Date appointmentDate = dateFormat.parse(dateStr);

            if (appointmentDate == null) {
                Log.e(TAG, "Failed to parse date: " + dateStr);
                return;
            }

            // Parse the time
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            Date appointmentTime = timeFormat.parse(timeStr);

            if (appointmentTime == null) {
                Log.e(TAG, "Failed to parse time: " + timeStr);
                return;
            }

            // Combine date and time
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(appointmentDate);

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(appointmentTime);

            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Subtract notification lead time (e.g., 1 hour before)
            calendar.add(Calendar.MINUTE, -NOTIFICATION_LEAD_TIME);

            // Check if the notification time has already passed
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                Log.w(TAG, "Appointment time has already passed: " + appointment.getClinicName());
                return;
            }

            // For testing/debugging - uncomment to set alarm to trigger soon
            /*
            Calendar testCalendar = Calendar.getInstance();
            testCalendar.add(Calendar.MINUTE, 1);  // Set to trigger in 1 minute
            calendar = testCalendar;
            */

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction("com.example.medassist.APPOINTMENT_REMINDER_" + appointment.getId());
            intent.putExtra(AlarmReceiver.NOTIFICATION_TYPE, AlarmReceiver.TYPE_APPOINTMENT);
            intent.putExtra(AlarmReceiver.APPOINTMENT_TITLE, appointment.getClinicName());
            intent.putExtra(AlarmReceiver.APPOINTMENT_LOCATION, appointment.getLocation());
            intent.putExtra(AlarmReceiver.APPOINTMENT_TIME, appointment.getAppointmentStart());

            int notificationId = generateNotificationId(appointment.getId());
            intent.putExtra("notificationId", notificationId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Use inexact alarm if we can't schedule exact alarms
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent);
            } else {
                // Use exact and allow while idle for important notifications
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent);
            }

            Log.d(TAG, "Scheduled reminder for appointment '" + appointment.getClinicName()
                    + "' at " + dateFormat.format(appointmentDate) + " " + appointment.getAppointmentStart()
                    + " (notification will show at " + calendar.getTime() + ")");

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing appointment date/time", e);
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling appointment notification", e);
        }
    }

    /**
     * Cancel a scheduled appointment reminder
     *
     * @param context Context
     * @param appointmentId ID of the appointment
     */
    public static void cancelAppointmentReminder(Context context, String appointmentId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("com.example.medassist.APPOINTMENT_REMINDER_" + appointmentId);

        int notificationId = generateNotificationId(appointmentId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "Cancelled reminder for appointment ID: " + appointmentId);
    }

    private static int generateNotificationId(String appointmentId) {
        return ("appointment_" + appointmentId).hashCode();
    }
}