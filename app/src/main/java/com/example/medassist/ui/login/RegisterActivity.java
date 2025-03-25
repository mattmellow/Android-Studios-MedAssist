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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.medassist.R;

public class RegisterActivity extends AppCompatActivity {

    EditText Email, Username, Password, RetypePassword;
    Button VerifyAccount;
    TextView HomePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Email = findViewById(R.id.editTextRegisterEmail);
        Username = findViewById(R.id.editTextRegisterUsername);
        Password = findViewById(R.id.editTextRegisterPassword);
        RetypePassword = findViewById(R.id.editTextRegisterRetypePassword);
        VerifyAccount = findViewById(R.id.RegisterButtonVerify);
        HomePage = findViewById(R.id.RegisterTextViewBacktoHomePage);

        VerifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = Username.getText().toString();
                String email = Email.getText().toString();
                String retypepassword = RetypePassword.getText().toString();
                String password = Password.getText().toString();
                if (email.isEmpty() || username.isEmpty() || password.isEmpty() || retypepassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all details", Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(retypepassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }
        });

        HomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });



    }
}

