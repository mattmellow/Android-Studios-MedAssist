package com.example.medassist.ui.transform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.medassist.R;
import com.example.medassist.ui.transform.AppointmentView;

public class AppointmentCard extends BaseCard{
    private final AppointmentView appointment;

    public AppointmentCard(Context context, ViewGroup container, AppointmentView appointment) {
        super(context, container);
        this.appointment = appointment;
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

        dayView.setText(appointment.getDay());
        dateView.setText(appointment.getDate());
        titleView.setText(appointment.getTitle());
        locationView.setText(appointment.getLocation());
        timeView.setText(appointment.getTime());
    }

    @Override
    protected void setupActions() {
        cardView.setOnClickListener(v -> navigateTo(R.id.nav_appointment));
    }
}
