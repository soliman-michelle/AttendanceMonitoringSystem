package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class showStudents extends AppCompatActivity {
    private Spinner course, year, block;
    ArrayList<String> courseLists;
    private ArrayAdapter<String> courseAdapters;
    Button show;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_students);

        year = (Spinner) findViewById(R.id.yearLevelSpinner);
        course = (Spinner) findViewById(R.id.courseSpinner);
        block = (Spinner) findViewById(R.id.block);
        show = findViewById(R.id.show);
        courseLists = new ArrayList<>();
        ArrayAdapter<CharSequence> yearLevelAdapter = ArrayAdapter.createFromResource(this,
                R.array.year_options, android.R.layout.simple_spinner_item);
        yearLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(yearLevelAdapter);

        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this,
                R.array.section_options, android.R.layout.simple_spinner_item);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        block.setAdapter(sectionAdapter);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("programList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String spinner = childSnapshot.child("program").getValue(String.class);
                    courseLists.add(spinner);

                    courseAdapters = new ArrayAdapter<String>(showStudents.this, android.R.layout.simple_spinner_dropdown_item, courseLists);
                    courseAdapters.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    course.setAdapter(courseAdapters);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedCourse = course.getSelectedItem().toString();
                String selectedYear = year.getSelectedItem().toString();
                String selectedBlock = block.getSelectedItem().toString();

                DatabaseReference studentAccRef = reference.child("StudentAcc")
                        .child(selectedCourse)
                        .child(selectedYear)
                        .child(selectedBlock);

                studentAccRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ArrayList<studentList> studentList = new ArrayList<>();

                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String fname = childSnapshot.child("fname").getValue(String.class);
                                String lname = childSnapshot.child("lname").getValue(String.class);
                                String mname = childSnapshot.child("mname").getValue(String.class);
                                String fullname = fname + " " + mname + " " + lname;
                                String studNum = childSnapshot.child("studnum").getValue(String.class);

                                studentList student = new studentList(fullname, studNum);
                                studentList.add(student);
                            }

                            Intent intent = new Intent(showStudents.this, showStudentList.class);
                            intent.putParcelableArrayListExtra("studentList", studentList);
                            startActivity(intent);
                        } else {
                            // Handle the case when no students are found
                            Toast.makeText(showStudents.this, "No students found", Toast.LENGTH_SHORT).show();
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
}