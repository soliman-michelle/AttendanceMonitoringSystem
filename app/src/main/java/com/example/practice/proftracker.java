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
        private String selectedSubject;
        private String selectedClassId;
        private String selectedDate;
        private String selectedTerm;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            activityProftrackerBinding = ActivityProftrackerBinding.inflate(getLayoutInflater());
            setContentView(activityProftrackerBinding.getRoot());
            allocateActivityTitle("Attendance Tracker");

            Spinner classIdSpinner = findViewById(R.id.classid);
            Spinner subjectSpinner = findViewById(R.id.subject);
            Spinner dateSpinner = findViewById(R.id.date);
            Spinner termSpinner = findViewById(R.id.term);
            show = findViewById(R.id.show);

            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the selected values from the spinners
                    String selectedSubject = subjectSpinner.getSelectedItem().toString();
                    String selectedClassId = classIdSpinner.getSelectedItem().toString();
                    String selectedDate = dateSpinner.getSelectedItem().toString();
                    String selectedTerm = termSpinner.getSelectedItem().toString();

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    String professorId = user.getUid();

                    DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("profTracker");

                    // Query the attendance data based on the selected values
                    attendanceRef.child(professorId).child(selectedSubject).child(selectedClassId).child(selectedTerm).child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                List<String> attendanceDataList = new ArrayList<>();

                                // Loop through the dataSnapshot's children to update attendance data
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
                                intent.putExtra("Date", selectedDate);
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
// ...

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
                                        List<String> dates = new ArrayList<>();

                                        // Loop through the dataSnapshot to extract subjects, classIds, and dates
                                        for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                                            String subject = subjectSnapshot.getKey();
                                            subjects.add(subject);

                                            for (DataSnapshot classIdSnapshot : subjectSnapshot.getChildren()) {
                                                String classId = classIdSnapshot.getKey();
                                                classIds.add(classId);

                                                for (DataSnapshot termSnapshot : classIdSnapshot.getChildren()) {
                                                    String term = termSnapshot.getKey();
                                                    terms.add(term);

                                                    for (DataSnapshot dateSnapshot : termSnapshot.getChildren()) {
                                                        String date = dateSnapshot.getKey();
                                                        dates.add(date);

                                                        for (DataSnapshot studentSnapshot : dateSnapshot.getChildren()) {
                                                            String studentUid = studentSnapshot.getKey();

                                                            // Now you have access to the student UID and can perform further operations
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Create ArrayAdapter and set it as the adapter for subjectSpinner
                                        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(proftracker.this, android.R.layout.simple_spinner_item, subjects);
                                        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        subjectSpinner.setAdapter(subjectAdapter);

                                        ArrayAdapter<String> classIdAdapter = new ArrayAdapter<>(proftracker.this, android.R.layout.simple_spinner_item, classIds);
                                        classIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        classIdSpinner.setAdapter(classIdAdapter);

                                        ArrayAdapter<String> termAdapter = new ArrayAdapter<>(proftracker.this, android.R.layout.simple_spinner_item, terms);
                                        classIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        termSpinner.setAdapter(termAdapter);
                                        // Create ArrayAdapter and set it as the adapter for dateSpinner
                                        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(proftracker.this, android.R.layout.simple_spinner_item, dates);
                                        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        dateSpinner.setAdapter(dateAdapter);

                                        // Insert the code snippet here
                                        // Insert the code snippet here
                                        if (selectedSubject != null && selectedSubject.equals(subjects.get(0))
                                                && selectedClassId != null && selectedClassId.equals(classIds.get(0))
                                                && selectedTerm != null && selectedTerm.equals(terms.get(0))
                                                && selectedDate != null && selectedDate.equals(dates.get(0))) {
                                            // Retrieve attendance data for the selected values and update the ListView
                                            retrieveAttendanceData(userUid, selectedSubject, selectedClassId, selectedTerm, selectedDate);
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
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });


        }
        // ...

        // Insert the code snippet here
        private void retrieveAttendanceData(String userUid, String subject, String classId, String term, String date) {
            DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("profTracker")
                    .child(userUid).child(subject).child(classId).child(term).child(date);

            attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Process the attendance data
                        for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                            // Retrieve the attendance information for each student
                            String studentUid = studentSnapshot.getKey();
                            String attendanceStatus = studentSnapshot.child("status").getValue(String.class);

                            // Perform any necessary operations with the attendance data
                            // ...
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }

// ...

    }
