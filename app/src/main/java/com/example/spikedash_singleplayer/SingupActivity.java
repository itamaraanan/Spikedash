package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingupActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText etEmail, etPassword, etConfirmPassword, etUsername;
    LinearLayout btnSingup;
    ImageButton btnBack, btnShowPassword, btnShowConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnShowPassword = findViewById(R.id.btnShowPassword);
        btnShowConfirmPassword = findViewById(R.id.btnShowConfirmPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etUsername = findViewById(R.id.etUsername);
        btnSingup = findViewById(R.id.btnSignUp);
        btnBack = findViewById(R.id.btnBack);
        btnSingup.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnShowPassword.setOnClickListener(this);
        btnShowConfirmPassword.setOnClickListener(this);
    }

    private void createAccount(String email, String password, String username) {
        DatabaseReference usernamesRef = FirebaseDatabase.getInstance().getReference("usernames");

        usernamesRef.child(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Toast.makeText(this, "Username already taken. Try another one.", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, authTask -> {
                                if (authTask.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    writeNewUser(user.getUid(), username, email);
                                    usernamesRef.child(username).setValue(user.getUid());
                                    startActivity(new Intent(this, MainActivity.class));
                                } else {
                                    Toast.makeText(this, "Signup failed: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Error checking username.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeNewUser(String userId, String username, String email) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        User user = new User(username, email, userId, userRef.getKey());
        userRef.setValue(user);
    }


    @Override
    public void onClick(View v) {
        if (v == btnSingup){
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            String username = etUsername.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            createAccount(email, password, username);
        }
        else if(v == btnBack){
            Intent intent = new Intent(SingupActivity.this, MenuActivity.class);
            startActivity(intent);
        }
        else if(v == btnShowPassword) {
            if (etPassword.getTransformationMethod() == null) {
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                btnShowPassword.setImageResource(R.drawable.ic_closed_eye_24);
            } else {
                etPassword.setTransformationMethod(null);
                btnShowPassword.setImageResource(R.drawable.ic_opened_eye_24);
            }
        }

        else if(v == btnShowConfirmPassword) {
            if (etConfirmPassword.getTransformationMethod() == null) {
                etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                btnShowConfirmPassword.setImageResource(R.drawable.ic_closed_eye_24);
            } else {
                etConfirmPassword.setTransformationMethod(null);
                btnShowConfirmPassword.setImageResource(R.drawable.ic_opened_eye_24);
            }
        }

    }
}