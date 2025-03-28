package com.example.spikedash_singleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBack;
    User user;
    TextView tvEmail;
    EditText etUsername;
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        user = getIntent().getParcelableExtra("user");
        etUsername = findViewById(R.id.editUsername);
        tvEmail = findViewById(R.id.etEmail);
        btnConfirm = findViewById(R.id.btnConfirmChanges);
        btnConfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == btnBack) {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if (v == btnConfirm){
            String updatedUsername = user.username;

            if (!updatedUsername.isEmpty()) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(firebaseUser.getUid());

                    userRef.child("username").setValue(updatedUsername)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                                // Return to main activity
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Failed
                                Toast.makeText(ProfileActivity.this, "Failed to update profile: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(ProfileActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }

    }
}