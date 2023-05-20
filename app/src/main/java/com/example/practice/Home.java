package com.example.practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.example.practice.databinding.ActivityDrawableBinding;
import com.example.practice.databinding.ActivityHomeBinding;

public class Home extends Drawable implements View.OnClickListener {

    ActivityHomeBinding activityHomeBinding;
    public CardView cv1, cv2, cv3, cv4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());
        allocateActivityTitle("Home");

        cv1 = (CardView) findViewById(R.id.attendanceTracker);
        cv2 = (CardView) findViewById(R.id.attendanceScanner);
        cv3 = (CardView) findViewById(R.id.attendanceSchedule);
        cv4 = (CardView) findViewById(R.id.profile);

        cv1.setOnClickListener(this);
        cv2.setOnClickListener(this);
        cv3.setOnClickListener(this);
        cv4.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()){
            case R.id.attendanceTracker:
                i = new Intent(this, tracker.class);
                startActivity(i);
                break;
            case R.id.attendanceScanner:
                i = new Intent(this, scanner.class);
                startActivity(i);
                break;
            case R.id.attendanceSchedule:
                i = new Intent(this, schedule.class);
                startActivity(i);
                break;
            case R.id.profile:
                i = new Intent(this, Profile.class);
                startActivity(i);
                break;
        }
    }
    public void onBackPressed(){
        //super.onBackPressed();
    }
}