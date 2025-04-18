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
        etUsername.setText(user.getUsername());
        tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText(user.email);
        btnConfirm = findViewById(R.id.btnConfirmChanges);
        btnConfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == btnBack) {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if (v == btnConfirm) {
            String newUsername = etUsername.getText().toString().trim();

            if (newUsername.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newUsername.equals(user.username)) {
                Toast.makeText(ProfileActivity.this, "No changes made", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid());

            userRef.child("username").setValue(newUsername)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.username = newUsername;

                            Toast.makeText(ProfileActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}