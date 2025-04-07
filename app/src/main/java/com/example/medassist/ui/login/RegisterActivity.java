//package com.example.medassist.ui.login;
//
//import android.content.Intent;
//
//import android.os.Bundle;
//
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.medassist.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    private EditText Email, Password, RetypePassword;
//    private Button VerifyAccount;
//    private TextView HomePage;
//
//
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.login_register);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        Email = findViewById(R.id.editTextRegisterEmail);
//        Password = findViewById(R.id.editTextRegisterPassword);
//        RetypePassword = findViewById(R.id.editTextRegisterRetypePassword);
//        VerifyAccount = findViewById(R.id.RegisterButtonVerify);
//        HomePage = findViewById(R.id.RegisterTextViewBacktoHomePage);
//
//        mAuth = FirebaseAuth.getInstance();
//
//        VerifyAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email = Email.getText().toString().trim();
//                String password = Password.getText().toString().trim();
//                String retypepassword = RetypePassword.getText().toString().trim();
//
//                if (email.isEmpty() || password.isEmpty() || retypepassword.isEmpty()) {
//                    Toast.makeText(RegisterActivity.this, "Please fill in all details", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!password.equals(retypepassword)) {
//                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                // to tell user that app is not hanging
//                Toast.makeText(RegisterActivity.this, "Processing. Please wait.", Toast.LENGTH_SHORT).show();
//
//                // Create user in Firebase Auth
//                mAuth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(task -> {
//
//                            if (task.isSuccessful()) {
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                if (user != null) {
//
//                                    // Send the verification email
//                                    user.sendEmailVerification().addOnCompleteListener(task1 -> {
//                                        if (task1.isSuccessful()) {
//                                            Toast.makeText(RegisterActivity.this,
//                                                    "Registration successful. Verification email sent. Please verify and then log in. Please check the spam folder if link not seen.",
//                                                    Toast.LENGTH_LONG).show();
//                                            mAuth.signOut();
//                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                                            finish();
//                                        } else {
//                                            Toast.makeText(RegisterActivity.this,
//                                                    "Failed to send verification email.",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            } else {
//                                Toast.makeText(RegisterActivity.this,
//                                        "Registration failed: " + task.getException().getMessage(),
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
//
//        HomePage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//            }
//        });
//    }
//
//
//}
package com.example.medassist.ui.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.medassist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends BaseAuthActivity {
    private EditText emailField, passwordField, retypePasswordField;
    private Button verifyAccountButton;
    private TextView homePageText;
    private FirebaseAuth auth;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.login_register;
    }

    @Override
    protected void initViews() {
        // Apply window insets for proper UI layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        emailField = findViewById(R.id.editTextRegisterEmail);
        passwordField = findViewById(R.id.editTextRegisterPassword);
        retypePasswordField = findViewById(R.id.editTextRegisterRetypePassword);
        verifyAccountButton = findViewById(R.id.RegisterButtonVerify);
        homePageText = findViewById(R.id.RegisterTextViewBacktoHomePage);

        auth = FirebaseAuth.getInstance();

        verifyAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                String retypePassword = retypePasswordField.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(retypePassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(RegisterActivity.this, "Processing. Please wait.", Toast.LENGTH_SHORT).show();

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this,
                                            "Registration successful. Verification email sent. Please verify and then log in. Please check the spam folder if link not seen.",
                                            Toast.LENGTH_LONG).show();
                                    auth.signOut();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this,
                                            "Failed to send verification email.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        homePageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
}
