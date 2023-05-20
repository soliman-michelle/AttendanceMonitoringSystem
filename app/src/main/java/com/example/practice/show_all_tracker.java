package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class show_all_tracker extends AppCompatActivity {
    private TextView subjectTextView;
    private DatabaseReference attendanceRef;
    private RecyclerView studentRecyclerView;
    private StudentAdapters studentAdapter;
    private List<studentTrack> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_tracker);

        studentRecyclerView = findViewById(R.id.studentRecyclerView);
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectTextView = findViewById(R.id.subjectName);

        students = new ArrayList<>();
        studentAdapter = new StudentAdapters(students);
        studentRecyclerView.setAdapter(studentAdapter);
        TableLayout attendanceTable = findViewById(R.id.attendanceTable);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();

        Intent intent = getIntent();
        String userid = intent.getStringExtra("useruid");
        String selectedSubject = intent.getStringExtra("subject");

        subjectTextView.setText(selectedSubject);
        attendanceRef = FirebaseDatabase.getInstance().getReference().child("studentAT").child(userid).child(selectedSubject);

        attendanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                students.clear();
                int totalSessions = 0;
                int presentSessions = 0;
                // Create the header row
                TableRow headerRow = new TableRow(show_all_tracker.this);

                TextView dateHeader = new TextView(show_all_tracker.this);
                dateHeader.setText("Date");
                dateHeader.setPadding(10, 10, 10, 10);
                dateHeader.setTypeface(null, Typeface.BOLD);
                headerRow.addView(dateHeader);

                TextView timeHeader = new TextView(show_all_tracker.this);
                timeHeader.setText("Time");
                timeHeader.setPadding(10, 10, 10, 10);
                timeHeader.setTypeface(null, Typeface.BOLD);
                headerRow.addView(timeHeader);

                TextView statusHeader = new TextView(show_all_tracker.this);
                statusHeader.setText("Status");
                statusHeader.setPadding(10, 10, 10, 10);
                statusHeader.setTypeface(null, Typeface.BOLD);
                headerRow.addView(statusHeader);

                attendanceTable.addView(headerRow);

                // Add data rows
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();

                    for (DataSnapshot studentSnapshot : dateSnapshot.getChildren()) {
                        String name = studentSnapshot.child("name").getValue(String.class);
                        String studNum = studentSnapshot.child("studNum").getValue(String.class);
                        String status = studentSnapshot.child("status").getValue(String.class);
                        String arrival = studentSnapshot.child("arrival").getValue(String.class);

                        studentTrack student = new studentTrack(name, studNum, status, arrival);
                        students.add(student);

                        TableRow tableRow = new TableRow(show_all_tracker.this);

                        TextView dateTextView = new TextView(show_all_tracker.this);
                        dateTextView.setText(date);
                        dateTextView.setPadding(10, 10, 10, 10);

                        TextView timeTextView = new TextView(show_all_tracker.this);
                        timeTextView.setText(arrival);
                        timeTextView.setPadding(10, 10, 10, 10);

                        TextView statusTextView = new TextView(show_all_tracker.this);
                        if (status.equals("Present")) {
                            statusTextView.setText(status);
                        } else {
                            statusTextView.setText("Absent");
                        }
                        statusTextView.setPadding(10, 10, 10, 10);

                        tableRow.addView(dateTextView);
                        tableRow.addView(timeTextView);
                        tableRow.addView(statusTextView);

                        attendanceTable.addView(tableRow);
                        totalSessions++;
                        if (status.equals("Present")) {
                            presentSessions++;
                        }
                    }
                }

                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}
