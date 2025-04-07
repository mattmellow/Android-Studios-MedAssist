package com.example.medassist.ui.login;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

public abstract class BaseAuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(getLayoutResourceId());
        initViews();
    }

    // Subclasses must provide their own layout resource ID
    protected abstract int getLayoutResourceId();

    // Subclasses initialize their views and listeners here
    protected abstract void initViews();
}
