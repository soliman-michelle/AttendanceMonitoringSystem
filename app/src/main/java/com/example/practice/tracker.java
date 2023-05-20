package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class tracker extends AppCompatActivity {
    private Spinner subjectSpinner;
    private DatabaseReference databaseReference;
    Button show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();

        subjectSpinner = findViewById(R.id.subject);
        show = findViewById(R.id.show);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected values from the spinners
                String selectedSubject = subjectSpinner.getSelectedItem().toString();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                String userid = user.getUid();

                DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("studentAT");

                // Query the attendance data based on the selected values
                attendanceRef.child(userid).child(selectedSubject).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            List<String> attendanceDataList = new ArrayList<>();

                            // Loop through the dataSnapshot to update attendance data
                            for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                                String attendanceStatus = studentSnapshot.child("status").getValue(String.class);
                                attendanceDataList.add(attendanceStatus);

                            }

                            // Pass the attendance data to the showAllStudentAttendance activity
                            Intent intent = new Intent(tracker.this, show_all_tracker.class);
                            intent.putExtra("useruid", userid);
                            intent.putExtra("subject", selectedSubject);

                            intent.putStringArrayListExtra("attendanceDataList", (ArrayList<String>) attendanceDataList);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors
                    }
                });
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("studentAT");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> subjects = new ArrayList<>();

                DataSnapshot userSnapshot = dataSnapshot.child(uid);
                if (userSnapshot.exists()) {
                    for (DataSnapshot subjectSnapshot : userSnapshot.getChildren()) {
                        subjects.add(subjectSnapshot.getKey());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(tracker.this, android.R.layout.simple_spinner_item, subjects);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}
