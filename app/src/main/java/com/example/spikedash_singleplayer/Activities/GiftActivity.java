package com.example.spikedash_singleplayer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.SoundManager;
import com.example.spikedash_singleplayer.VibrationManager;
import com.example.spikedash_singleplayer.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiftActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTimer;
    Button spinButton;
    ImageButton btnReturnMenu;
    LuckyWheel luckyWheel;
    User user;
    String pointsAmount;
    List<WheelItem> wheelItemList;
    String points;
    DatabaseReference userRef;
    CountDownTimer countdownTimer;
    final long TWENTY_FOUR_HOURS_MS = 24 * 60 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getIntent().getParcelableExtra("user");
        setContentView(R.layout.activity_gift);
        luckyWheel = findViewById(R.id.luckyWheel);
        spinButton = findViewById(R.id.spinButton);
        btnReturnMenu = findViewById(R.id.btnRturnMenu);
        btnReturnMenu.setOnClickListener(this);
        spinButton.setOnClickListener(this);
        tvTimer = findViewById(R.id.tvTimer);
        userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        setupWheelItems();
        checkSpinCooldown();
        configureWheelListener();


    }
    private void setupWheelItems() {
        wheelItemList = new ArrayList<>();
        WheelItem gift1 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.blue, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy),"0");
        WheelItem gift2 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.orange, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "25");
        WheelItem gift3 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.blue, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "50");
        WheelItem gift4 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.orange, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "100");
        WheelItem gift5 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.blue, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "500");
        WheelItem gift6 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.orange, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "1000");

        wheelItemList.add(gift1);
        wheelItemList.add(gift2);
        wheelItemList.add(gift3);
        wheelItemList.add(gift4);
        wheelItemList.add(gift5);
        wheelItemList.add(gift6);

        luckyWheel.addWheelItems(wheelItemList);
    }
    private void configureWheelListener() {
        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
                VibrationManager.vibrate(GiftActivity.this  , 200);
                SoundManager.play("win");

                WheelItem itemSelected = wheelItemList.get(Integer.parseInt(points)-1);
                pointsAmount = itemSelected.text;
                Toast.makeText(GiftActivity.this, "You won " + pointsAmount + " points!", Toast.LENGTH_SHORT).show();
                btnReturnMenu.setVisibility(View.VISIBLE);
                spinButton.setVisibility(View.INVISIBLE);
                startCooldown(TWENTY_FOUR_HOURS_MS);

                if(pointsAmount != null) {
                    user.add(Integer.parseInt(pointsAmount));
                    userRef.child("balance").setValue(user.getBalance());
                }
            }
        });
    }
    private void checkSpinCooldown() {
        //try to get the last spin time from the database
        userRef.child("lastSpinTime").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // If the last spin time exists, check if it's within the cooldown period
                long lastSpinTime = snapshot.getValue(Long.class);
                long timeSinceLastSpin = System.currentTimeMillis() - lastSpinTime;

                if (timeSinceLastSpin < TWENTY_FOUR_HOURS_MS) {
                    // If the cooldown period is still active, disable the spin button and start the countdown
                    startCooldown(TWENTY_FOUR_HOURS_MS - timeSinceLastSpin);
                } else {
                    // If the cooldown period has passed, enable the spin button
                    spinButton.setVisibility(View.VISIBLE);
                    tvTimer.setVisibility(View.INVISIBLE);
                }
            } else {
                // If the user's first spin enable the spin button
                spinButton.setEnabled(true);
                tvTimer.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(e -> {
            // If there was an error retrieving the last spin time, log the error and enable the spin button
            SoundManager.play("error");
            spinButton.setEnabled(true);
            tvTimer.setVisibility(View.INVISIBLE);
        });
    }

    private void startCooldown(long millisUntilFinished) {
        // Start the countdown timer for the cooldown period
        tvTimer.setVisibility(View.VISIBLE);
        spinButton.setVisibility(View.INVISIBLE);

        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        // Create a new CountDownTimer instance
        countdownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            // This method is called every second
            public void onTick(long millisUntilFinished) {
                // Calculate the remaining time in hours, minutes, and seconds
                long seconds = millisUntilFinished / 1000;
                long hours = seconds / 3600;
                long minutes = (seconds % 3600) / 60;
                long secs = seconds % 60;

                // Format the time and update the TextView
                String time = String.format("%02d:%02d:%02d", hours, minutes, secs);
                tvTimer.setText("COME BACK IN: " + time);
            }

            @Override
            public void onFinish() {
                // When the countdown finishes, enable the spin button and hide the timer
                tvTimer.setVisibility(View.INVISIBLE);
                spinButton.setVisibility(View.VISIBLE);
            }
        };
        // Start the countdown timer
        countdownTimer.start();
    }


    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 100);
        if(v == spinButton) {
            SoundManager.play("spin");
            btnReturnMenu.setVisibility(View.INVISIBLE);
            spinButton.setVisibility(View.INVISIBLE);
            // Generate a random index for the wheel item
            Random random = new Random();
            int index = random.nextInt(wheelItemList.size());
            points = String.valueOf(index + 1);
            // Rotate the wheel to the selected item
            luckyWheel.rotateWheelTo(index + 1);


            spinButton.setVisibility(View.INVISIBLE);
            userRef.child("lastSpinTime").setValue(System.currentTimeMillis());

        }
        else if (v == btnReturnMenu){
            SoundManager.play("click");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newBalance", user.getBalance());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}