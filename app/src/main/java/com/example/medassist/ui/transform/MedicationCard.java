package com.example.medassist.ui.transform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.medassist.R;
import com.example.medassist.ui.transform.Medication;

public class MedicationCard extends BaseCard{
    private final Medication medication;

    public MedicationCard(Context context, ViewGroup container, Medication medication){
        super(context, container);
        this.medication = medication;
    }

    @Override
    protected View createView(){
        return LayoutInflater.from(context).inflate(R.layout.medication_card, container, false);
    }

    @Override
    protected void bindData(View cardView){
        TextView nameView = cardView.findViewById(R.id.medName1);
        TextView timeView = cardView.findViewById(R.id.medTime1);
        TextView warningView = cardView.findViewById(R.id.medWarning1);

        nameView.setText(medication.getName());
        timeView.setText(medication.getTime());

        if (medication.getWarning().isEmpty()) {
            warningView.setVisibility(View.GONE);
        } else {
            warningView.setText(medication.getWarning());
            warningView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setupActions(){
        cardView.setOnClickListener(v -> navigateTo(R.id.nav_medication));
    }
}
