package com.example.practice;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.practice.databinding.ActivityScannerBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class scanner extends Drawable {

    Button qrbtn;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    public static TextView qrtext, arrival, date, address, name, studNum, prof, subject, term;
    public static int REQUEST_CODE = 100;
    private FirebaseUser currentUser;
    DatabaseReference usersRef;
    ActivityScannerBinding activityScannerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityScannerBinding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(activityScannerBinding.getRoot());
        allocateActivityTitle("Attendance Scanner");

        //String profUid = getIntent().getStringExtra("profUid");

        qrbtn = (Button) findViewById(R.id.qrbtn);
        qrtext = (TextView) findViewById(R.id.qrtext);
        arrival = (TextView) findViewById(R.id.arrival);
        date = (TextView) findViewById(R.id.date);
        name = (TextView) findViewById(R.id.name);
        studNum = (TextView) findViewById(R.id.studentNum);
        address = (TextView) findViewById(R.id.location);
        subject = findViewById(R.id.subject);
        term = findViewById(R.id.term);
        prof = findViewById(R.id.prof);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        qrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), qrscanner.class));
                getLocation();
                saveUser();
            }
        });
    }
    private void getLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(scanner.this, Locale.getDefault());

                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            address.setText("Location: " + addresses.get(0).getAddressLine(0)
                            );
                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            askPermission();
        }
    }

    private void askPermission() {

        ActivityCompat.requestPermissions(scanner.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(scanner.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void saveUser() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("profiledb");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userKey = user.getUid();

        ref.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstname = dataSnapshot.child("fname").getValue(String.class);
                String middlename = dataSnapshot.child("mname").getValue(String.class);
                String lastname = dataSnapshot.child("lname").getValue(String.class);
                String fullName = firstname + " " + middlename + " " + lastname;
                String studNumString = dataSnapshot.child("studnum").getValue(String.class);

                name.setText("" + fullName);
                studNum.setText("" + studNumString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
}
}