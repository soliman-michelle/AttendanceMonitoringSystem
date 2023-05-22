package com.example.practice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.practice.databinding.ActivityQrGeneratorBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class qr_generator extends AppCompatActivity {
    MaterialButton qrbtn, savebtn;
    ImageView imageview, barimageview;
    Spinner year, section, term;
    Bitmap bitmap;
    FirebaseUser user;
    TextView prof, loc;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    public static int REQUEST_CODE = 100;
    AutoCompleteTextView courses;
    ArrayList<String> courseLists, profList, subjectList;
    private ArrayAdapter<String> courseAdapters, profAdapter, subAdapter;
    private DatabaseReference enrollSubRef;
    private static final int STORAGE_PERMISSION_CODE = 100;
    ActivityQrGeneratorBinding activityQrGeneratorBinding;
    DatabaseReference dbref;
    Map<String, Object> enrollSubData;
    public static String profUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityQrGeneratorBinding = ActivityQrGeneratorBinding.inflate(getLayoutInflater());
        setContentView(activityQrGeneratorBinding.getRoot());

        qrbtn = findViewById(R.id.qr_id);
        savebtn = findViewById(R.id.save_id);
        imageview = findViewById(R.id.imageview_id);
        barimageview = findViewById(R.id.barimageview_image);
        courses = findViewById(R.id.subject);
        year = findViewById(R.id.year);
        section = findViewById(R.id.section);
        loc = findViewById(R.id.location);
        term = findViewById(R.id.term);
        subjectList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        courseLists = new ArrayList<>();

        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this,
                R.array.term_options, android.R.layout.simple_spinner_item);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NothingSelectedSpinnerAdapter termSpinnerAdapter = new NothingSelectedSpinnerAdapter(
                termAdapter,
                R.layout.spinner_prompt_item,
                this,
                "Select Term");
        term.setAdapter(termSpinnerAdapter);
        ArrayAdapter<CharSequence> yearLevelAdapter = ArrayAdapter.createFromResource(this,
                R.array.year_options, android.R.layout.simple_spinner_item);
        yearLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NothingSelectedSpinnerAdapter yearLevelSpinnerAdapter = new NothingSelectedSpinnerAdapter(
                yearLevelAdapter,
                R.layout.spinner_prompt_item,
                this,
                "Select Year Level");
        year.setAdapter(yearLevelSpinnerAdapter);

        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this,
                R.array.section_options, android.R.layout.simple_spinner_item);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NothingSelectedSpinnerAdapter nothingSelectedAdapter = new NothingSelectedSpinnerAdapter(
                sectionAdapter,
                R.layout.spinner_prompt_item,
                this,
                "Select Section");
        section.setAdapter(nothingSelectedAdapter);

        dbref = FirebaseDatabase.getInstance().getReference();
        enrollSubRef = FirebaseDatabase.getInstance().getReference("subList");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            profUid = user.getUid();
        }
        dbref = FirebaseDatabase.getInstance().getReference();
        dbref.child("subList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String spinner = childSnapshot.child("subject").getValue(String.class);
                    subjectList.add(spinner);

                    subAdapter = new ArrayAdapter<String>(qr_generator.this, android.R.layout.simple_dropdown_item_1line, subjectList);
                    courses.setAdapter(subAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference profRef = dbref.child(profUid);

        // Retrieve enrollSub data

        qrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String years = year.getSelectedItem().toString().trim();
                String sections = section.getSelectedItem().toString().trim();
                String subject = courses.getText().toString();
                String profUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String classId = subject + " " + sections;
                String terms = term.getSelectedItem().toString().trim();
                getLocation();
                String locationString = loc.getText().toString();

                ///prof.setText(profUid);

                String qrData = classId + "|" + years + "|" + terms + "|" + subject + "|" + sections + "|" + profUid + "|" + locationString;

                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 350, 350);

                    BarcodeEncoder encoder = new BarcodeEncoder();
                    bitmap = encoder.createBitmap(matrix);
                    imageview.setImageBitmap(bitmap);

                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(courses.getWindowToken(), 0);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    saveImage();
                }
            }
        });
    }
    void saveImage() {
        try {
            DatabaseReference studentAccRef = FirebaseDatabase.getInstance().getReference("StudentAcc");
            DatabaseReference classStudentsRef = FirebaseDatabase.getInstance().getReference("classStudents");
            DatabaseReference profRef = FirebaseDatabase.getInstance().getReference("profTracker");

            String path = Environment.getExternalStorageDirectory().toString();
            File directory = new File(path + "/folder/subfolder");
            directory.mkdirs();

            String filename = "image.jpg";
            File file = new File(directory, filename);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), filename, filename);

            Toast.makeText(getApplicationContext(), "File is Saved in  " + file, Toast.LENGTH_LONG).show();

            String years = year.getSelectedItem().toString();
            String course = courses.getText().toString().trim();
            String sections = section.getSelectedItem().toString().trim();
            String terms = term.getSelectedItem().toString().trim();
            String classid = course + " " + sections;
            String profUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Date dateAndTime = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(dateAndTime);
            String locationString = loc.getText().toString();

            studentAccRef.child("BSCS")
                    .child(years)
                    .child(sections)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final boolean[] sectionExists = {false};
                            final boolean[] studentExists = {false};

                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String studentId = childSnapshot.getKey();
                                String fname = childSnapshot.child("fname").getValue(String.class);
                                String lname = childSnapshot.child("lname").getValue(String.class);
                                String studentName = fname + " " + lname;

                                DatabaseReference classSectionRef = classStudentsRef.child(classid)
                                        .child(locationString)
                                        .child(studentId);

                                DatabaseReference prof = profRef.child(profUid)
                                        .child(classid)
                                        .child(course)
                                        .child(terms)
                                        .child(formattedDate)
                                        .child(studentId)
                                        .child(studentName);

                                String length = ""; // Empty string by default
                                String arrival= "";
                                String departure= "";
                                String status= "";
                                String date = "";

                                AttendanceRecord attendance = new AttendanceRecord(arrival, length, departure, status, studentName);
                                // Update the student data in profTracker
                                prof.child(formattedDate).setValue(attendance);
                                prof.child(studentId).child("studentName").setValue("");
                                prof.child(studentId).child("arrival").setValue("");
                                prof.child(studentId).child("length_stay").setValue("");
                                prof.child(studentId).child("departure").setValue("");
                                prof .child(studentId).child("status").setValue("");
                                classSectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            // Section doesn't exist, create a new section
                                            classSectionRef.child(studentId).setValue(studentName);
                                        } else {
                                            // Section exists, check if the student is already assigned to another class ID for the same subject
                                            boolean studentAssigned = false;
                                            for (DataSnapshot sectionSnapshot : dataSnapshot.getChildren()) {
                                                if (sectionSnapshot.hasChild(studentId)) {
                                                    // Student is already assigned to another class ID for the same subject
                                                    studentAssigned = true;
                                                    break;
                                                }
                                            }
                                            if (!studentAssigned) {
                                                classSectionRef.child(studentId).setValue(studentName);
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle the error
                                    }
                                });

                                studentExists[0] = true;
                            }

                            if (!sectionExists[0]) {
                                DatabaseReference sectionRef = classStudentsRef.child(classid)
                                        .child(locationString)
                                        .child("section");

                                sectionRef.setValue(sections);
                            }

                            if (!studentExists[0]) {
                                // If no students exist in the section, remove the section from the classStudents node
                                classStudentsRef.child(classid)
                                        .child(locationString)
                                        .removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle the error
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


                                        private String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle activity result if needed
    }
    private void getLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(qr_generator.this, Locale.getDefault());

                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            loc.setText("Location: " + addresses.get(0).getAddressLine(0)
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

        ActivityCompat.requestPermissions(qr_generator.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                getLocation();

            } else {


                Toast.makeText(qr_generator.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();

            }


        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}