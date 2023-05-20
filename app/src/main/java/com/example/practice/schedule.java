package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.practice.databinding.ActivityScheduleBinding;
import com.example.practice.databinding.ActivityTrackerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class schedule extends AppCompatActivity {
    List<studentSched> schedList;
    DatabaseReference studentDbRef, ref, sub, student;
    TextView name, sections, programs, years;
    FirebaseUser user;

    ActivityScheduleBinding activityScheduleBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityScheduleBinding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(activityScheduleBinding.getRoot());

        schedList = new ArrayList<>();
        name = findViewById(R.id.name);
        sections = findViewById(R.id.section);
        programs = findViewById(R.id.program);
        years = findViewById(R.id.year);
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        student = FirebaseDatabase.getInstance().getReference("StudentAcc");
        sub = FirebaseDatabase.getInstance().getReference("subjectList");
        ref = FirebaseDatabase.getInstance().getReference("profiledb").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstname = dataSnapshot.child("fname").getValue(String.class);
                    String middlename = dataSnapshot.child("mname").getValue(String.class);
                    String lastname = dataSnapshot.child("lname").getValue(String.class);
                    String fullName = firstname + " " + middlename + " " + lastname;
                    String program = dataSnapshot.child("program").getValue(String.class);
                    String year = dataSnapshot.child("year").getValue(String.class);
                    String section = dataSnapshot.child("block").getValue(String.class);

                    name.setText(fullName);
                    programs.setText(program);
                    years.setText(year);
                    sections.setText(section);

                    // Retrieve data from "subjectList" based on program, year, and section
                    DatabaseReference subjectListRef = sub.child(program).child(year).child(section);
                    subjectListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                tableLayout.removeAllViews(); // Clear the existing rows in the TableLayout

                                // Create the header row
                                TableRow headerRow = new TableRow(schedule.this);

                                // Create TextViews for each column header
                                TextView dayHeaderTextView = new TextView(schedule.this);
                                TextView timeHeaderTextView = new TextView(schedule.this);
                                TextView subjectHeaderTextView = new TextView(schedule.this);
                                TextView roomHeaderTextView = new TextView(schedule.this);

                                // Set the text for each column header
                                dayHeaderTextView.setText("Day");
                                timeHeaderTextView.setText("Time");
                                subjectHeaderTextView.setText("Subject");
                                roomHeaderTextView.setText("Room");

                                // Add the column header TextViews to the header row
                                headerRow.addView(dayHeaderTextView);
                                headerRow.addView(timeHeaderTextView);
                                headerRow.addView(subjectHeaderTextView);
                                headerRow.addView(roomHeaderTextView);

                                // Add the header row to the TableLayout
                                tableLayout.addView(headerRow);

                                for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                                    for (DataSnapshot daySnapshot : subjectSnapshot.getChildren()) {
                                        String day = daySnapshot.child("day").getValue(String.class);
                                        String room = daySnapshot.child("room").getValue(String.class);
                                        String subject = daySnapshot.child("subject").getValue(String.class);
                                        String time = daySnapshot.child("time").getValue(String.class);

                                        TableRow tableRow = new TableRow(schedule.this);

                                        // Create TextViews for each column
                                        TextView dayTextView = new TextView(schedule.this);
                                        TextView timeTextView = new TextView(schedule.this);
                                        TextView subjectTextView = new TextView(schedule.this);
                                        TextView roomTextView = new TextView(schedule.this);

                                        // Set the text for each TextView
                                        dayTextView.setText(day);
                                        timeTextView.setText(time);
                                        subjectTextView.setText(subject);
                                        roomTextView.setText(room);

                                        // Add the TextViews to the TableRow
                                        tableRow.addView(dayTextView);
                                        tableRow.addView(timeTextView);
                                        tableRow.addView(subjectTextView);
                                        tableRow.addView(roomTextView);

                                        // Add the TableRow to the TableLayout
                                        tableLayout.addView(tableRow);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("DATABASE", "Error retrieving data from the database", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
