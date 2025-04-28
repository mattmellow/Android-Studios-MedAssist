package com.example.medassist.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.medassist.databinding.FragmentProfileBinding;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://medassist-fdddd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        mStorage = FirebaseStorage.getInstance("https://medassist-fdddd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        uploadImageToFirebase(uri);
                    }
                });

        binding.profileImage.setOnClickListener(v -> openGallery());
        binding.editProfilePhoto.setOnClickListener(v -> openGallery());
        binding.backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        binding.saveChanges.setOnClickListener(v -> saveProfileChanges());

        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            binding.editName.setText("Not Signed In");
            binding.editEmail.setText("Not Signed In");
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        binding.editEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Not Set");

        String userId = currentUser.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    binding.editName.setText(name != null ? name : "Not Set");

                    if (profileImageUrl != null) {
                        Glide.with(requireContext())
                                .load(profileImageUrl)
                                .into(binding.profileImage);
                    }
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.editName.setText("Error Loading");
                binding.editEmail.setText("Error Loading");
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        imagePickerLauncher.launch("image/*");
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        binding.progressBar.setVisibility(View.VISIBLE);
        String userId = currentUser.getUid();
        StorageReference profileImageRef = mStorage.child("profile_images/" + userId + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        mDatabase.child("users").child(userId).child("profileImageUrl").setValue(downloadUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Glide.with(requireContext()).load(downloadUrl).into(binding.profileImage);
                                    binding.progressBar.setVisibility(View.GONE);
                                    Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                    Toast.makeText(requireContext(), "Failed to update profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileChanges() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = binding.editName.getText().toString().trim();
        String email = binding.editEmail.getText().toString().trim();
        String currentPassword = binding.editCurrentPassword.getText().toString().trim();
        String newPassword = binding.editNewPassword.getText().toString().trim();
        String confirmPassword = binding.editConfirmPassword.getText().toString().trim();


        if (name.isEmpty()) {
            binding.editName.setError("Name is required");
            return;
        }

        if (email.isEmpty()) {
            binding.editEmail.setError("Email is required");
            return;
        }

        if (!newPassword.isEmpty() || !currentPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (currentPassword.isEmpty()) {
                binding.editCurrentPassword.setError("Current password is required to change password");
                return;
            }
            if (newPassword.isEmpty()) {
                binding.editNewPassword.setError("New password is required");
                return;
            }
            if (confirmPassword.isEmpty()) {
                binding.editConfirmPassword.setError("Please confirm your new password");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                binding.editConfirmPassword.setError("Passwords do not match");
                return;
            }
            if (newPassword.length() < 6) {
                binding.editNewPassword.setError("Password must be at least 6 characters");
                return;
            }
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.saveChanges.setEnabled(false);

        String userId = currentUser.getUid();
        mDatabase.child("users").child(userId).child("name").setValue(name)
                .addOnSuccessListener(aVoid -> {
                    // Update email in Firebase Auth if it has changed
                    if (!email.equals(currentUser.getEmail())) {
                        currentUser.verifyBeforeUpdateEmail(email)
                                .addOnSuccessListener(aVoid1 -> {
                                    // Handle password change if provided
                                    if (!newPassword.isEmpty()) {
                                        updatePassword(currentUser, currentPassword, newPassword);
                                    } else {
                                        binding.progressBar.setVisibility(View.GONE);
                                        binding.saveChanges.setEnabled(true);
                                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.saveChanges.setEnabled(true);
                                    binding.editEmail.setError("Failed to update email: " + e.getMessage());
                                });
                    } else {
                        // If email hasn't changed, just handle password update
                        if (!newPassword.isEmpty()) {
                            updatePassword(currentUser, currentPassword, newPassword);
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.saveChanges.setEnabled(true);
                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.saveChanges.setEnabled(true);
                    binding.editName.setError("Failed to update name: " + e.getMessage());
                });
    }

    private void updatePassword(FirebaseUser user, String currentPassword, String newPassword) {

        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentPassword))
                .addOnSuccessListener(aVoid -> {
                    user.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid1 -> {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.saveChanges.setEnabled(true);
                                binding.editCurrentPassword.setText("");
                                binding.editNewPassword.setText("");
                                binding.editConfirmPassword.setText("");
                                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.saveChanges.setEnabled(true);
                                binding.editNewPassword.setError("Failed to update password: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.saveChanges.setEnabled(true);
                    binding.editCurrentPassword.setError("Incorrect current password");
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}