package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.practice.databinding.ActivityProfHomeBinding;


public class ProfHome extends profdrawable implements View.OnClickListener {
    ActivityProfHomeBinding activityProfHomeBinding;
    public CardView cv1, cv2, cv3, cv4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfHomeBinding =ActivityProfHomeBinding.inflate(getLayoutInflater());
        setContentView(activityProfHomeBinding.getRoot());
        allocateActivityTitle("Home");
        cv1 = (CardView) findViewById(R.id.attendanceTracker);
        cv2 = (CardView) findViewById(R.id.qr_generator);
        cv3 = (CardView) findViewById(R.id.attendanceSchedule);
        cv4 = (CardView) findViewById(R.id.profile);

        cv1.setOnClickListener(this);
        cv2.setOnClickListener(this);
        cv3.setOnClickListener(this);
        cv4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()){
            case R.id.attendanceTracker:
                i = new Intent(this, proftracker.class);
                startActivity(i);
                break;
            case R.id.qr_generator:
                i = new Intent(this, qr_generator.class);
                startActivity(i);
                break;
            case R.id.attendanceSchedule:
                i = new Intent(this, profsched.class);
                startActivity(i);
                break;
            case R.id.profile:
                i = new Intent(this, profprofile.class);
                startActivity(i);
                break;
        }

    }
    public void onBackPressed(){
        //super.onBackPressed();
    }
}