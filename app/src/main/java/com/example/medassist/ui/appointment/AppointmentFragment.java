package com.example.medassist.ui.appointment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.medassist.R;
import com.example.medassist.databinding.FragmentSlideshowBinding;
import com.example.medassist.ui.slideshow.SlideshowViewModel;

public class AppointmentFragment extends Fragment {

    private AppointmentViewModel mViewModel;

    public static AppointmentFragment newInstance() {
        return new AppointmentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        // TODO: Use the ViewModel
    }

}
