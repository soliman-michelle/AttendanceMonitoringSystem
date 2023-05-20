package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.FragmentManager;
import android.widget.Button;
import android.widget.Toast;
public class welcomeScreen extends AppCompatActivity implements restrictForAdmin.restrictForAdminListener {
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        mButton = findViewById(R.id.bt_admin);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                restrictForAdmin restrictForAdmin = new restrictForAdmin();
                restrictForAdmin.show(fragmentManager, "login_dialog");
            }
        });
    }
    @Override
    public void onLoginClick(String username, String password) {
        if (username.equals("admin") && password.equals("1234")) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    public void prof(View view) {
        Intent intent = new Intent(welcomeScreen.this, Prof_sign_in.class);
        startActivity(intent);
    }

    public void student(View view) {
        Intent intent = new Intent(welcomeScreen.this, Student_sign_in.class);
        startActivity(intent);
    }
}