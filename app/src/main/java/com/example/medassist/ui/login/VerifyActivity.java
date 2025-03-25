package com.example.medassist.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.medassist.R;
public class VerifyActivity extends AppCompatActivity {
    EditText EnterCode;
    Button SendCode;
    TextView HomePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_verify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EnterCode = findViewById(R.id.editTextVerifyUsername);
        SendCode = findViewById(R.id.VerifyButtonVerify);
        HomePage = findViewById(R.id.VerifyTextViewBacktoHomePage);

        HomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerifyActivity.this, LoginActivity.class));
            }
        });
    }
}
