package com.example.medassist.ui.medication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.medassist.databinding.FragmentMedicationBinding;
import com.example.medassist.R;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class MedicationFragment extends Fragment implements WeeklyCalendarFragment.OnDateSelectedListener {

    private FragmentMedicationBinding binding;
    private WeeklyCalendarFragment weeklyCalendarFragment;
    private MedicationViewModel medicationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        medicationViewModel = new ViewModelProvider(this).get(MedicationViewModel.class);

        binding = FragmentMedicationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        weeklyCalendarFragment = (WeeklyCalendarFragment)
                getChildFragmentManager().findFragmentById(R.id.weeklyCalendarFragment);

        if (weeklyCalendarFragment == null) {
            weeklyCalendarFragment = new WeeklyCalendarFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.weeklyCalendarFragment, weeklyCalendarFragment)
                    .commit();
        }

        weeklyCalendarFragment.setOnDateSelectedListener(this);
        weeklyCalendarFragment.setOnMonthChangeListener((month, year) -> {
            binding.currentMonthTextView.setText(month);
            binding.currentYearTextView.setText(String.valueOf(year));
        });

        LocalDate currentDate = LocalDate.now();
        String currentMonth = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        int currentYear = currentDate.getYear();

        binding.currentMonthTextView.setText(currentMonth);
        binding.currentYearTextView.setText(String.valueOf(currentYear));

        return root;
    }

    @Override
    public void onDateSelected(LocalDate date) {

        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        int year = date.getYear();

        binding.currentMonthTextView.setText(month);
        binding.currentYearTextView.setText(String.valueOf(year));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}