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
                databaseReference = FirebaseDatabase.getInstance().getReference("profiledb/" + studentNumber);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Retrieve the profile data for the matching student number
                            String firstname = dataSnapshot.child("fname").getValue(String.class);
                            String middlename = dataSnapshot.child("mname").getValue(String.class);
                            String lastname = dataSnapshot.child("lname").getValue(String.class);
                            String user = dataSnapshot.child("studnum").getValue(String.class);
                            String phonenum = dataSnapshot.child("phone").getValue(String.class);
                            String pass = dataSnapshot.child("defaultpass").getValue(String.class);
                            String useremail = dataSnapshot.child("email").getValue(String.class);
                            nameLabel.setText(firstname + " " + middlename + " " + lastname);
                            editname.setText(firstname + " " + middlename + " " + lastname);
                            editusername.setText(user);
                            editPhone.setText(phonenum);
                            editEmail.setText(useremail);
                            editPassword.setText(pass);
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
        // Implement your logic to extract the student number from the email
        // For example, if the email format is "studentNumber@gmail.com",
        // you can extract the student number by removing the "@gmail.com" part.
        // Modify this method according to your specific email format.
        if (email != null && email.contains("@")) {
            String[] parts = email.split("@");
            if (parts.length > 0) {
                return parts[0];
            }
        }
        return null;
    }
}
