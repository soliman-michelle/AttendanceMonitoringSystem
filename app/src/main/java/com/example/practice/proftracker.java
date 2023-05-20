package com.example.practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import com.example.practice.databinding.ActivityProftrackerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class proftracker extends profdrawable {
    ActivityProftrackerBinding activityProftrackerBinding;
    Button show;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProftrackerBinding = ActivityProftrackerBinding.inflate(getLayoutInflater());
        setContentView(activityProftrackerBinding.getRoot());
        allocateActivityTitle("Attendance Tracker");

        Spinner subjectSpinner = findViewById(R.id.subject);
        Spinner classIdSpinner = findViewById(R.id.classid);
        Spinner termSpinner = findViewById(R.id.term);
        show = findViewById(R.id.show);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected values from the spinners
                String selectedSubject = subjectSpinner.getSelectedItem().toString();
                String selectedClassId = classIdSpinner.getSelectedItem().toString();
                String selectedTerm = termSpinner.getSelectedItem().toString();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                String professorId = user.getUid();

                DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("profTracker");

                // Query the attendance data based on the selected values
                attendanceRef.child(professorId).child(selectedSubject).child(selectedClassId).child(selectedTerm).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            Intent intent = new Intent(proftracker.this, showAllStudentAttendance.class);
                            intent.putExtra("professorId", professorId);
                            intent.putExtra("subject", selectedSubject);
                            intent.putExtra("classId", selectedClassId);
                            intent.putExtra("term", selectedTerm);
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


        // Assuming you have a database reference to the node containing user UIDs
        DatabaseReference userUidsRef = FirebaseDatabase.getInstance().getReference().child("profTracker");

        // Fetch the user UIDs from the database
        userUidsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> userUids = new ArrayList<>();

                    // Loop through the dataSnapshot to extract the user UIDs
                    for (DataSnapshot uidSnapshot : dataSnapshot.getChildren()) {
                        String uid = uidSnapshot.getKey();
                        userUids.add(uid);
                    }

                    // Proceed with the rest of the code
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("profTracker");

                    // Loop through the user UIDs and retrieve data for each user
                    for (String userUid : userUids) {
                        // Query the "profTracker" node using the current user UID
                        databaseReference.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    List<String> subjects = new ArrayList<>();
                                    List<String> classIds = new ArrayList<>();
                                    List<String> terms = new ArrayList<>();

                                    // Loop through the dataSnapshot to extract subjects, classIds, and terms
                                    for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                                        String subject = subjectSnapshot.getKey();
                                        subjects.add(subject);

                                        for (DataSnapshot classIdSnapshot : subjectSnapshot.getChildren()) {
                                            String classId = classIdSnapshot.getKey();
                                            classIds.add(classId);

                                            for (DataSnapshot termSnapshot : classIdSnapshot.getChildren()) {
                                                String term = termSnapshot.getKey();
                                                terms.add(term);
                                            }
                                        }
                                    }

                                    // Create ArrayAdapter and set it as the adapter for subjectSpinner
                                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(proftracker.this, android.R.layout.simple_spinner_item, subjects);
                                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    subjectSpinner.setAdapter(subjectAdapter);

                                    // Create ArrayAdapter and set it as the adapter for classIdSpinner
                                    ArrayAdapter<String> classIdAdapter = new ArrayAdapter<>(proftracker.this, android.R.layout.simple_spinner_item, classIds);
                                    classIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    classIdSpinner.setAdapter(classIdAdapter);

                                    // Create ArrayAdapter and set it as the adapter for termSpinner
                                    ArrayAdapter<String> termAdapter = new ArrayAdapter<>(proftracker.this, android.R.layout.simple_spinner_item, terms);
                                    termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    termSpinner.setAdapter(termAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle any errors
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });

    }
}
