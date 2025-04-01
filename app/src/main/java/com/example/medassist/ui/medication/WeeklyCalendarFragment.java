package com.example.medassist.ui.medication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medassist.R;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeeklyCalendarFragment extends Fragment {
    private RecyclerView weekDaysRecyclerView;
    private LocalDate selectedDate;
    private WeekDaysAdapter weekDaysAdapter;
    private OnDateSelectedListener dateSelectedListener;
    private OnMonthChangeListener monthChangeListener;

    private static final int INITIAL_LOAD_DAYS = 60; // Load more days initially
    private static final int LOAD_MORE_THRESHOLD = 5;

    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate date);
    }

    public interface OnMonthChangeListener {
        void onMonthChanged(String month, int year);
    }

    public void setOnMonthChangeListener(OnMonthChangeListener listener) {
        this.monthChangeListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weekly_calendar_view, container, false);

        weekDaysRecyclerView = view.findViewById(R.id.weekDaysRecyclerView);
        selectedDate = LocalDate.now();

        setupWeekView();
        setupScrollListeners();

        return view;
    }

    // Method to set a listener for date selection
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.dateSelectedListener = listener;
    }

    private void setupWeekView() {
        LocalDate startDate = selectedDate.minusDays(INITIAL_LOAD_DAYS);
        LocalDate endDate = selectedDate.plusDays(INITIAL_LOAD_DAYS);

        List<LocalDate> weekDates = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            weekDates.add(date);
        }

        weekDaysAdapter = new WeekDaysAdapter(weekDates, selectedDate, this::onDateClick);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        weekDaysRecyclerView.setLayoutManager(layoutManager);
        weekDaysRecyclerView.setAdapter(weekDaysAdapter);

        int selectedPosition = weekDates.indexOf(selectedDate);
        if (selectedPosition != -1) {
            weekDaysRecyclerView.scrollToPosition(selectedPosition);
        }
    }
    private void setupScrollListeners() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) weekDaysRecyclerView.getLayoutManager();

        // Month tracking scroll listener
        weekDaysRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (weekDaysAdapter == null || layoutManager == null) return;

                List<LocalDate> dates = weekDaysAdapter.getWeekDates();
                if (dates.isEmpty()) return;

                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                // Count months in visible range
                Map<String, Integer> monthCounts = new HashMap<>();
                for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                    if (i >= 0 && i < dates.size()) {
                        LocalDate date = dates.get(i);
                        String monthKey = date.getMonth().toString() + date.getYear();
                        monthCounts.put(monthKey, monthCounts.getOrDefault(monthKey, 0) + 1);
                    }
                }

                // Find the most prevalent month
                String mostPrevalentMonthKey = null;
                int maxCount = 0;
                for (Map.Entry<String, Integer> entry : monthCounts.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        mostPrevalentMonthKey = entry.getKey();
                        maxCount = entry.getValue();
                    }
                }

                // Update month if a prevalent month is found
                if (mostPrevalentMonthKey != null) {
                    for (LocalDate date : dates) {
                        String currentMonthKey = date.getMonth().toString() + date.getYear();
                        if (currentMonthKey.equals(mostPrevalentMonthKey)) {
                            if (monthChangeListener != null) {
                                monthChangeListener.onMonthChanged(
                                        date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                                        date.getYear()
                                );
                                break;
                            }
                        }
                    }
                }
                // Load more dates when near the end
                int totalItemCount = layoutManager.getItemCount();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                if (lastVisiblePosition + LOAD_MORE_THRESHOLD >= totalItemCount) {
                    loadMoreDates();
                }
            }
        });
    }
    private void loadPastDates() {
        List<LocalDate> currentDates = weekDaysAdapter.getWeekDates();
        LocalDate firstDate = currentDates.get(0);

        // Insert previous 7 days at the beginning
        List<LocalDate> newDates = new ArrayList<>();
        for (int i = 7; i > 0; i--) {
            newDates.add(firstDate.minusDays(i));
        }

        currentDates.addAll(0, newDates);
        weekDaysAdapter.notifyItemRangeInserted(0, newDates.size());

        // Maintain scroll position to avoid jump
        weekDaysRecyclerView.scrollToPosition(newDates.size());
    }

    private void loadMoreDates() {
        List<LocalDate> currentDates = weekDaysAdapter.getWeekDates();
        LocalDate lastDate = currentDates.get(currentDates.size() - 1);

        // Add next 7 days
        for (int i = 1; i <= 7; i++) {
            currentDates.add(lastDate.plusDays(i));
        }

        weekDaysAdapter.notifyItemRangeInserted(currentDates.size() - 7, 7);
    }

    private void onDateClick(LocalDate date) {
        // Update selected date in adapter
        weekDaysAdapter.setSelectedDate(date);
        selectedDate = date;

        // Notify listener if set
        if (dateSelectedListener != null) {
            dateSelectedListener.onDateSelected(date);
        }
    }

    // Method to update the week view programmatically
    public void updateWeekView(LocalDate newDate) {
        selectedDate = newDate;
        setupWeekView();
    }

    // Method to get currently selected date
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    // RecyclerView Adapter
    private static class WeekDaysAdapter extends RecyclerView.Adapter<WeekDaysAdapter.WeekDayViewHolder> {
        private final List<LocalDate> weekDates;
        private LocalDate selectedDate;
        private final OnDateClickListener dateClickListener;

        public interface OnDateClickListener {
            void onDateClick(LocalDate date);
        }

        public WeekDaysAdapter(List<LocalDate> weekDates, LocalDate selectedDate, OnDateClickListener listener) {
            this.weekDates = weekDates;
            this.selectedDate = selectedDate;
            this.dateClickListener = listener;
        }

        // Method to get the current dates list
        public List<LocalDate> getWeekDates() {
            return weekDates;
        }

        public void setSelectedDate(LocalDate date) {
            LocalDate oldSelectedDate = selectedDate;
            selectedDate = date;

            int oldPosition = weekDates.indexOf(oldSelectedDate);
            int newPosition = weekDates.indexOf(date);

            if (oldPosition != -1) {
                notifyItemChanged(oldPosition);
            }
            if (newPosition != -1) {
                notifyItemChanged(newPosition);
            }
        }

        @NonNull
        @Override
        public WeekDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.day_week, parent, false);
            return new WeekDayViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeekDayViewHolder holder, int position) {
            LocalDate date = weekDates.get(position);

            // Set day of week abbreviation
            holder.dayOfWeekText.setText(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).toUpperCase());

            // Set day of month
            holder.dateOfMonthText.setText(String.valueOf(date.getDayOfMonth()));

            // Set selection and color
            boolean isSelected = date.isEqual(selectedDate);
            holder.dayContainer.setSelected(isSelected);
            holder.dateOfMonthText.setSelected(isSelected);
            holder.dayOfWeekText.setSelected(isSelected);

            if (isSelected) {
                holder.dateOfMonthText.setTextColor(holder.itemView.getResources().getColor(android.R.color.white));
                holder.dayOfWeekText.setTextColor(holder.itemView.getResources().getColor(android.R.color.white));
            } else {
                holder.dateOfMonthText.setTextColor(holder.itemView.getResources().getColor(R.color.black));
                holder.dayOfWeekText.setTextColor(holder.itemView.getResources().getColor(R.color.black));
            }

            // Set click listener
            holder.itemView.setOnClickListener(v -> dateClickListener.onDateClick(date));
        }

        @Override
        public int getItemCount() {
            return weekDates.size();
        }

        static class WeekDayViewHolder extends RecyclerView.ViewHolder {
            TextView dayOfWeekText;
            TextView dateOfMonthText;
            LinearLayout dayContainer;

            public WeekDayViewHolder(@NonNull View itemView) {
                super(itemView);
                dayOfWeekText = itemView.findViewById(R.id.dayOfWeekTextView);
                dateOfMonthText = itemView.findViewById(R.id.dateOfMonthTextView);
                dayContainer = itemView.findViewById(R.id.dayContainer);
            }
        }
    }
}