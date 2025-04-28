package com.example.medassist.ui.medication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medassist.R;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class DateList extends RecyclerView.Adapter<DateList.DateItemViewHolder> {
    private final List<LocalDate> dates;
    private LocalDate selectedDate;
    private final OnDateClickListener dateClickListener;

    public interface OnDateClickListener {
        void onDateClick(LocalDate date);
    }

    public DateList(List<LocalDate> dates, LocalDate selectedDate, OnDateClickListener listener) {
        this.dates = dates;
        this.selectedDate = selectedDate;
        this.dateClickListener = listener;
    }

    public List<LocalDate> getDates() {
        return dates;
    }


    public void setSelectedDate(LocalDate date) {
        LocalDate oldSelectedDate = selectedDate;
        selectedDate = date;

        int oldPosition = dates.indexOf(oldSelectedDate);
        int newPosition = dates.indexOf(date);

        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        if (newPosition != -1) {
            notifyItemChanged(newPosition);
        }
    }

    @NonNull
    @Override
    public DateItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_week, parent, false);
        return new DateItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateItemViewHolder holder, int position) {
        LocalDate date = dates.get(position);
        //abbreviation for day
        holder.dayOfWeekText.setText(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).toUpperCase());
        holder.dateOfMonthText.setText(String.valueOf(date.getDayOfMonth()));
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

        holder.itemView.setOnClickListener(v -> dateClickListener.onDateClick(date));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    static class DateItemViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfWeekText;
        TextView dateOfMonthText;
        LinearLayout dayContainer;

        public DateItemViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeekText = itemView.findViewById(R.id.dayOfWeekTextView);
            dateOfMonthText = itemView.findViewById(R.id.dateOfMonthTextView);
            dayContainer = itemView.findViewById(R.id.dayContainer);
        }
    }
}