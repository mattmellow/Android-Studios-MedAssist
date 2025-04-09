package com.example.medassist.ui.transform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.medassist.databinding.FragmentTransformBinding;
import java.util.ArrayList;
import java.util.List;

import com.example.medassist.ui.transform.AppointmentView;
import com.example.medassist.ui.transform.Medication;
import com.example.medassist.ui.transform.Exercise;

public class TransformFragment extends Fragment {

    private FragmentTransformBinding binding;
    private final List<BaseCard> cards = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransformBinding.inflate(inflater, container, false);
        setupCards();
        return binding.getRoot();
    }

    private void setupCards() {
        // Clear previous cards if any
        clearAllCards();

        // 1. Create Medication Card
        Medication med1 = new Medication(
                "Aspirin",
                "8:00 AM",
                "Take with food"
        );
        cards.add(new MedicationCard(
                requireContext(),
                binding.medicationCardContainer,
                med1
        ));

        // 2. Create Appointment Card
        AppointmentView appointment = new AppointmentView(
                "Tuesday",
                "18",
                "Dental Checkup",
                "QM Dental",
                "10:30 AM"
        );
        cards.add(new AppointmentCard(
                requireContext(),
                binding.appointmentCardContainer,
                appointment
        ));

        // 3. Create Exercise Card
        Exercise exercise = new Exercise(
                "Brisk Walking",
                "Swimming",
                true,  // First exercise completed
                false  // Second exercise not completed
        );
        cards.add(new ExerciseCard(
                requireContext(),
                binding.exerciseCardContainer,
                exercise,
                exercise.getCompletionPercentage()  // Auto-calculate progress
        ));

        // Render all cards
        for (BaseCard card : cards) {
            card.render();
        }
    }

    private void clearAllCards() {
        if (binding != null) {
            if (binding.medicationCardContainer != null)
                binding.medicationCardContainer.removeAllViews();
            if (binding.appointmentCardContainer != null)
                binding.appointmentCardContainer.removeAllViews();
            if (binding.exerciseCardContainer != null)
                binding.exerciseCardContainer.removeAllViews();
        }
        cards.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearAllCards();
        binding = null;
    }
}