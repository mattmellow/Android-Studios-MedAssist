package com.example.medassist.ui.transform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.medassist.R;
import com.example.medassist.ui.medication.Medication;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {
    private List<Medication> medications;

    public MedicationAdapter(List<Medication> medications) {
        this.medications = medications;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medications.get(position);
        holder.bind(medication);
    }

    @Override
    public int getItemCount() {
        return medications != null ? medications.size() : 0;
    }

    public void updateMedications(List<Medication> newMedications) {
        this.medications = newMedications;
        notifyDataSetChanged();
    }

    static class MedicationViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final TextView dosageView;
        private final TextView warningView;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.medName);
            dosageView = itemView.findViewById(R.id.medDosage);
            warningView = itemView.findViewById(R.id.medWarning);
        }

        public void bind(Medication medication) {
            nameView.setText(medication.getName());
            dosageView.setText(medication.getDosage());
            warningView.setText(medication.getSideEffects());
        }
    }
}