package com.example.spikedash_singleplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
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
    ImageButton btnShowPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnShowPassword = findViewById(R.id.btnShowPassword);
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
        btnShowPassword.setOnClickListener(this);

    }

    private void login(String email, String password, Dialog progressDialog) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActicvity.this, "Login succeeded.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActicvity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = "Login failed";
                            if (task.getException() != null) {
                                errorMessage += ": " + task.getException().getMessage();
                            }
                            Toast.makeText(LoginActicvity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == btnLogin){
            if (etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            Dialog progressDialog = new Dialog(this);
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
            if (tvMessage != null) {
                tvMessage.setText("Logging in...");
            }
            progressDialog.show();
            login(etEmail.getText().toString(), etPassword.getText().toString(), progressDialog);
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
        if(v == btnShowPassword){
            if (etPassword.getTransformationMethod() == null) {
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                btnShowPassword.setImageResource(R.drawable.ic_closed_eye_24);
            } else {
                etPassword.setTransformationMethod(null);
                btnShowPassword.setImageResource(R.drawable.ic_opened_eye_24);
            }
        }
    }
}