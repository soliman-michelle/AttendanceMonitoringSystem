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

    private EditText passwordEditText, phoneNumberEditText, emailEditText, fname, mname, lname;
    private Spinner yearSpinner, sectionSpinner;
    private Button updateButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private studentList selectedStudent; // Added field to store the selected student
    private TextView fullNameTextView, studentNumberTextView, courseTextView;

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
        courseTextView = findViewById(R.id.courseTextView);
        fname = findViewById(R.id.fname);
        mname = findViewById(R.id.mname);
        lname = findViewById(R.id.lname);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Retrieve the selected student from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String fullname = intent.getStringExtra("fullname");
            String studNum = intent.getStringExtra("studNum");
            String course = intent.getStringExtra("course");
            String yearLevel = intent.getStringExtra("yearLevel");
            String section = intent.getStringExtra("section");
            String email = intent.getStringExtra("email");
            String defaultpass = intent.getStringExtra("defaultpass");
            String phone = intent.getStringExtra("phone");

            String[] nameParts = fullname.split("\\s+"); // Assuming the full name has spaces as separators
            String firstName = "";
            String middleName = "";
            String lastName = "";

            if (nameParts.length >= 1) {
                firstName = nameParts[0];
            }
            if (nameParts.length >= 2) {
                lastName = nameParts[nameParts.length - 1];
            }
            if (nameParts.length >= 3) {
                middleName = nameParts[1];
            }

            // Populate the fields with the student data
            fullNameTextView.setText(fullname);
            studentNumberTextView.setText(studNum);
            courseTextView.setText(course);
            passwordEditText.setText(defaultpass);
            emailEditText.setText(email);
            phoneNumberEditText.setText(phone);
            fname.setText(firstName);
            mname.setText(middleName);
            lname.setText(lastName);

            ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_options, android.R.layout.simple_spinner_item);
            yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            yearSpinner.setAdapter(yearAdapter);
            setSpinnerSelection(yearSpinner, yearLevel);
            yearSpinner.setClickable(false);

            ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this, R.array.section_options, android.R.layout.simple_spinner_item);
            sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sectionSpinner.setAdapter(sectionAdapter);
            setSpinnerSelection(sectionSpinner, section);

            selectedStudent = new studentList(
                    fullname,
                    studNum,
                    course,
                    yearLevel,
                    section,
                    email,
                    defaultpass,
                    phone

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
        String fullName = fullNameTextView.getText().toString();
        String studNum = studentNumberTextView.getText().toString();

        // Split the full name into first name, middle name, and last name
        String[] nameParts = fullName.split("\\s+"); // Assuming the full name has spaces as separators
        String firstName = "";
        String middleName = "";
        String lastName = "";

        if (nameParts.length >= 1) {
            firstName = nameParts[0];
        }
        if (nameParts.length >= 2) {
            lastName = nameParts[nameParts.length - 1];
        }
        if (nameParts.length >= 3) {
            middleName = nameParts[1];
        }

        // Check if the section has changed
        if (!newSection.equals(selectedStudent.getSection())) {
            // Section has changed, update the section in the Firebase Realtime Database (StudentAcc)
            DatabaseReference studentAccRef = databaseReference.child("StudentAcc")
                    .child("BSCS")
                    .child(selectedStudent.getYearLevel())
                    .child(selectedStudent.getSection())
                    .child(selectedStudent.getStudNum());

            studentAccRef.removeValue(); // Remove the existing student data under the old section

            // Create a new DatabaseReference for the updated section
            DatabaseReference newStudentAccRef = databaseReference.child("StudentAcc")
                    .child("BSCS")
                    .child(newYearLevel) // Use newYearLevel instead of selectedStudent.getYearLevel()
                    .child(newSection) // Use newSection instead of selectedStudent.getSection()
                    .child(selectedStudent.getStudNum());

            // Set the updated values for the student under the new section
            newStudentAccRef.child("fname").setValue(firstName);
            newStudentAccRef.child("mname").setValue(middleName);
            newStudentAccRef.child("lname").setValue(lastName);
            newStudentAccRef.child("studnum").setValue(studNum);
            newStudentAccRef.child("year").setValue(newYearLevel);
            newStudentAccRef.child("section").setValue(newSection);
            newStudentAccRef.child("email").setValue(newEmail);
            newStudentAccRef.child("phone").setValue(newPhoneNumber);
            newStudentAccRef.child("defaultpass").setValue(newPassword);

            // Display a toast message to indicate that the update was successful
            Toast.makeText(UpdateStudentInfo.this, "Student information updated", Toast.LENGTH_SHORT).show();

            // Finish the activity to go back to the previous screen
            finish();
        } else {
            // Section has not changed, update only the other fields in the Firebase Realtime Database (StudentAcc)
            DatabaseReference studentAccRef = databaseReference.child("StudentAcc")
                    .child("BSCS")
                    .child(selectedStudent.getYearLevel())
                    .child(selectedStudent.getSection())
                    .child(selectedStudent.getStudNum());

            studentAccRef.child("fname").setValue(firstName);
            studentAccRef.child("mname").setValue(middleName);
            studentAccRef.child("lname").setValue(lastName);
            studentAccRef.child("studnum").setValue(studNum);
            studentAccRef.child("yearLevel").setValue(newYearLevel);
            studentAccRef.child("email").setValue(newEmail);
            studentAccRef.child("phone").setValue(newPhoneNumber);
            studentAccRef.child("defaultpass").setValue(newPassword);

            // Display a toast message to indicate that the update was successful
            Toast.makeText(UpdateStudentInfo.this, "Student information updated", Toast.LENGTH_SHORT).show();

            // Finish the activity to go back to the previous screen
            finish();
        }
    }

    private String encodeEmail(String email) {
        return Base64.encodeToString(email.getBytes(), Base64.NO_WRAP);
    }
}
