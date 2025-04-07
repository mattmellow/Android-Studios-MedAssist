package com.example.medassist.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.medassist.R;
import com.google.firebase.auth.FirebaseAuth;

public class VerifyActivity extends BaseAuthActivity {
    private EditText emailInput;
    private Button sendCode;
    private TextView maskedEmailTextView, homePage;
    private static final long RESEND_INTERVAL = 60000;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.login_verify;
    }

    @Override
    protected void initViews() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        emailInput = findViewById(R.id.editTextVerifyEmail);
        sendCode = findViewById(R.id.VerifyButtonVerify);
        maskedEmailTextView = findViewById(R.id.VerifyTextViewMaskedEmail);
        homePage = findViewById(R.id.VerifyTextViewBacktoHomePage);

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddr = emailInput.getText().toString().trim();
                if (emailAddr.isEmpty()) {
                    Toast.makeText(VerifyActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                maskedEmailTextView.setText(maskEmail(emailAddr));

                FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddr)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(VerifyActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                                startCountdownTimer();
                            } else {
                                Toast.makeText(VerifyActivity.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerifyActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void startCountdownTimer() { // Thecountdown timer
        sendCode.setEnabled(false);
        sendCode.setBackgroundColor(Color.GRAY);
        new CountDownTimer(RESEND_INTERVAL, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                sendCode.setText("Resend in " + secondsLeft + " seconds");
            }
            @Override
            public void onFinish() {
                sendCode.setEnabled(true);
                sendCode.setText("Send Link to your email");
                sendCode.setBackgroundResource(R.drawable.login);
            }
        }.start();
    }

    private String maskEmail(String email) { //mask the email
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        String maskedName = name.length() > 1 ? name.charAt(0) + "••••" : name;
        String maskedDomain;
        int dotIndex = domain.lastIndexOf('.');
        if (dotIndex > 1) {
            maskedDomain = domain.charAt(0) + "••••" + domain.substring(dotIndex);
        } else {
            maskedDomain = domain;
        }
        return maskedName + "@" + maskedDomain;
    }
}