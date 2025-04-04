package com.example.medassist.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medassist.MainActivity;
import com.example.medassist.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText Email, Password;
    private Button Login;
    private TextView Register, ForgotPassword;

    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_login);

        Email = findViewById(R.id.editTextLoginUsername);
        Password = findViewById(R.id.editTextLoginPassword);
        Login = findViewById(R.id.loginbuttonLogin);
        Register = findViewById(R.id.Reg); // special naming case
        ForgotPassword = findViewById(R.id.TextViewForgotPassword);

        Auth = FirebaseAuth.getInstance();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                //firebase auth signin thing
                Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = Auth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

//                            //jya added and modified to nav to home after login
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            intent.putExtra("NAV_TO", "home");  // Custom redirection
//                            startActivity(intent);

                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Please verify your email before logging in", Toast.LENGTH_SHORT).show();// for a new eamil
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();// any other reason
                    }
                });
            }
        });






        Register.setOnClickListener(new View.OnClickListener() {//for new user
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() { // go to verify for forgotpassword
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, VerifyActivity.class));
            }
        });
    }
}
