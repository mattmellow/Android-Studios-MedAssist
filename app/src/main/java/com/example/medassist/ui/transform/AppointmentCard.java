package com.example.medassist.ui.transform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medassist.R;
import com.example.medassist.ui.appointment.Appointment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppointmentCard extends BaseCard {
    private List<Appointment> appointments;
    private LinearLayout appointmentsContainer;
    private TextView emptyView;

    public AppointmentCard(Context context, ViewGroup container, Appointment appointment) {
        super(context, container);
        this.appointments = new ArrayList<>();
        if (appointment != null) {
            this.appointments.add(appointment);
        }
    }

    @Override
    protected View createView() {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.appointment_view_card, container, false);

        appointmentsContainer = view.findViewById(R.id.appointmentsContainer);
        emptyView = view.findViewById(R.id.emptyAppointmentsText);

        return view;
    }

    @Override
    protected void bindData(View cardView) {
        appointmentsContainer.removeAllViews();

        if (appointments == null || appointments.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        emptyView.setVisibility(View.GONE);

        for (Appointment appointment : appointments) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.item_appointment, appointmentsContainer, false);

            // Parse day and date
            String day = parseDayFromDate(appointment.getDate());
            String date = parseDateToDayOnly(appointment.getDate());

            // Bind data
            TextView dayView = itemView.findViewById(R.id.appointmentDay);
            TextView dateView = itemView.findViewById(R.id.appointmentDate);
            TextView titleView = itemView.findViewById(R.id.appointmentTitle);
            TextView locationView = itemView.findViewById(R.id.appointmentLocation);
            TextView timeView = itemView.findViewById(R.id.appointmentTime);

            dayView.setText(day);
            dateView.setText(date);
            titleView.setText(appointment.getClinicName());
            locationView.setText(appointment.getLocation());
            timeView.setText(appointment.getAppointmentStart());

            appointmentsContainer.addView(itemView);
        }
    }

    private String parseDayFromDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate localDate = LocalDate.parse(date, formatter);
            return localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        } catch (Exception e) {
            return "";
        }
    }

    private String parseDateToDayOnly(String date) {
        try {
            String[] parts = date.split("/");
            return parts[0]; // Return just the day part (dd)
        } catch (Exception e) {
            return date; // Fallback to full date if parsing fails
        }
    }

    public void updateAppointments(List<Appointment> appointments) {
        this.appointments = appointments != null ? appointments : new ArrayList<>();
        if (cardView != null) {
            bindData(cardView);
        }
    }

    @Override
    protected void setupActions() {
        cardView.setOnClickListener(v -> navigateTo(R.id.nav_appointment));
    }
}
