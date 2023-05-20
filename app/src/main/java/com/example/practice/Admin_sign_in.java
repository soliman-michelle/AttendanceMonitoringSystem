package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Admin_sign_in extends AppCompatActivity {
    EditText username, password;

    Button btnlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_in);
        username = (EditText) findViewById(R.id.username1);
        password = (EditText) findViewById(R.id.password1);
        btnlogin = (Button) findViewById(R.id.btnsignin1);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = username.getText().toString();
                String pass = password.getText().toString();

                if(user.equals("")||pass.equals(""))
                    Toast.makeText(Admin_sign_in.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(Admin_sign_in.this, "Sign in successfull", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), AdminHome.class);
                    startActivity(intent);
                }
        }
        });
    }
}
