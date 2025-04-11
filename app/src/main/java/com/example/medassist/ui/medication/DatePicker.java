package com.example.medassist.ui.medication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class DatePicker extends Fragment {
    private RecyclerView dateRecyclerView;
    private LocalDate selectedDate;
    private DateList dateItemAdapter;
    private OnDateSelectedListener dateSelectedListener;
    private OnMonthChangeListener monthChangeListener;

    private static final int INITIAL_LOAD_DAYS = 60;
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
        dateRecyclerView = view.findViewById(R.id.weekDaysRecyclerView);
        selectedDate = LocalDate.now();
        setupDateView();
        setupScrollListeners();
        return view;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.dateSelectedListener = listener;
    }

    private void setupDateView() {
        LocalDate startDate = selectedDate.minusDays(INITIAL_LOAD_DAYS);
        LocalDate endDate = selectedDate.plusDays(INITIAL_LOAD_DAYS);

        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date);
        }

        dateItemAdapter = new DateList(dates, selectedDate, this::onDateClick);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateRecyclerView.setAdapter(dateItemAdapter);

        int selectedPosition = dates.indexOf(selectedDate);
        if (selectedPosition != -1) {
            dateRecyclerView.scrollToPosition(selectedPosition);
        }
    }

    //set up scroll listeners so that when user scrolls a new month and it becomes visible in page, trigger the on month change mthd
    private void setupScrollListeners() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) dateRecyclerView.getLayoutManager();
        dateRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dateItemAdapter == null || layoutManager == null) return;

                List<LocalDate> dates = dateItemAdapter.getDates();
                if (dates.isEmpty()) return;

                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

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

    private void loadMoreDates() {
        List<LocalDate> currentDates = dateItemAdapter.getDates();
        if (currentDates.isEmpty()) return;
        LocalDate lastDate = currentDates.get(currentDates.size() - 1);
        for (int i = 1; i <= 7; i++) {
            currentDates.add(lastDate.plusDays(i));
        }
        dateItemAdapter.notifyItemRangeInserted(currentDates.size() - 7, 7);
    }

    private void onDateClick(LocalDate date) {
        dateItemAdapter.setSelectedDate(date);
        selectedDate = date;
        if (dateSelectedListener != null) {
            dateSelectedListener.onDateSelected(date);
        }
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }
}