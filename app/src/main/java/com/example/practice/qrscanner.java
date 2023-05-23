package com.example.practice;

import static com.example.practice.scanner.REQUEST_CODE;
import static com.example.practice.scanner.arrival;
import static com.example.practice.scanner.fusedLocationProviderClient;
import static com.example.practice.scanner.name;
import static com.example.practice.scanner.studNum;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class qrscanner extends Drawable implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;
    DatabaseReference dbref;
    private FirebaseUser currentUser;
    String profUid = qr_generator.profUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        dbref = FirebaseDatabase.getInstance().getReference("studentAT");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }


    @Override
    public void handleResult(Result result) {
        String data = result.getText().toString();
        String[] splitData = data.split("\\|");

        // Check if the splitData array contains at least 3 elements
        if (splitData.length >= 4) {
            String classId = splitData[0].trim();
            String term = splitData[2].trim();
            String subject = splitData[3].trim();
            String profuid = splitData[5].trim();

            scanner.term.setText(term);
            scanner.qrtext.setText(classId);
            scanner.subject.setText(subject);
            scanner.prof.setText(profuid);
        }
        Date dateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        String date = dateFormat.format(dateAndTime);
        String time = timeFormat.format(dateAndTime);
        getLocation();
        String locationString = scanner.address.getText().toString();
        String name = scanner.name.getText().toString();
        String studNum = scanner.studNum.getText().toString();
        String terms = scanner.term.getText().toString();
        String classid = scanner.qrtext.getText().toString();
        String subjects = scanner.subject.getText().toString();
        String id = scanner.prof.getText().toString();
            saveAttendance(classid, name, studNum, time, locationString, terms, subjects, id, date);
    }

    private void saveAttendance(String classId, String name, String studNum, String time, String locationString, String course, String terms, String id, String date) {
        // Format the date
        Date dateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(dateAndTime);
        String attendanceStatus = "Present";

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("studentAT");
        DatabaseReference profTrackerRef = FirebaseDatabase.getInstance().getReference("profTracker");

            StoreAttendance storeAttendance = new StoreAttendance(course, classId, terms, formattedDate, time, locationString, name, studNum, id);
            studentTrack track = new studentTrack(name, studNum, attendanceStatus, time);

        ref.child(currentUser.getUid()).child(terms).child(formattedDate).child(studNum).setValue(track)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                scanner.qrtext.setText("ClassID: " + classId);
                                scanner.date.setText("Date: " + formattedDate);
                                scanner.arrival.setText("Arrival time: " + time);
                                scanner.name.setText("Name: " + name);
                                scanner.studNum.setText("Student Number: " + studNum);
                                String attendanceStatus = "Present";
                                Toast.makeText(qrscanner.this, "Saved Attendance", Toast.LENGTH_SHORT).show();
                                studentTrack track = new studentTrack(name, studNum, attendanceStatus, time);

                                studentData studentDatas = new studentData(studNum, attendanceStatus,name, date, time);
                                // Store attendance in the classStudents node
                                profTrackerRef
                                        .child(id)
                                        .child(classId)
                                        .child(terms)
                                        .child(course)
                                        .child(formattedDate)
                                        .child(studNum).setValue(studentDatas);

                                onBackPressed();
                            } else {
                                // Save failed
                                Toast.makeText(qrscanner.this, "Failed to save attendance.", Toast.LENGTH_SHORT).show();
                            }
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
                            Geocoder geocoder = new Geocoder(qrscanner.this, Locale.getDefault());

                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            scanner.address.setText("Location: " + addresses.get(0).getAddressLine(0)
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

        ActivityCompat.requestPermissions(qrscanner.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(qrscanner.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}