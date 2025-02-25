package com.example.spikedash_singleplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActicvity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText etEmail, etPassword;
    LinearLayout btnLogin;
    TextView btnGoToSignUp, btnForgotPassword;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);
        btnBack = findViewById(R.id.btnBack);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnLogin.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnGoToSignUp.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);

    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActicvity.this, "Login succeeded.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActicvity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActicvity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == btnLogin){
            login(etEmail.getText().toString(), etPassword.getText().toString());
        }
        if(v == btnGoToSignUp){
            Intent intent = new Intent(LoginActicvity.this, SingupActivity.class);
            startActivity(intent);
        }
        if(v == btnBack){
            Intent intent = new Intent(LoginActicvity.this, MenuActivity.class);
            startActivity(intent);
        }
        if(v == btnForgotPassword){
            Intent intent = new Intent(LoginActicvity.this, ForgotActivity.class);
            startActivity(intent);
        }
    }
}