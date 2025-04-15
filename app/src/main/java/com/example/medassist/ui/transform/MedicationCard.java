package com.example.medassist.ui.transform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medassist.R;
import com.example.medassist.ui.medication.Medication;

import java.util.ArrayList;
import java.util.List;

public class MedicationCard extends BaseCard {
    private List<Medication> medications;
    private MedicationAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    public MedicationCard(Context context, ViewGroup container, Medication medication) {
        super(context, container);
        this.medications = new ArrayList<>();
        if (medication != null) {
            this.medications.add(medication);
        }
    }

    @Override
    protected View createView() {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.medication_card, container, false);

        recyclerView = view.findViewById(R.id.medicationsRecyclerView);
        emptyView = view.findViewById(R.id.emptyMedicationsText);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MedicationAdapter(medications);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    protected void bindData(View cardView) {
        if (medications == null || medications.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.updateMedications(medications);
        }
    }

    public void updateMedications(List<Medication> medications) {
        this.medications = medications != null ? medications : new ArrayList<>();
        if (cardView != null) {
            bindData(cardView);
        }
    }

    @Override
    protected void setupActions() {
        cardView.setOnClickListener(v -> navigateTo(R.id.nav_medication));
    }
}
