package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.practice.databinding.ActivityProfileBinding;
import com.example.practice.databinding.ActivityScannerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    TextView nameLabel, editname, editusername, editEmail, editPassword, editPhone;
    ActivityProfileBinding activityProfileBinding;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Make sure to use the correct layout file name

        nameLabel = findViewById(R.id.nameLabel);
        editname = findViewById(R.id.editTextName);
        editusername = findViewById(R.id.editTextUsername);
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editPhone = findViewById(R.id.editTextPhone);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();

            // Extract the student number from the email
            String studentNumber = extractStudentNumber(email);

            if (studentNumber != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("StudentAcc");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot bscsSnapshot = dataSnapshot.child("BSCS");
                            if (bscsSnapshot.exists()) {
                                for (DataSnapshot yearSnapshot : bscsSnapshot.getChildren()) {
                                    for (DataSnapshot sectionSnapshot : yearSnapshot.getChildren()) {
                                        DataSnapshot studentSnapshot = sectionSnapshot.child(studentNumber);
                                        if (studentSnapshot.exists()) {
                                            String firstname = studentSnapshot.child("fname").getValue(String.class);
                                            String middlename = studentSnapshot.child("mname").getValue(String.class);
                                            String lastname = studentSnapshot.child("lname").getValue(String.class);
                                            String user = studentSnapshot.child("studnum").getValue(String.class);
                                            String phonenum = studentSnapshot.child("phone").getValue(String.class);
                                            String pass = studentSnapshot.child("defaultpass").getValue(String.class);
                                            String useremail = studentSnapshot.child("email").getValue(String.class);
                                            nameLabel.setText(firstname + " " + middlename + " " + lastname);
                                            editname.setText(firstname + " " + middlename + " " + lastname);
                                            editusername.setText(user);
                                            editPhone.setText(phonenum);
                                            editEmail.setText(useremail);
                                            editPassword.setText(pass);
                                            return; // Exit the listener if the student number is found
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DATABASE", "Error retrieving data from database", error.toException());
                    }
                });
            }
        }
    }
        private String extractStudentNumber(String email) {
        if (email != null && email.contains("@")) {
            String[] parts = email.split("@");
            if (parts.length > 0) {
                return parts[0];
            }
        }
        return null;
    }
}
