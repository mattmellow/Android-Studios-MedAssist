package com.example.medassist.ui.medication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medassist.R;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {
    private List<Medication> medications;
    private OnMedicationClickListener listener;

    // Interface for click handling
    public interface OnMedicationClickListener {
        void onMedicationClick(Medication medication);
        void onMedicationLongClick(Medication medication, int position);
    }

    public MedicationAdapter(List<Medication> medications) {
        this.medications = medications;
    }

    public void setOnMedicationClickListener(OnMedicationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pill_item, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medications.get(position);

        // Set text for each field
        holder.medicationName.setText(medication.getName());
        holder.medicationDosage.setText(medication.getDosage() + ", " + medication.getFrequency());
        holder.medicationTime.setText(medication.getTime());

        // Handle side effects (may be optional)
        if (medication.getSideEffects() != null && !medication.getSideEffects().isEmpty()) {
            holder.medicationSideEffects.setText(medication.getSideEffects());
            holder.medicationSideEffects.setVisibility(View.VISIBLE);
        } else {
            holder.medicationSideEffects.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMedicationClick(medication);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onMedicationLongClick(medication, holder.getAdapterPosition());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return medications != null ? medications.size() : 0;
    }

    /**
     * Updates the list of medications and refreshes the display
     * @param newMedications The new list of medications to display
     */
    public void updateMedications(List<Medication> newMedications) {
        this.medications = newMedications;
        notifyDataSetChanged();
    }

    /**
     * Removes a medication at the specified position
     * @param position Position of the medication to remove
     * @return The removed medication, or null if position is invalid
     */
    public Medication removeMedication(int position) {
        if (position >= 0 && position < medications.size()) {
            Medication removedMedication = medications.get(position);
            medications.remove(position);
            notifyItemRemoved(position);
            return removedMedication;
        }
        return null;
    }

    /**
     * ViewHolder class for caching view references
     */
    static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView medicationName;
        TextView medicationDosage;
        TextView medicationTime;
        TextView medicationSideEffects;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medicationName = itemView.findViewById(R.id.medicationName);
            medicationDosage = itemView.findViewById(R.id.medicationDosage);
            medicationTime = itemView.findViewById(R.id.medicationTime);
            medicationSideEffects = itemView.findViewById(R.id.medicationSideEffects);
        }
    }
}