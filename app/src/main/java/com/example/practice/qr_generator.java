package com.example.practice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practice.databinding.ActivityQrGeneratorBinding;
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
    Spinner term, courses, classid;
    Bitmap bitmap;
    FirebaseUser user;
    TextView prof;
    private DatabaseReference enrollSubRef;
    private static final int STORAGE_PERMISSION_CODE = 100;
    ActivityQrGeneratorBinding activityQrGeneratorBinding;
    ArrayList<String> subjectList;
    ArrayAdapter<String> subAdapter;
    DatabaseReference dbref;
    Map<String, Object> enrollSubData; // Added for storing enrollSub data
    public static String profUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityQrGeneratorBinding = ActivityQrGeneratorBinding.inflate(getLayoutInflater());
        setContentView(activityQrGeneratorBinding.getRoot());

        qrbtn = findViewById(R.id.qr_id);
        savebtn = findViewById(R.id.save_id);
        courses = findViewById(R.id.course);
        imageview = findViewById(R.id.imageview_id);
        barimageview = findViewById(R.id.barimageview_image);
        term = findViewById(R.id.terms);
        classid = findViewById(R.id.id);
        prof = findViewById(R.id.prof);
        subjectList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
// Specify the "profTracker" node as the reference
        DatabaseReference profTrackerRef = database.getReference("profTracker");
        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this,
                R.array.term_options, android.R.layout.simple_spinner_item);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        term.setAdapter(termAdapter);

        dbref = FirebaseDatabase.getInstance().getReference();
        enrollSubRef = FirebaseDatabase.getInstance().getReference("enrollSub");

        String professorUid = getCurrentProfessorUid();
        if (professorUid != null) {
            populateSubjectSpinner(professorUid);
        }
        courses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSubject = adapterView.getItemAtPosition(position).toString();
                populateClassIdsSpinner(professorUid, selectedSubject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            profUid = user.getUid();
        }
        DatabaseReference profRef = dbref.child(profUid);
        profRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    subjectList.clear();

                    for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                        String courseName = courseSnapshot.getKey();
                        subjectList.add(courseName);
                        // Retrieve class IDs under the course
                        for (DataSnapshot classSnapshot : courseSnapshot.getChildren()) {
                            String classId = classSnapshot.getKey();
                            subjectList.add(classId);
                        }
                    }

                    subAdapter = new ArrayAdapter<>(qr_generator.this, android.R.layout.simple_dropdown_item_1line, subjectList);
                    courses.setAdapter(subAdapter);
                    classid.setAdapter(subAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        // Retrieve enrollSub data
        DatabaseReference enrollSubRef = dbref.child("enrollSub");
        enrollSubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    enrollSubData = (Map<String, Object>) snapshot.getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        classid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedClassId = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no item is selected
            }
        });


        qrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classId = classid.getSelectedItem().toString().trim();
                String terms = term.getSelectedItem().toString().trim();
                String subject = courses.getSelectedItem().toString().trim();
                String profUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ///prof.setText(profUid);
                // Combine the selected class ID, term, and subject with a delimiter "|"
                String qrData = classId + "|" + terms + "|" + subject + "|" + profUid;

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
    private String getCurrentProfessorUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }
    private void populateClassIdsSpinner(String professorUid, String selectedSubject) {
        DatabaseReference classIdRef = enrollSubRef.child(professorUid).child(selectedSubject);
        classIdRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> classIdList = new ArrayList<>();

                    for (DataSnapshot classIdSnapshot : snapshot.getChildren()) {
                        String classId = classIdSnapshot.getKey();
                        classIdList.add(classId);
                    }

                    ArrayAdapter<String> classIdAdapter = new ArrayAdapter<>(qr_generator.this,
                            android.R.layout.simple_spinner_item, classIdList);
                    classIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    classid.setAdapter(classIdAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
            }
        });
    }
    private void populateSubjectSpinner(String professorUid) {
        DatabaseReference subjectRef = enrollSubRef.child(professorUid);
        subjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> subjectList = new ArrayList<>();

                    for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                        String subject = subjectSnapshot.getKey();
                        subjectList.add(subject);
                    }

                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(qr_generator.this,
                            android.R.layout.simple_spinner_item, subjectList);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    courses.setAdapter(subjectAdapter);

                    // Select the first subject by default
                    if (!subjectList.isEmpty()) {
                        String firstSubject = subjectList.get(0);
                        populateClassIdsSpinner(professorUid, firstSubject);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
            }
        });
    }
        void saveImage() {
        try {

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

            String selectedTerm = term.getSelectedItem().toString();
            String selectedCourse = courses.getSelectedItem().toString().trim();
            String enteredClassId = classid.getSelectedItem().toString().trim();
            String profUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (selectedCourse.isEmpty() || enteredClassId.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter Course ID and Class ID", Toast.LENGTH_SHORT).show();
                return;
            }


            String qrData = getQRCodeData(selectedCourse, enteredClassId, selectedTerm, profUid);
            String currentDate = getCurrentDate();
            DatabaseReference qrDataRef = FirebaseDatabase.getInstance().getReference().child("QRdata");

            qrDataRef.child(profUid)
                    .child(selectedCourse)
                    .child(enteredClassId)
                    .child(selectedTerm)
                    .child(getCurrentDate())
                    .child("students")
                    .setValue("Present")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // QR code data saved successfully
                            Toast.makeText(getApplicationContext(), "QR code data saved", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to save QR code data
                            Toast.makeText(getApplicationContext(), "Failed to save QR code data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getQRCodeData(String selectedCourse, String enteredClassId, String selectedTerm, String profUid) {
        if (enrollSubData != null) {
            Map<String, Object> courseData = (Map<String, Object>) enrollSubData.get(selectedCourse);
            if (courseData != null) {
                Map<String, Object> classData = (Map<String, Object>) courseData.get(enteredClassId);
                if (classData != null) {
                    Object qrDataObj = classData.get(selectedTerm);
                    // Obtain a reference to the Firebase Realtime Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference profTrackerRef = database.getReference("profTracker");

                    if (qrDataObj != null) {
                        // Append profUid to the QR code data
                        String qrCodeData = qrDataObj.toString() + " Prof UID: " + profUid;

                        profTrackerRef.child(profUid).child(selectedCourse).child(enteredClassId).child(selectedTerm)
                                .child(getCurrentDate()).child("profUid").setValue(profUid)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Professor UID stored successfully
                                        Toast.makeText(getApplicationContext(), "Professor UID stored", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Failed to store professor UID
                                        Toast.makeText(getApplicationContext(), "Failed to store professor UID", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
            }
        }
        return ""; // Return empty string if QR code data is not found
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
}