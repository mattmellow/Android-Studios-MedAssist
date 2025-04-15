package com.example.medassist.ui.transform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.medassist.R;
import com.example.medassist.ui.medication.Medication;

import java.util.List;

public class MedicationCard extends BaseCard{
    private List<Medication> medication;
    private Medication currentMedication;

    public void updateMedications(List<Medication> medications) {
        this.medication = medications;
        if (medications != null && !medications.isEmpty()) {
            this.currentMedication = medications.get(0); // Show first medication
            if (cardView != null) {
                bindData(cardView); // Refresh UI
            }
        }
    }

    public MedicationCard(Context context, ViewGroup container, Medication medication) {
        super(context, container);
        this.currentMedication = medication;
    }

    @Override
    protected View createView(){
        return LayoutInflater.from(context).inflate(R.layout.medication_card, container, false);
    }

    @Override
    protected void bindData(View cardView) {
        TextView nameView = cardView.findViewById(R.id.medName);
        TextView dosageView = cardView.findViewById(R.id.medDosage); // Add this view
        TextView warningView = cardView.findViewById(R.id.medWarning);

        if (currentMedication != null) {
            nameView.setText(currentMedication.getName());
            dosageView.setText(currentMedication.getDosage());
            warningView.setText(currentMedication.getSideEffects());
        }
    }


    @Override
    protected void setupActions(){
        cardView.setOnClickListener(v -> navigateTo(R.id.nav_medication));
    }
}
