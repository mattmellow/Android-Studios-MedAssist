package com.example.medassist.ui.transform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.medassist.databinding.FragmentTransformBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.medassist.ui.appointment.Appointment;
import com.example.medassist.ui.appointment.AppointmentRepository;
import com.example.medassist.ui.reminders.ReminderRepository;
import com.example.medassist.ui.medication.Medication;
import com.example.medassist.ui.medication.MedicationRepository;
import com.google.firebase.auth.FirebaseAuth;

public class TransformFragment extends Fragment {

    private FragmentTransformBinding binding;
    private final List<BaseCard> cards = new ArrayList<>();
    private AppointmentRepository appointmentRepository;
    private MedicationRepository medicationRepository;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransformBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        appointmentRepository = new AppointmentRepository(requireContext());
        medicationRepository = new MedicationRepository(requireContext());

        setupCards(); //Initiliaze UI with placeholders
        loadUpcomingAppointments();
        loadTodaysMedications();
        return binding.getRoot();
    }

    private void loadUpcomingAppointments() {
        appointmentRepository.loadAppointments(new ReminderRepository.OnRemindersLoadedListener() {
            @Override
            public void onRemindersLoaded(List<Map<String, Object>> reminders) {
                List<Appointment> upcomingAppointments = new ArrayList<>();
                LocalDate today = LocalDate.now();
                LocalDate nextWeek = today.plusDays(7);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

                for (Map<String, Object> map : reminders) {
                    Appointment appointment = (Appointment) map.get("appointment");
                    if (appointment != null) {
                        LocalDate appointmentDate = LocalDate.parse(appointment.getDate(), formatter);
                        if (!appointmentDate.isBefore(today) && !appointmentDate.isAfter(nextWeek)) {
                            upcomingAppointments.add(appointment);
                        }
                    }
                }

                // Update Appointment Card
                AppointmentCard appointmentCard = findAppointmentCard();
                if (appointmentCard != null) {
                    appointmentCard.updateAppointments(upcomingAppointments);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Appointments: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTodaysMedications() {
        medicationRepository.loadMedications(new ReminderRepository.OnRemindersLoadedListener() {
            @Override
            public void onRemindersLoaded(List<Map<String, Object>> reminders) {
                List<Medication> todaysMeds = new ArrayList<>();

                for (Map<String, Object> map : reminders) {
                    Medication med = (Medication) map.get("medication");
                    if (med != null) {
                        todaysMeds.add(med);
                    }
                }

                // Update Medication Card
                MedicationCard medicationCard = findMedicationCard();
                if (medicationCard != null) {
                    medicationCard.updateMedications(todaysMeds);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Medications: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Helper Methods ---
    private AppointmentCard findAppointmentCard() {
        for (BaseCard card : cards) {
            if (card instanceof AppointmentCard) {
                return (AppointmentCard) card;
            }
        }
        return null;
    }

    private MedicationCard findMedicationCard() {
        for (BaseCard card : cards) {
            if (card instanceof MedicationCard) {
                return (MedicationCard) card;
            }
        }
        return null;
    }

    private void setupCards() {
        // Clear previous cards if any
        clearAllCards();

        cards.add(new MedicationCard(requireContext(), binding.medicationCardContainer, null));
        cards.add(new AppointmentCard(requireContext(), binding.appointmentCardContainer, null));

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