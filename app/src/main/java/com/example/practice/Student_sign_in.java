package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
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

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class Student_sign_in extends AppCompatActivity {
    TextInputLayout studentNo, password;
    Button forgot, sign_in, signup;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    public static final String SHARED_PREFS = "sharedprefs";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_in);

        studentNo = (TextInputLayout) findViewById(R.id.studentNo);
        password = (TextInputLayout) findViewById(R.id.password);
        forgot = (Button) findViewById(R.id.bt_forgot);
        sign_in = (Button) findViewById(R.id.signin);

        progressBar = (ProgressBar) findViewById(R.id.progressBarSignin);
        mAuth = FirebaseAuth.getInstance();
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        checkBox();
    }

    /*   private Boolean validateStudent() {
           String val = studentNo.getEditText().getText().toString();

           if (val.isEmpty()) {
               studentNo.setError("Field Cannot be Empty!");
               return false;
           } else if (val.length() > 😎 {
               studentNo.setError("Student number is too long!");
               return false;
           }else if (val.length() < 😎 {
               studentNo.setError("Student number is too short!");
               return false;
           } else{
               studentNo.setError(null);
               studentNo.setErrorEnabled(false);
               return true;
           }
       }
       private Boolean validatePassword(){
           String val = password.getEditText().getText().toString();

           if (val.isEmpty()) {
               password.setError("Field cannot be Empty!");
               return false;
           } else{
               password.setError(null);
               return true;
           }
       } */
    public void forgotPasswordClicked(View v){
        Intent intent = new Intent(Student_sign_in.this, ForgotPassword.class);
        startActivity(intent);
    }

    private void signIn()  {
        String enterStudNo = studentNo.getEditText().getText().toString().trim();
        String enterPass = password.getEditText().getText().toString().trim();

        if (enterStudNo.isEmpty()) {
            studentNo.setError("Field Cannot be Empty!");
            return;
        }
        if (enterPass.isEmpty()) {
            password.setError("Field cannot be Empty!");
            return;
        }
        mAuth.signInWithEmailAndPassword(enterStudNo + "@gmail.com", enterPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("names", enterStudNo); // Store the email as a suggested account
                            editor.putString("passwords", enterPass); // Store the password
                            editor.apply();

                            Toast.makeText(Student_sign_in.this, "Successfully Log in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Student_sign_in.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void checkBox() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String check = sharedPreferences.getString("names", "");
        String pass = sharedPreferences.getString("passwords", "");

        if (!check.isEmpty()) {
            studentNo.getEditText().setText(check);
            if (!pass.isEmpty()) {
                password.getEditText().setText(pass); // Pre-fill the password field with the saved password
            }
        }
    }
}