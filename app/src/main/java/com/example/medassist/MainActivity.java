package com.example.medassist;


import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.medassist.ui.reminders.NotificationInitializer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;


import com.example.medassist.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.widget.PopupMenu;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        ImageView accountImageView = findViewById(R.id.accountImageView);
        if (accountImageView != null) {
            // Set click listener to show popup menu
            accountImageView.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(MainActivity.this, accountImageView);
                popup.getMenuInflater().inflate(R.menu.overflow, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.nav_settings) {
                        NavController navController = Navigation.findNavController(MainActivity.this,
                                R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_settings);
                        return true;
                    }
                    else if (item.getItemId() == R.id.nav_all_medications) {
                        NavController navController = Navigation.findNavController(MainActivity.this,
                                R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_all_medications);
                        return true;
                    }
                    else if (item.getItemId() == R.id.nav_all_appointments) {
                        NavController navController = Navigation.findNavController(MainActivity.this,
                                R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_all_appointments);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        if (binding.appBarMain.fab != null) {
            binding.appBarMain.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).setAnchorView(R.id.fab).show());
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // Handling the page title here
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            TextView titleLine1 = findViewById(R.id.titleLine1);
            TextView titleLine2 = findViewById(R.id.titleLine2);

            if (titleLine1 != null && titleLine2 != null) {
                int destId = destination.getId();

                if (destId == R.id.nav_transform) {
                    titleLine1.setText("Welcome back,");
                    titleLine2.setText("Your Overview");
                }
                else if (destId == R.id.nav_medication) {
                    titleLine1.setText("Manage your");
                    titleLine2.setText("Medications");
                }
                else if (destId == R.id.nav_appointment) {
                    titleLine1.setText("Your");
                    titleLine2.setText("Appointments");
                }
                else if (destId == R.id.nav_exercise) {
                    titleLine1.setText("Track your");
                    titleLine2.setText("Exercises");
                }
                else if (destId == R.id.nav_settings) {
                    titleLine1.setText("Account");
                    titleLine2.setText("Settings");
                }
                else if (destId == R.id.nav_all_medications) {
                    titleLine1.setText("Manage your");
                    titleLine2.setText("Medications");
                }
                else if (destId == R.id.nav_all_appointments) {
                    titleLine1.setText("Manage your");
                    titleLine2.setText("Appointments");
                }
                else {
                    titleLine1.setText("Welcome back,");
                    titleLine2.setText("User");
                }
            }
        });

        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_transform, R.id.nav_medication, R.id.nav_exercise, R.id.nav_settings, R.id.nav_appointment, R.id.nav_all_medications, R.id.nav_all_appointments)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;
        if (bottomNavigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_transform, R.id.nav_medication, R.id.nav_exercise, R.id.nav_appointment)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);

                if (item.getItemId() == R.id.nav_transform) {
                    // Ensure we always return to Home (even if already there), clearing intermediate stack
                    navController.popBackStack(R.id.nav_transform, false);
                }

                return handled;
            });
        }

        NotificationInitializer.initializeAllReminders(this);


        // Hide the default title in the toolbar
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }
//
//        TextView pageTitleTextView = findViewById(R.id.pageTitleTextView);
//        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            if (pageTitleTextView != null) {
//                pageTitleTextView.setText(destination.getLabel());
//            }
//        });
    }

//    private String getUserName() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null && user.getDisplayName() != null) {
//            return user.getDisplayName();
//        }
//        return "User"; // Default if not logged in or no name
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
        }
        else if (item.getItemId() == R.id.nav_all_medications) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_all_medications);
        }
        else if (item.getItemId() == R.id.nav_all_appointments) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_all_appointments);
        }
        else if (item.getItemId() == R.id.nav_profile) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_profile);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationPermission() {
        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }
}