package com.example.medassist.ui.transform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.medassist.R;
import com.example.medassist.ui.appointment.Appointment;

import java.util.List;

public class AppointmentCard extends BaseCard{
    private List<Appointment> appointments;
    private AppointmentView currentAppointmentView;

    public AppointmentCard(Context context, ViewGroup container, AppointmentView appointment) {
        super(context, container);
        this.currentAppointmentView = appointment;
    }

    public void updateAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        if (appointments != null && !appointments.isEmpty()) {
            Appointment first = appointments.get(0);
            this.currentAppointmentView = new AppointmentView(
                    "Today", // Or parse day from date
                    first.getDate(),
                    first.getClinicName(),
                    first.getLocation(),
                    first.getAppointmentStart()
            );
            if (cardView != null) {
                bindData(cardView); // Refresh UI
            }
        }
    }

    @Override
    protected View createView() {
        return LayoutInflater.from(context)
                .inflate(R.layout.appointment_view_card, container, false);
    }

    @Override
    protected void bindData(View cardView) {
        TextView dayView = cardView.findViewById(R.id.appointmentDay);
        TextView dateView = cardView.findViewById(R.id.appointmentDate);
        TextView titleView = cardView.findViewById(R.id.appointmentTitle1);
        TextView locationView = cardView.findViewById(R.id.appointmentLocation1);
        TextView timeView = cardView.findViewById(R.id.appointmentTime1);

        if (currentAppointmentView != null) {
            dayView.setText(currentAppointmentView.getDay());
            dateView.setText(currentAppointmentView.getDate());
            titleView.setText(currentAppointmentView.getTitle());
            locationView.setText(currentAppointmentView.getLocation());
            timeView.setText(currentAppointmentView.getTime());
        }
    }

    @Override
    protected void setupActions() {
        cardView.setOnClickListener(v -> navigateTo(R.id.nav_appointment));
    }
}
