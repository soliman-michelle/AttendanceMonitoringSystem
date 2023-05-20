package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText editTextEmail;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    public void forgotPassButtonClicked(View v){

        resetPassword();
    }
    private void resetPassword(){
        String txtEmail = editTextEmail.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
            editTextEmail.setError("Please Enter Valid Email!");
            editTextEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(txtEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotPassword.this, "New Password is sent to your Email", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotPassword.this, Student_sign_in.class);
                    startActivity(intent);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotPassword.this, "Sorry you can`t reset your password", Toast.LENGTH_LONG).show();

                }
            }

        });
    }
}