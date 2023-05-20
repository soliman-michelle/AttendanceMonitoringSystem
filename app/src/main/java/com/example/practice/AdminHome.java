package com.example.practice;

import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.practice.databinding.ActivityAdminHomeBinding;

public class AdminHome extends AdminDrawable implements View.OnClickListener {
    public CardView cv1, cv2, cv3;
    ActivityAdminHomeBinding activityAdminHomeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminHomeBinding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(activityAdminHomeBinding.getRoot());
        allocateActivityTitle("Home");

        cv1 = (CardView) findViewById(R.id.attendanceTracker);
        cv2 = (CardView) findViewById(R.id.attendanceScanner);
        cv3 = (CardView) findViewById(R.id.attendanceSchedule);

        cv1.setOnClickListener(this);
        cv2.setOnClickListener(this);
        cv3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()){
            case R.id.attendanceTracker:
                i = new Intent(this, addStudent.class);
                startActivity(i);
                break;
            case R.id.attendanceScanner:
                i = new Intent(this, addProf.class);
                startActivity(i);
                break;
            case R.id.attendanceSchedule:
                i = new Intent(this, addSubject.class);
                startActivity(i);
                break;
        }
    }
    public void onBackPressed(){
        //super.onBackPressed();
    }
}