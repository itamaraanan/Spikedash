package com.example.spikedash_singleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBack;
    ImageButton btnShowPassword;
    TextView tvPassword;
    int clicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnBack = findViewById(R.id.btnBack);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        tvPassword = findViewById(R.id.tvPassword);
        btnBack.setOnClickListener(this);
        btnShowPassword.setOnClickListener(this);
        clicks =0;
    }

    @Override
    public void onClick(View v) {
        if(v == btnBack) {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if (v == btnShowPassword) {
            clicks++;
            btnShowPassword.setImageResource(
                (clicks % 2 == 0) ? R.drawable.ic_opened_eye_24 : R.drawable.ic_closed_eye_24
            );
            if(clicks%2 ==0){
                tvPassword.setText("••••••••");
            }
            else{
                tvPassword.setText("password");
            }
        }
    }
}