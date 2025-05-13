package com.example.spikedash_singleplayer;

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
    Intent intent;
    User user;
    String pointsAmount;
    List<WheelItem> wheelItemList = new ArrayList<>();
    String points;
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
        intent = getIntent();
        btnReturnMenu.setOnClickListener(this);
        spinButton.setOnClickListener(this);
        tvTimer = findViewById(R.id.tvTimer);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.child("lastSpinTime").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                long lastSpinTime = snapshot.getValue(Long.class);
                long timeSinceLastSpin = System.currentTimeMillis() - lastSpinTime;

                if (timeSinceLastSpin < TWENTY_FOUR_HOURS_MS) {
                    startCooldown(TWENTY_FOUR_HOURS_MS - timeSinceLastSpin);
                } else {
                    spinButton.setVisibility(View.VISIBLE);
                    tvTimer.setVisibility(View.INVISIBLE);
                }
            } else {
                spinButton.setEnabled(true);
                tvTimer.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(e -> {
            spinButton.setEnabled(true);
            tvTimer.setVisibility(View.INVISIBLE);
        });

    WheelItem gift1 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.blue, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy),"0");
WheelItem gift2 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.orange, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "10");
WheelItem gift3 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.blue, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "50");
WheelItem gift4 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.orange, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "100");
WheelItem gift5 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.blue, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "250");
WheelItem gift6 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.orange, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "500");

        wheelItemList.add(gift1);
        wheelItemList.add(gift2);
        wheelItemList.add(gift3);
        wheelItemList.add(gift4);
        wheelItemList.add(gift5);
        wheelItemList.add(gift6);

        luckyWheel.addWheelItems(wheelItemList);
        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
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

    private void startCooldown(long millisUntilFinished) {
        tvTimer.setVisibility(View.VISIBLE);
        spinButton.setVisibility(View.INVISIBLE);

        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        countdownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long hours = seconds / 3600;
                long minutes = (seconds % 3600) / 60;
                long secs = seconds % 60;

                String time = String.format("%02d:%02d:%02d", hours, minutes, secs);
                tvTimer.setText("COME BACK IN: " + time);
            }

            @Override
            public void onFinish() {
                tvTimer.setVisibility(View.INVISIBLE);
                spinButton.setVisibility(View.VISIBLE);
            }
        };

        countdownTimer.start();
    }


    @Override
    public void onClick(View v) {
        if(v == spinButton) {
            btnReturnMenu.setVisibility(View.INVISIBLE);
            spinButton.setVisibility(View.INVISIBLE);
            Random random = new Random();
            points = String.valueOf(random.nextInt(10));
            luckyWheel.rotateWheelTo(Integer.parseInt(points));

            spinButton.setVisibility(View.INVISIBLE);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.child("lastSpinTime").setValue(System.currentTimeMillis());

        }
        else if (v == btnReturnMenu){
            Intent intent = new Intent(GiftActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}