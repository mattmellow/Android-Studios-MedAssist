package com.example.medassist.ui.transform;

import android.os.Bundle;
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
        View root = binding.getRoot();

        // Initialize views from your home layout
        ImageButton addMedButton = binding.addMedButton; // These IDs must match your XML
        CardView appointmentCard = binding.appointmentViewCard;
        CardView exerciseCard = binding.exerciseCard;

        // Set click listeners
        addMedButton.setOnClickListener(v ->
                navigateTo(R.id.nav_medication));

        appointmentCard.setOnClickListener(v ->
                navigateTo(R.id.nav_appointment));

        exerciseCard.setOnClickListener(v ->
                navigateTo(R.id.nav_exercise));

        return root;
    }

    private void navigateTo(@IdRes int destinationId) {
        View view = getView();
        if (view != null) {
            Navigation.findNavController(view).navigate(destinationId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}