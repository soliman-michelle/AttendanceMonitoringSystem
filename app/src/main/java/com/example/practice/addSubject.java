package com.example.practice;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class addSubject extends AppCompatActivity {

    Spinner year, program, term, prof, days, section;
    EditText roomNumber, coursename, coursenumber, unit, starttime, endTime;
    Button save, showList, btimport;
    private DatabaseReference programsRef, reference, ref , schedref, classref, student;
    ArrayList<String> courseLists, profList, subjectList;
    private ArrayAdapter<String> courseAdapters, profAdapter, subAdapter;

    public static final int cellCount=2;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);


        starttime = findViewById(R.id.editstartTime);
        endTime = findViewById(R.id.editendTime);
        days = findViewById(R.id.days);
        prof = findViewById(R.id.prof);
        term = findViewById(R.id.term);
        program = findViewById(R.id.program);
        year = findViewById(R.id.year);
        section = findViewById(R.id.section);
        save = findViewById(R.id.save);
        showList = findViewById(R.id.show);
        unit = findViewById(R.id.unit);
        coursenumber = findViewById(R.id.code);
        coursename = findViewById(R.id.subjectName);
        roomNumber = findViewById(R.id.room);
        btimport = findViewById(R.id.btimport);


        courseLists = new ArrayList<>();
        profList = new ArrayList<>();
        subjectList = new ArrayList<>();
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            if (result.getData() != null) {
                                Uri uri = result.getData().getData();
                                importExcelFile(uri);
                            } else {
                                showToast("No file selected");
                            }
                        } else {
                            showToast("File picking cancelled");
                        }
                    }
                });


        btimport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Use the correct MIME type for .xlsx files

                filePickerLauncher.launch(intent);
            }
        });

        ArrayAdapter<CharSequence> yearLevelAdapter = ArrayAdapter.createFromResource(this,
                R.array.year_options, android.R.layout.simple_spinner_item);
        yearLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(yearLevelAdapter);

        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this,
                R.array.term_options, android.R.layout.simple_spinner_item);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        term.setAdapter(termAdapter);

        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_options, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this,
                R.array.section_options, android.R.layout.simple_spinner_item);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        section.setAdapter(sectionAdapter);
        ref = FirebaseDatabase.getInstance().getReference();

        ref.child("profList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String spinner = childSnapshot.child("prof").getValue(String.class);
                    profList.add(spinner);

                    profAdapter = new ArrayAdapter<String>(addSubject.this, android.R.layout.simple_spinner_dropdown_item, profList);
                    profAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    prof.setAdapter(profAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("programList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String spinner = childSnapshot.child("program").getValue(String.class);
                    courseLists.add(spinner);

                    courseAdapters = new ArrayAdapter<String>(addSubject.this, android.R.layout.simple_spinner_dropdown_item, courseLists);
                    courseAdapters.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    program.setAdapter(courseAdapters);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                programsRef = FirebaseDatabase.getInstance().getReference("subjectList");
                student = FirebaseDatabase.getInstance().getReference("StudentAcc");

                schedref = FirebaseDatabase.getInstance().getReference("studentSched");
                String courseName = coursename.getText().toString();
                String units = unit.getText().toString();
                String time = starttime.getText().toString() + " - " + endTime.getText().toString();
                String courseCode= coursenumber.getText().toString();
                String course = coursenumber.getText().toString() + " " + coursename.getText().toString();
                String room = roomNumber.getText().toString();
                String yearLevel = year.getSelectedItem().toString();
                String programNames = program.getSelectedItem().toString();
                String professors = prof.getSelectedItem().toString();
                String terms = term.getSelectedItem().toString();
                String day = days.getSelectedItem().toString();
                String sections = section.getSelectedItem().toString();
                subjectsList subject = new subjectsList(courseName, programNames, yearLevel, terms, professors, day, room, courseCode, units,time, sections);
                studentSched sched = new studentSched(courseName, time, day, room );
                subList sub = new subList(course);
                classref = FirebaseDatabase.getInstance().getReference("subList");

                classref.child(course).setValue(sub);
                schedref.child(course)
                        .child(day).setValue(sched);
                // Write the new section to the database
                programsRef.child(programNames)
                        .child(yearLevel)
                        .child(sections)
                        .child(course)
                        .child(day)
                        .setValue(sched).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Show toast message for successful addition
                                Toast.makeText(addSubject.this, "Subject added successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Show toast message for failure
                                Toast.makeText(addSubject.this, "Failed to add subject", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
            }

    private String getPathFromUri(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }

    private void showToast(String message) {
        Toast.makeText(addSubject.this, message, Toast.LENGTH_SHORT).show();
    }

    public void importExcelFile(Uri uri) {
        try {
            programsRef = FirebaseDatabase.getInstance().getReference("subjectList");
            schedref = FirebaseDatabase.getInstance().getReference("studentSched");
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            InputStream inputStream = getContentResolver().openInputStream(uri);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // Retrieve values from Excel sheet
                String programNames = getStringCellValue(row.getCell(0));
                String yearLevel = getStringCellValue(row.getCell(1));
                String professors = getStringCellValue(row.getCell(2));
                String terms = getStringCellValue(row.getCell(3));
                String courseName = getStringCellValue(row.getCell(4));
                String courseCode = getStringCellValue(row.getCell(5));
                String units = getStringCellValue(row.getCell(6));
                String day = getStringCellValue(row.getCell(7));
                String stime = getStringCellValue(row.getCell(8));
                String etime = getStringCellValue(row.getCell(9));
                String time = stime + " - " + etime;
                String course = courseCode + " " + courseName;
                String room = getStringCellValue(row.getCell(10));
                String section = getStringCellValue(row.getCell(11));
                String modifiedYearLevel = yearLevel;
                if (modifiedYearLevel.contains(".")) {
                    modifiedYearLevel = modifiedYearLevel.split("\\.")[0];
                }

                String modifiedUnits = units;
                if (modifiedUnits.contains(".")) {
                    modifiedUnits = modifiedUnits.split("\\.")[0];
                }

                final String courseKey = course;
                final String finalYearLevel = modifiedYearLevel;
                final String finalUnits = modifiedUnits;

                // Check if the data already exists
                DatabaseReference subjectRef = programsRef.child(professors)
                        .child(terms)
                        .child(day)
                        .child(course);
                subjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Data already exists, display a message
                            showToast("Data for " + courseKey + " already exists");
                        } else {
                            // Data doesn't exist, save the new data
                            subjectsList subject = new subjectsList(courseName, programNames, finalYearLevel, terms, professors, day, room, courseCode, finalUnits, time, section);
                            studentSched sched = new studentSched(courseName, room, time, day);
                            subList sub = new subList(course);
                            classref = FirebaseDatabase.getInstance().getReference("subList");

                            classref.child(course).setValue(sub);
                            schedref.child(course)
                                    .child(day).setValue(sched);
                            programsRef.child(professors)
                                    .child(terms)
                                    .child(day)
                                    .child(course)
                                    .setValue(subject);

                            showToast("Data added successfully for " + courseKey);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error if needed
                    }
                });
            }

            workbook.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error importing data");
        }
    }


    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        } else if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // Check if the numeric value represents a date/time
            if (DateUtil.isCellDateFormatted(cell)) {
                // Convert date/time value to string using a desired format
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                return dateFormat.format(cell.getDateCellValue());
            } else {
                // Convert other numeric values to string
                return String.valueOf(cell.getNumericCellValue());
            }
        } else {
            return "";
        }
    }

}