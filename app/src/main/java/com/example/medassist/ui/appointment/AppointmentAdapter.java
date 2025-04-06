package com.example.medassist.ui.appointment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medassist.R;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;
    private OnAppointmentClickListener listener;

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
        void onAppointmentLongClick(Appointment appointment, int position);
    }

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void setOnAppointmentClickListener(OnAppointmentClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        holder.clinicName.setText(appointment.getClinicName());
        holder.appointmentTime.setText(appointment.getAppointmentStart() + " - " + appointment.getAppointmentEnd());
        holder.appointmentLocation.setText(appointment.getLocation());
        holder.appointmentDate.setText(appointment.getDate());

        if (appointment.getDescription() != null && !appointment.getDescription().isEmpty()) {
            holder.appointmentDescription.setText(appointment.getDescription());
            holder.appointmentDescription.setVisibility(View.VISIBLE);
        } else {
            holder.appointmentDescription.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppointmentClick(appointment);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onAppointmentLongClick(appointment, holder.getAdapterPosition());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    public void updateAppointments(List<Appointment> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    public Appointment removeAppointment(int position) {
        if (position >= 0 && position < appointments.size()) {
            Appointment removed = appointments.remove(position);
            notifyItemRemoved(position);
            return removed;
        }
        return null;
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView clinicName;
        TextView appointmentTime;
        TextView appointmentDate;
        TextView appointmentLocation;
        TextView appointmentDescription;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            clinicName = itemView.findViewById(R.id.appointmentClinicName);
            appointmentTime = itemView.findViewById(R.id.appointmentTime);
            appointmentDate = itemView.findViewById(R.id.appointmentDate);
            appointmentLocation = itemView.findViewById(R.id.appointmentLocation);
            appointmentDescription = itemView.findViewById(R.id.appointmentDescription);
        }
    }
}
