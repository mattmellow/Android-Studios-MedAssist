package com.example.medassist.ui.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.medassist.MainActivity;

import com.example.medassist.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseAuthActivity implements EmailAuth {
    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView registerText, forgotPasswordText;
    private FirebaseAuth auth;

    @Override
    protected int getLayoutResourceId() { // links to specfic layout.xml file
        return R.layout.login_login;
    }

    @Override
    protected void initViews() {
        emailField = findViewById(R.id.editTextLoginUsername);
        passwordField = findViewById(R.id.editTextLoginPassword);
        loginButton = findViewById(R.id.loginbuttonLogin);
        registerText = findViewById(R.id.Reg);
        forgotPasswordText = findViewById(R.id.TextViewForgotPassword);

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                authenticate(email, password);//use interference
            }
        });

        registerText.setOnClickListener(new View.OnClickListener() {//for new user
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {// go to verify for forgotpassword
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, VerifyActivity.class));
            }
        });
    }

    @Override
    public void authenticate(String email, String password) { // auth sign in
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                  //jya added and modified to nav to home after login
//                  Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                  intent.putExtra("NAV_TO", "home");  // Custom redirection
//                  startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Please verify your email before logging in", Toast.LENGTH_SHORT).show();// for a new eamil
                }
            } else {
                Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();// any other reason
            }
        });
    }
}