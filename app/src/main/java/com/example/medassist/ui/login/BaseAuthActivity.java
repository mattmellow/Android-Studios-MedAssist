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


    protected abstract int getLayoutResourceId(); // layout resource id


    protected abstract void initViews(); // wiewers and listeners
}