package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Prof_sign_in extends AppCompatActivity {
    TextInputLayout username, password;
    Button forgot, sign_in;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    public static final String SHARED_PREFS = "sharedprefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_sign_in);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forgot = findViewById(R.id.bt_forgot);
        sign_in = findViewById(R.id.signin);
        progressBar = findViewById(R.id.progressBarSignin);
        mAuth = FirebaseAuth.getInstance();

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        checkBox();
    }

    private void signIn() {
        String email = username.getEditText().getText().toString().trim();
        String passwords = password.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            username.setError("Field Cannot be Empty!");
            return;
        }
        if (passwords.isEmpty()) {
            password.setError("Field cannot be Empty!");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, passwords)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("name", email); // Store the email as a suggested account
                            editor.putString("password", passwords); // Store the password
                            editor.apply();

                            Toast.makeText(Prof_sign_in.this, "Successfully Log in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ProfHome.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Prof_sign_in.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void forgotPasswordClicked(View v) {
        Intent intent = new Intent(Prof_sign_in.this, ForgotPassword.class);
        startActivity(intent);
    }

    private void checkBox() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String email = sharedPreferences.getString("name", "");
        String savedPassword = sharedPreferences.getString("password", "");

        if (!email.isEmpty()) {
            username.getEditText().setText(email); // Pre-fill the username field with the saved email

            if (!savedPassword.isEmpty()) {
                password.getEditText().setText(savedPassword); // Pre-fill the password field with the saved password
            }
        }
    }
}