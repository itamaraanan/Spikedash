package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class DifficultyActivity extends AppCompatActivity implements View.OnClickListener {

    Button easyButton, mediumButton, hardButton, insaneButton, returnButton;

    Intent intent;
    User user;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        user = getIntent().getParcelableExtra("user");
        uid = user.getUid();

        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> {
            VibrationManager.vibrate(this, 25);
            SoundManager.play("click");
           finish();
        });
        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        hardButton = findViewById(R.id.hardButton);
        insaneButton = findViewById(R.id.insaneButton);
        easyButton.setOnClickListener(this);
        mediumButton.setOnClickListener(this);
        hardButton.setOnClickListener(this);
        insaneButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        SoundManager.play("click");
        if (v == easyButton) {
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("difficultyMultiplier")
                    .setValue(0.75f);
        } else if (v == mediumButton) {
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("difficultyMultiplier")
                    .setValue(1.0f);
        } else if (v == hardButton) {
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("difficultyMultiplier")
                    .setValue(1.5f);
        } else if (v == insaneButton) {
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("difficultyMultiplier")
                    .setValue(2f);
        }
        SoundManager.play("win");
        Toast.makeText(this, "Difficulty set successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}