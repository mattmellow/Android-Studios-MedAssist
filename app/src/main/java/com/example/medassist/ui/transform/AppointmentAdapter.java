package com.example.medassist.ui.transform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medassist.R;
import com.example.medassist.ui.appointment.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    public void updateAppointments(List<Appointment> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView dayView;
        private final TextView dateView;
        private final TextView titleView;
        private final TextView locationView;
        private final TextView timeView;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            dayView = itemView.findViewById(R.id.appointmentDay);
            dateView = itemView.findViewById(R.id.appointmentDate);
            titleView = itemView.findViewById(R.id.appointmentTitle);
            locationView = itemView.findViewById(R.id.appointmentLocation);
            timeView = itemView.findViewById(R.id.appointmentTime);
        }

        public void bind(Appointment appointment) {
            // Parse day from date (you'll need to implement this)
            String day = parseDayFromDate(appointment.getDate());

            dayView.setText(day);
            dateView.setText(appointment.getDate());
            titleView.setText(appointment.getClinicName());
            locationView.setText(appointment.getLocation());
            timeView.setText(appointment.getAppointmentStart());
        }

        private String parseDayFromDate(String date) {
            // Implement date parsing to get day (e.g., "Monday")
            // This is a placeholder - implement based on your date format
            return "Today"; // Or parse actual day
        }
    }
}