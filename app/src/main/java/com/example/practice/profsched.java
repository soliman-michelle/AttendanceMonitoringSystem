package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class profsched extends profdrawable {
    Spinner classid, subject;
    Button show;
    private DatabaseReference enrollSubRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profsched);
        allocateActivityTitle("Schedule");

        classid = findViewById(R.id.classId);
        subject = findViewById(R.id.subject);
        show = findViewById(R.id.show);

        enrollSubRef = FirebaseDatabase.getInstance().getReference("enrollSub");
        String professorUid  = getCurrentProfessorUid();
        if (professorUid != null) {
            populateSubjectSpinner(professorUid);
        }

        subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSubject = adapterView.getItemAtPosition(position).toString();
                populateClassIdsSpinner(professorUid, selectedSubject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedSubject = subject.getSelectedItem().toString();
                String selectedClassId = classid.getSelectedItem().toString();
                if (selectedSubject.isEmpty() || selectedClassId.isEmpty()) {
                    Toast.makeText(profsched.this, "Please select a subject and class ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference studentAccRef = enrollSubRef
                        .child(professorUid)
                        .child(selectedSubject)
                        .child(selectedClassId);

                studentAccRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            List<Student> studentList = new ArrayList<>();

                            for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                                String uid = studentSnapshot.getKey();
                                String name = studentSnapshot.child("name").getValue(String.class);
                                if (name != null) {
                                    Student student = new Student(name, uid);
                                    studentList.add(student);
                                }
                            }

                            if (!studentList.isEmpty()) {
                                Intent intent = new Intent(profsched.this, masterList.class);
                                intent.putParcelableArrayListExtra("studentList", new ArrayList<>(studentList));
                                intent.putExtra("subject", selectedSubject);
                                intent.putExtra("classId", selectedClassId);
                                startActivity(intent);
                            } else {
                                Toast.makeText(profsched.this, "No students found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(profsched.this, "No students found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error if necessary
                    }
                });
            }
        });
    }

    private String getCurrentProfessorUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    private void populateClassIdsSpinner(String professorUid, String selectedSubject) {
        DatabaseReference classIdRef = enrollSubRef.child(professorUid).child(selectedSubject);
        classIdRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> classIdList = new ArrayList<>();

                    for (DataSnapshot classIdSnapshot : snapshot.getChildren()) {
                        String classId = classIdSnapshot.getKey();
                        classIdList.add(classId);
                    }

                    ArrayAdapter<String> classIdAdapter = new ArrayAdapter<>(profsched.this,
                            android.R.layout.simple_spinner_item, classIdList);
                    classIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    classid.setAdapter(classIdAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
            }
        });
    }

    private void populateSubjectSpinner(String professorUid) {
        DatabaseReference subjectRef = enrollSubRef.child(professorUid);
        subjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> subjectList = new ArrayList<>();

                    for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                        String subject = subjectSnapshot.getKey();
                        subjectList.add(subject);
                    }

                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(profsched.this,
                            android.R.layout.simple_spinner_item, subjectList);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subject.setAdapter(subjectAdapter);

                    // Select the first subject by default
                    if (!subjectList.isEmpty()) {
                        String firstSubject = subjectList.get(0);
                        populateClassIdsSpinner(professorUid, firstSubject);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
            }
        });
    }
}
