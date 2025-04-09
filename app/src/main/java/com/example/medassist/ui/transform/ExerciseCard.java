package com.example.medassist.ui.transform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.medassist.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class ExerciseCard extends BaseCard {
    private final Exercise exercise;
    private final int progress;

    public ExerciseCard(Context context, ViewGroup container, Exercise exercise, int progress) {
        super(context, container);
        this.exercise = exercise;
        this.progress = exercise.getCompletionPercentage();
    }

    @Override
    protected View createView() {
        return LayoutInflater.from(context)
                .inflate(R.layout.exercise_card, container, false);
    }

    @Override
    protected void bindData(View cardView) {
        CircularProgressBar progressBar = cardView.findViewById(R.id.exerciseProgress);
        TextView progressText = cardView.findViewById(R.id.progressNumber);
        TextView exercise1View = cardView.findViewById(R.id.exercise1);
        TextView exercise2View = cardView.findViewById(R.id.exercise2);

        progressBar.setProgress(progress);
        progressText.setText(progress + "%");
        exercise1View.setText(exercise.getName1());
        exercise2View.setText(exercise.getName2());
    }

    @Override
    protected void setupActions() {
        cardView.setOnClickListener(v -> navigateTo(R.id.nav_exercise));
    }
}