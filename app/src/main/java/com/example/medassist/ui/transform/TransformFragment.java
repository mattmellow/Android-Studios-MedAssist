package com.example.medassist.ui.transform;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Button;
import androidx.cardview.widget.CardView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medassist.R;
import com.example.medassist.databinding.FragmentTransformBinding;
import com.example.medassist.databinding.ItemTransformBinding;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.IdRes;

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
public class TransformFragment extends Fragment {

    private FragmentTransformBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransformBinding.inflate(inflater, container, false);

        setupCardNavigation();

        return binding.getRoot();
    }

    private void setupCardNavigation() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        binding.medicationCard.setOnClickListener(v -> {
            navController.navigate(R.id.nav_medication);
        });

        binding.appointmentViewCard.setOnClickListener(v -> {
            navController.navigate(R.id.nav_appointment);
        });

        binding.exerciseCard.setOnClickListener(v -> {
            navController.navigate(R.id.nav_exercise);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}