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

public class LoginActivity extends AppCompatActivity {
    EditText Username, Password;
    Button Login;
    TextView Register, ForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_login);


        Username = findViewById(R.id.editTextLoginUsername);
        Password = findViewById(R.id.editTextLoginPassword);
        Login = findViewById(R.id.loginbuttonLogin);
        Register = findViewById(R.id.Reg);
        ForgotPassword = findViewById(R.id.TextViewForgotPassword);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = Username.getText().toString();
                String password = Password.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all details", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();// does not stay on stack
                }
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, VerifyActivity.class));
            }
        });
    }
}
