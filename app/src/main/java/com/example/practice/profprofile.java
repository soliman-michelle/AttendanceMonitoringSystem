package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.practice.databinding.ActivityProfprofileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profprofile extends profdrawable {
    TextView nameLabel, editname, editusername, editEmail, editPassword, editPhone;
ActivityProfprofileBinding activityProfprofileBinding;
    FirebaseUser user;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfprofileBinding = ActivityProfprofileBinding.inflate(getLayoutInflater());
        setContentView(activityProfprofileBinding.getRoot());
        allocateActivityTitle("Profile");

        editname = findViewById(R.id.editTextName);
        editusername = findViewById(R.id.editTextUsername);
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editPhone = findViewById(R.id.editTextPhone);

        nameLabel = findViewById(R.id.nameLabel);


        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("addProfessors").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstname = dataSnapshot.child("fname").getValue(String.class);
                    String middlename = dataSnapshot.child("mname").getValue(String.class);
                    String lastname = dataSnapshot.child("lname").getValue(String.class);
                    String user = dataSnapshot.child("username").getValue(String.class);
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

