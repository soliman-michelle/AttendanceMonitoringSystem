package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class UpdateStudentInfo extends AppCompatActivity {

    private EditText passwordEditText;
    private EditText phoneNumberEditText;
    private EditText emailEditText;
    private Spinner yearSpinner;
    private Spinner sectionSpinner;
    private Button updateButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private studentList selectedStudent; // Added field to store the selected student
    private TextView fullNameTextView;
    private TextView studentNumberTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student_info);

        // Initialize views
        passwordEditText = findViewById(R.id.passwordEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        yearSpinner = findViewById(R.id.year);
        sectionSpinner = findViewById(R.id.section);
        updateButton = findViewById(R.id.updateButton);
        fullNameTextView = findViewById(R.id.fullNameTextView);
        studentNumberTextView = findViewById(R.id.studentNumberTextView);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Retrieve the selected student from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String fullname = intent.getStringExtra("fullname");
            String studNum = intent.getStringExtra("studNum");
            String defaultpass = intent.getStringExtra("defaultpass");
            String email = intent.getStringExtra("email");
            String phone = intent.getStringExtra("phone");
            String section = intent.getStringExtra("section");
            String yearLevel = intent.getStringExtra("yearLevel");

            // Populate the fields with the student data
            fullNameTextView.setText(fullname);
            studentNumberTextView.setText(studNum);
            passwordEditText.setText(defaultpass);
            emailEditText.setText(email);
            phoneNumberEditText.setText(phone);

            ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_options, android.R.layout.simple_spinner_item);
            yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            yearSpinner.setAdapter(yearAdapter);
            setSpinnerSelection(yearSpinner, yearLevel);
            ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this, R.array.section_options, android.R.layout.simple_spinner_item);
            sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sectionSpinner.setAdapter(sectionAdapter);
            setSpinnerSelection(sectionSpinner, section);

            selectedStudent = new studentList(
                    fullname,
                    studNum,
                    yearLevel,
                    section,
                    email,
                    phone,
                    defaultpass
            );


        }

        // Set click listener for update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStudentInfo();
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void updateStudentInfo() {
        // Retrieve the updated information from the form fields
        String newPassword = passwordEditText.getText().toString();
        String newPhoneNumber = phoneNumberEditText.getText().toString();
        String newEmail = emailEditText.getText().toString();
        String newYearLevel = yearSpinner.getSelectedItem().toString();
        String newSection = sectionSpinner.getSelectedItem().toString();

        String encodedEmail = encodeEmail(newEmail);

        // Update the student's information in the Firebase Realtime Database (StudentAcc)
        DatabaseReference studentAccRef = databaseReference.child("StudentAcc").child("BSCS")
                .child(selectedStudent.getYearLevel()).child(selectedStudent.getSection())
                .child(selectedStudent.getStudNum());

        studentAccRef.child("yearLevel").setValue(newYearLevel);
        studentAccRef.child("section").setValue(newSection);
        studentAccRef.child("email").setValue(newEmail);
        studentAccRef.child("phone").setValue(newPhoneNumber);
        studentAccRef.child("defaultpass").setValue(newPassword);

        // Update the student's information in the Firebase Realtime Database (profiledb)
        DatabaseReference usersRef = databaseReference.child("profiledb");
        usersRef.orderByChild("studnum").equalTo(selectedStudent.getStudNum()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();

                    // Update the user's information in the Firebase Realtime Database (profiledb)
                    DatabaseReference userProfileRef = databaseReference.child("profiledb")
                            .child(userId);

                    userProfileRef.child("defaultpass").setValue(newPassword);
                    userProfileRef.child("email").setValue(newEmail);
                    userProfileRef.child("phone").setValue(newPhoneNumber);
                    userProfileRef.child("year").setValue(newYearLevel);
                }

                // Display a toast message to indicate that the update was successful
                Toast.makeText(UpdateStudentInfo.this, "Student information updated", Toast.LENGTH_SHORT).show();

                // Finish the activity to go back to the previous screen
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
                Toast.makeText(UpdateStudentInfo.this, "Failed to update student information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encodeEmail(String email) {
        return Base64.encodeToString(email.getBytes(), Base64.NO_WRAP);
    }
}
