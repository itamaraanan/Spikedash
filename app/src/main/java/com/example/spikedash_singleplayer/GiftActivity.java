package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiftActivity extends AppCompatActivity implements View.OnClickListener {


    Button spinButton;
    ImageButton btnReturnMenu;
    LuckyWheel luckyWheel;
    List<WheelItem> wheelItemList = new ArrayList<>();
    String points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        luckyWheel = findViewById(R.id.luckyWheel);
        spinButton = findViewById(R.id.spinButton);
        btnReturnMenu = findViewById(R.id.btnRturnMenu);
        btnReturnMenu.setOnClickListener(this);
        spinButton.setOnClickListener(this);

    WheelItem gift1 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.blue, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_random_skin));
WheelItem gift2 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.orange, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "10");
WheelItem gift3 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.blue, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "20");
WheelItem gift4 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.orange, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_random_background));
WheelItem gift5 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.blue, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "40");
WheelItem gift6 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.orange, null), BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy), "50");

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
                String pointsAmount = itemSelected.text;
                Toast.makeText(GiftActivity.this, "You won " + pointsAmount + " points!", Toast.LENGTH_SHORT).show();
                btnReturnMenu.setVisibility(View.VISIBLE);
                spinButton.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == spinButton) {
            btnReturnMenu.setVisibility(View.INVISIBLE);
            spinButton.setVisibility(View.INVISIBLE);
            Random random = new Random();
            points = String.valueOf(random.nextInt(10));
            luckyWheel.rotateWheelTo(Integer.parseInt(points));
        }
        else if (v == btnReturnMenu){
            Intent intent = new Intent(GiftActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}