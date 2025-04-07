//package com.example.medassist.ui.login;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.CountDownTimer;
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
//
//
//public class VerifyActivity extends AppCompatActivity {
//
//    private EditText emailInput;
//    private Button sendCode;
//    private TextView maskedEmailTextView, homePage;
//    private static final long RESEND_INTERVAL = 60000;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.login_verify);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        emailInput = findViewById(R.id.editTextVerifyEmail);
//        sendCode = findViewById(R.id.VerifyButtonVerify);
//        maskedEmailTextView = findViewById(R.id.VerifyTextViewMaskedEmail);
//        homePage = findViewById(R.id.VerifyTextViewBacktoHomePage);
//
//        sendCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String emailAddr = emailInput.getText().toString().trim();
//                if (emailAddr.isEmpty()) {
//                    Toast.makeText(VerifyActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//
//                maskedEmailTextView.setText(maskEmail(emailAddr));
//
//                // Send the reset email to user
//                FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddr)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(VerifyActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
//                                startCountdownTimer();
//                            } else {
//                                Toast.makeText(VerifyActivity.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
//
//        homePage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Return to LoginActivity
//                startActivity(new Intent(VerifyActivity.this, LoginActivity.class));
//                finish();
//            }
//        });
//    }
//
//    private void startCountdownTimer() {// COOLDOWN FUNCTION BEFORE THE USER CAN PRESS THE BUTTON AGAIN
//        sendCode.setEnabled(false);
//        sendCode.setBackgroundColor(Color.GRAY);
//        new CountDownTimer(RESEND_INTERVAL, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                long secondsLeft = millisUntilFinished / 1000;
//                sendCode.setText("Resend in " + secondsLeft + " seconds");
//            }
//            @Override
//            public void onFinish() {
//                sendCode.setEnabled(true);
//                sendCode.setText("Send Link to your email");
//                sendCode.setBackgroundResource(R.drawable.login);
//            }
//        }.start();
//    }
//
//    // method to make "Enter your email" to the masked email.
//    private String maskEmail(String email) {
//        if (email == null || !email.contains("@")) {
//            return email;
//        }
//        String[] parts = email.split("@");
//        String name = parts[0];
//        String domain = parts[1];
//        String maskedName = name.length() > 1 ? name.charAt(0) + "••••" : name;
//        String maskedDomain;
//        int dotIndex = domain.lastIndexOf('.');
//        if (dotIndex > 1) {
//            maskedDomain = domain.charAt(0) + "••••" + domain.substring(dotIndex);
//        } else {
//            maskedDomain = domain;
//        }
//        return maskedName + "@" + maskedDomain;
//    }
//}
//
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

    private void startCountdownTimer() {
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

    // A helper method to mask the email address
    private String maskEmail(String email) {
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
