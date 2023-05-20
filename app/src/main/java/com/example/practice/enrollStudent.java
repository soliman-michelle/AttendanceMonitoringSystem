package com.example.practice;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class enrollStudent extends AppCompatActivity {
    Spinner year, course, block, term;
    EditText classId;
    List<String> classIds;
    AutoCompleteTextView courses;
    TextView loc;
    private FusedLocationProviderClient fusedLocationProviderClient;

    ArrayList<String> courseLists, profList, subjectList;
    private ArrayAdapter<String> courseAdapters, profAdapter, subAdapter;
    DatabaseReference reference, ref, dbref, enroll, ret;
    public static final int REQUEST_CODE = 100;

    Button save, retrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_student);
        year = (Spinner) findViewById(R.id.year);
        course = (Spinner) findViewById(R.id.program);
        block = (Spinner) findViewById(R.id.block);
        term = (Spinner) findViewById(R.id.terms);
        courses = findViewById(R.id.subject);
        classId = findViewById(R.id.classId);
        loc = findViewById(R.id.locations);

        save = findViewById(R.id.save);
        retrieve = findViewById(R.id.retrive);

        courseLists = new ArrayList<>();
        profList = new ArrayList<>();
        subjectList = new ArrayList<>();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ArrayAdapter<CharSequence> yearLevelAdapter = ArrayAdapter.createFromResource(this,
                R.array.year_options, android.R.layout.simple_spinner_item);
        yearLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(yearLevelAdapter);

        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this,
                R.array.term_options, android.R.layout.simple_spinner_item);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        term.setAdapter(termAdapter);

        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this,
                R.array.section_options, android.R.layout.simple_spinner_item);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        block.setAdapter(sectionAdapter);
        ref = FirebaseDatabase.getInstance().getReference();

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("programList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String spinner = childSnapshot.child("program").getValue(String.class);
                    courseLists.add(spinner);

                    courseAdapters = new ArrayAdapter<String>(enrollStudent.this, android.R.layout.simple_spinner_dropdown_item, courseLists);
                    courseAdapters.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    course.setAdapter(courseAdapters);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        dbref = FirebaseDatabase.getInstance().getReference();
        dbref.child("subList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String spinner = childSnapshot.child("subject").getValue(String.class);
                    subjectList.add(spinner);

                    subAdapter = new ArrayAdapter<String>(enrollStudent.this, android.R.layout.simple_dropdown_item_1line, subjectList);
                    courses.setAdapter(subAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enroll = FirebaseDatabase.getInstance().getReference("enrollSub");
                DatabaseReference studentAccRef = FirebaseDatabase.getInstance().getReference("StudentAcc");
                DatabaseReference classStudentsRef = FirebaseDatabase.getInstance().getReference("classStudents");
                final String locationString = loc.getText().toString();
                String yearLevel = year.getSelectedItem().toString();
                String programNames = course.getSelectedItem().toString();
                String terms = term.getSelectedItem().toString();
                String blocks = block.getSelectedItem().toString();
                String id = classId.getText().toString();
                String subject = courses.getText().toString();
                getLocation();

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String uid = "";
                if (currentUser != null) {
                    uid = currentUser.getUid();
                }
                final String finalUid = uid;
                enrollSub enrollsub = new enrollSub(programNames, yearLevel, uid, terms, blocks, id, subject, locationString);
                classStudents classstud = new classStudents(id, locationString);

                // Check if the classId already exists for the subject
                DatabaseReference enrollSubjectRef = enroll.child(finalUid).child(subject).child(id);

                enrollSubjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // ClassId already exists for this subject, handle accordingly (e.g., show an error message)
                            Toast.makeText(getApplicationContext(), "ClassId already exists for this subject", Toast.LENGTH_SHORT).show();
                        } else {
                            // ClassId doesn't exist, proceed with saving
                            // Update classStudents with location information
                            DatabaseReference classSectionRef = classStudentsRef.child(id).child(locationString);
                            classSectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        classSectionRef.setValue(locationString);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle the error
                                }
                            });

                            studentAccRef.child(programNames)
                                    .child(yearLevel)
                                    .child(blocks)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            final boolean[] sectionExists = {false};
                                            final boolean[] studentExists = {false};

                                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                                String studentId = childSnapshot.getKey();
                                                String fname = childSnapshot.child("fname").getValue(String.class);
                                                String lname = childSnapshot.child("lname").getValue(String.class);
                                                String studentName = fname + " " + lname;

                                                DatabaseReference classSectionRef = classStudentsRef.child(id)
                                                        .child(locationString)
                                                        .child(studentId);
                                                classSectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (!dataSnapshot.exists()) {
                                                            // Section doesn't exist, create a new section
                                                            classSectionRef.child(studentId).setValue(studentName);
                                                        } else {
                                                            // Section exists, check if the student is already assigned to another class ID for the same subject
                                                            boolean studentAssigned = false;
                                                            for (DataSnapshot sectionSnapshot : dataSnapshot.getChildren()) {
                                                                if (sectionSnapshot.hasChild(studentId)) {
                                                                    // Student is already assigned to another class ID for the same subject
                                                                    studentAssigned = true;
                                                                    break;
                                                                }
                                                            }
                                                            if (!studentAssigned) {
                                                                classSectionRef.child(studentId).setValue(studentName);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        // Handle the error
                                                    }
                                                });

                                                DatabaseReference enrollSubjectRef = enroll
                                                        .child(finalUid)
                                                        .child(subject);

                                                // ...

                                                enrollSubjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        boolean sectionExists = false;
                                                        boolean studentExists = false;

                                                        if (dataSnapshot.exists()) {
                                                            // Check if the subject exists
                                                            if (dataSnapshot.hasChild(id)) {
                                                                // Class ID already exists for this subject
                                                                sectionExists = true;

                                                                // Check if the student already exists in the section
                                                                if (dataSnapshot.child(id).hasChild(studentId)) {
                                                                    studentExists = true;
                                                                }
                                                            }
                                                        }

                                                        if (!sectionExists) {
                                                            // Subject doesn't exist, create a new class
                                                            DatabaseReference enrollClassRef = enrollSubjectRef.child(id);
                                                            enrollClassRef.child(studentId).child("name").setValue(studentName);
                                                            enrollClassRef.child(studentId).child("uid").setValue(studentId);
                                                        } else {
                                                            if (!studentExists) {
                                                                // Section exists, but student not assigned to the section
                                                                DatabaseReference enrollClassRef = enrollSubjectRef.child(id);
                                                                enrollClassRef.child(studentId).child("name").setValue(studentName);
                                                                enrollClassRef.child(studentId).child("uid").setValue(studentId);
                                                            } else {
                                                                // Student already exists in the section or class ID already exists for this subject, handle accordingly (e.g., show an error message)
                                                                Toast.makeText(getApplicationContext(), "Student is already enrolled in a class ID for the same subject", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        // Handle the error
                                                    }
                                                });

// ...

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle the error
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error
                    }
                });
            }
        });
    }


       private void getLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(enrollStudent.this, Locale.getDefault());

                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            loc.setText("Location: " + addresses.get(0).getAddressLine(0)
                            );
                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {

            askPermission();


        }


    }

    private void askPermission() {

        ActivityCompat.requestPermissions(enrollStudent.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                getLocation();

            } else {


                Toast.makeText(enrollStudent.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();

            }


        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
