package com.example.practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Admin_sign_up extends AppCompatActivity {

    EditText username, password, repassword;
    Button signup, signin;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference adminRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        signup = (Button) findViewById(R.id.btnsignup);
        signin = (Button) findViewById(R.id.btnsignin);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        adminRef = database.getReference("Admin");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Admins");

                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();

                if(user.equals("")||pass.equals("")||repass.equals(""))
                    Toast.makeText(Admin_sign_up.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    if(pass.equals(repass)){
                                Admin admin = new Admin(user, pass);
                                adminRef.push().setValue(admin);
                                Toast.makeText(Admin_sign_up.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Admin_sign_in.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(Admin_sign_up.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }

                } }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Admin_sign_in.class);
                startActivity(intent);
            }
        });
    }
}