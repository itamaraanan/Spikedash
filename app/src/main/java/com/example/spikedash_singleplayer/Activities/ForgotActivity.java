package com.example.spikedash_singleplayer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageView;

import com.example.spikedash_singleplayer.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    EditText etEmail;
    LinearLayout btnResetPassword;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        btnResetPassword.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void resetPassword() {
        // Get the email from the EditText and remove leading/trailing spaces
        String email = etEmail.getText().toString().trim();

        // Check if the email field is empty
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        // send the password reset email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    // Check if the task was successful
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotActivity.this,
                                "Password reset email sent. Check your inbox.",
                                Toast.LENGTH_LONG).show();

                        // Clear the email field and remove focus
                        etEmail.setText("");
                        etEmail.clearFocus();
                    } else {
                        // If the task failed, show an error message
                        Toast.makeText(ForgotActivity.this,
                                "Failed to send reset email: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == btnResetPassword) {
            resetPassword();
        }
        if (v == btnBack) {
            finish();
        }
    }
}