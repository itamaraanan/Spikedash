package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout btnLogin, btnSingup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnLogin = findViewById(R.id.btnLogin);
        btnSingup = findViewById(R.id.btnSignUp);
        btnLogin.setOnClickListener(this);
        btnSingup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {
            Intent intent = new Intent(MenuActivity.this, LoginActicvity.class);
            startActivity(intent);
        }

        if (v == btnSingup) {
            Intent intent = new Intent(MenuActivity.this, SingupActivity.class);
            startActivity(intent);
        }
    }
}