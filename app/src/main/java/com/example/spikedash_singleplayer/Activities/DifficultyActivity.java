package com.example.spikedash_singleplayer.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.User;
import com.example.spikedash_singleplayer.Managers.VibrationManager;
import com.google.firebase.database.FirebaseDatabase;

public class DifficultyActivity extends AppCompatActivity implements View.OnClickListener {
    // Difficulty buttons
    Button easyButton, mediumButton, hardButton, insaneButton, returnButton;
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
    private void setDifficulty(float multiplier) {
        // Update the user's difficulty multiplier in the database
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("difficultyMultiplier")
                .setValue(multiplier);

        SoundManager.play("win");
        Toast.makeText(this, "Difficulty set successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        SoundManager.play("click");
        if (v == easyButton) {
            setDifficulty(0.75f);
        } else if (v == mediumButton) {
            setDifficulty(1.0f);
        } else if (v == hardButton) {
            setDifficulty(1.25f);
        } else if (v == insaneButton) {
            setDifficulty(1.75f);
        }
        SoundManager.play("win");
        Toast.makeText(this, "Difficulty set successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}