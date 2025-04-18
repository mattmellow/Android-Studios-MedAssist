package com.example.medassist.ui.medication;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medassist.R;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {
    private List<Medication> medications;
    private OnMedicationClickListener listener;

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

        holder.medicationName.setText(medication.getName());
        holder.medicationDosage.setText(medication.getDosage() + ", " + medication.getFrequency());

        holder.timingsContainer.removeAllViews();

        for (String time : medication.getNotificationTimes()) {
            LinearLayout timeEntry = new LinearLayout(holder.itemView.getContext());
            timeEntry.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            timeEntry.setOrientation(LinearLayout.HORIZONTAL);
            timeEntry.setGravity(Gravity.CENTER_VERTICAL);
            timeEntry.setPadding(0, 4, 16, 4);


            ImageView clockIcon = new ImageView(holder.itemView.getContext());
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(50, 50);
            clockIcon.setLayoutParams(iconParams);
            clockIcon.setImageResource(R.drawable.clock_icon);
            timeEntry.addView(clockIcon);


            TextView timeView = new TextView(holder.itemView.getContext());
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.setMarginStart(4);
            timeView.setLayoutParams(textParams);
            timeView.setText(time);
            timeView.setTextSize(14);
            timeView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
            timeEntry.addView(timeView);

            holder.timingsContainer.addView(timeEntry);
        }

        if (medication.getSideEffects() != null && !medication.getSideEffects().isEmpty()) {
            holder.medicationSideEffects.setText(medication.getSideEffects());
            holder.medicationSideEffects.setVisibility(View.VISIBLE);
        } else {
            holder.medicationSideEffects.setVisibility(View.GONE);
        }

        if (medication.getFoodRelation() != null && !medication.getFoodRelation().isEmpty()) {
            holder.medicationFoodRelation.setText(medication.getFoodRelation());
            holder.medicationFoodRelation.setVisibility(View.VISIBLE);
        } else {
            holder.medicationFoodRelation.setVisibility(View.GONE);
        }

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
    public void updateMedications(List<Medication> newMedications) {
        this.medications = newMedications;
        notifyDataSetChanged();
    }

    static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView medicationName;
        TextView medicationDosage;
        TextView medicationFoodRelation;
        TextView medicationSideEffects;
        ViewGroup timingsContainer;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medicationName = itemView.findViewById(R.id.medicationName);
            medicationDosage = itemView.findViewById(R.id.medicationDosage);
            medicationFoodRelation = itemView.findViewById(R.id.medicationFoodRelation);
            medicationSideEffects = itemView.findViewById(R.id.medicationSideEffects);
            timingsContainer = itemView.findViewById(R.id.timingsContainer);
        }
    }
}