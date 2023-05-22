package com.example.practice;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class addSubject extends AppCompatActivity {

    Spinner year, program, term, prof, days, section;
    EditText roomNumber, coursename, coursenumber, unit, startTime, endTime;
    Button save, showList, btimport;
    private DatabaseReference programsRef, reference, ref, schedref, classref, student;
    ArrayList<String> courseLists, profList, subjectList;
    private ArrayAdapter<String> courseAdapter, profAdapter, subAdapter;

    public static final int cellCount = 2;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        Button startTimeButton = findViewById(R.id.startTimePicker);
        Button endTimeButton = findViewById(R.id.endTimePicker);
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

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(startTimeButton);
            }
        });
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(endTimeButton);
            }
        });

        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(addSubject.this, showAllSubject.class);
                startActivity(i);
            }
        });

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
        NothingSelectedSpinnerAdapter yearLevelSpinnerAdapter = new NothingSelectedSpinnerAdapter(
                yearLevelAdapter,
                R.layout.spinner_prompt_item,
                this,
                "Select Year Level");
        year.setAdapter(yearLevelSpinnerAdapter);

        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this,
                R.array.program_options, android.R.layout.simple_spinner_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NothingSelectedSpinnerAdapter programSpinnerAdapter = new NothingSelectedSpinnerAdapter(
                programAdapter,
                R.layout.spinner_prompt_item,
                this,
                "Select Program");
        program.setAdapter(programSpinnerAdapter);

        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this,
                R.array.section_options, android.R.layout.simple_spinner_item);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NothingSelectedSpinnerAdapter nothingSelectedAdapter = new NothingSelectedSpinnerAdapter(
                sectionAdapter,
                R.layout.spinner_prompt_item,
                this,
                "Select Section");
        section.setAdapter(nothingSelectedAdapter);

        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this,
                R.array.term_options, android.R.layout.simple_spinner_item);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NothingSelectedSpinnerAdapter termSpinnerAdapter = new NothingSelectedSpinnerAdapter(
                termAdapter,
                R.layout.spinner_prompt_item,
                this,
                "Select Term");
        term.setAdapter(termSpinnerAdapter);

        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_options, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NothingSelectedSpinnerAdapter daySpinnerAdapter = new NothingSelectedSpinnerAdapter(
                dayAdapter,
                R.layout.spinner_prompt_item,
                this,
                "Select Day");
        days.setAdapter(daySpinnerAdapter);


        ref = FirebaseDatabase.getInstance().getReference();

        ref.child("profList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profList.clear(); // Clear the list before populating it again
                profList.add("Select Professor"); // Add the prompt message as the first item

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String spinner = childSnapshot.child("prof").getValue(String.class);
                    profList.add(spinner);
                }

                profAdapter = new ArrayAdapter<String>(addSubject.this, android.R.layout.simple_spinner_dropdown_item, profList);
                profAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                prof.setAdapter(profAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });


        // Add a click listener to the save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the values from the input fields

                String courseName = coursename.getText().toString();
                String units = unit.getText().toString();
                String startTime = startTimeButton.getText().toString();
                String endTime = endTimeButton.getText().toString();
                String courseCode = coursenumber.getText().toString();
                String course = courseCode + " " + courseName;
                String room = roomNumber.getText().toString();
                String programNames = (program.getSelectedItem() != null) ? program.getSelectedItem().toString() : "";
                String yearLevel = (year.getSelectedItem() != null) ? year.getSelectedItem().toString() : "";
                String terms = (term.getSelectedItem() != null) ? term.getSelectedItem().toString() : "";
                String professors = (prof.getSelectedItem() != null) ? prof.getSelectedItem().toString() : "";
                String day = (days.getSelectedItem() != null) ? days.getSelectedItem().toString() : "";
                String sections = (section.getSelectedItem() != null) ? section.getSelectedItem().toString() : "";

                // Perform validation checks
                if (courseCode.isEmpty() || courseName.isEmpty() || room.isEmpty() || units.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    // Display an error message if any of the fields are empty
                    showErrorDialog("Please fill in all the fields");
                } else if (programNames.equals("Select Program") || programNames.trim().isEmpty()) {
                    // Display an error message if the program spinner is not selected or the selected program name is empty
                    showErrorDialog("Please select a valid Program");
                } else if (yearLevel.equals("Select Year Level") || yearLevel.trim().isEmpty()) {
                    // Display an error message if the year level spinner is not selected or the selected year level is empty
                    showErrorDialog("Please select a valid year level");
                } else if (sections.equals("Select Section") || sections.trim().isEmpty()) {
                    // Display an error message if the section spinner is not selected or the selected section is empty
                    showErrorDialog("Please select a valid section");
                } else if (professors.equals("Select Professor") || professors.trim().isEmpty()) {
                    // Display an error message if the professor spinner is not selected or the selected professor is empty
                    showErrorDialog("Please select a valid professor");
                } else if (terms.equals("Select Term") || terms.trim().isEmpty()) {
                    // Display an error message if the term spinner is not selected or the selected term is empty
                    showErrorDialog("Please select a valid Term");
                } else if (day.equals("Select Day") || day.trim().isEmpty()) {
                    // Display an error message if the day spinner is not selected or the selected day is empty
                    showErrorDialog("Please select a valid day");
                } else if (startTime.equals("Select Start Time") || endTime.equals("Select End Time")) {
                    // Display an error message if the start time or end time is not selected
                    showErrorDialog("Please select a start time and end time");
                } else if (courseCode.trim().isEmpty() || courseName.trim().isEmpty() || room.trim().isEmpty()) {
                    // Display an error message if any of the EditText fields don't contain text
                    showErrorDialog("Please enter valid text for course code, course name, and room number");
                } else {
                    // Create a confirmation dialog
                    String confirmationMessage = "Course Code: " + courseCode +
                            "\nCourse Name: " + courseName +
                            "\nDay: " + day +
                            "\nTime: " + startTime + " - " + endTime +
                            "\nRoom Number: " + room +
                            "\nYear Level: " + yearLevel +
                            "\nSection: " + sections;

                    AlertDialog.Builder builder = new AlertDialog.Builder(addSubject.this);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to add this subject?\n\n" + confirmationMessage);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User confirmed, proceed with subject addition
                            programsRef = FirebaseDatabase.getInstance().getReference("subjectList");
                            student = FirebaseDatabase.getInstance().getReference("StudentAcc");

                            schedref = FirebaseDatabase.getInstance().getReference("studentSched");
                            subjectsList subject = new subjectsList(courseName, programNames, yearLevel, terms, professors, day, room, courseCode, units, startTime + " - " + endTime, sections);
                            studentSched sched = new studentSched(courseName, startTime + " - " + endTime, day, room);
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
                                    .setValue(sched)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Show toast message for successful addition
                                            Toast.makeText(addSubject.this, "Subject added successfully", Toast.LENGTH_SHORT).show();
                                            // Clear EditText fields
                                            coursename.getText().clear();
                                            unit.getText().clear();
                                            coursenumber.getText().clear();
                                            roomNumber.getText().clear();

                                            // Clear Spinners
                                            program.setSelection(0);
                                            year.setSelection(0);
                                            section.setSelection(0);
                                            prof.setSelection(0);
                                            term.setSelection(0);
                                            days.setSelection(0);

                                            // Clear time buttons
                                            startTimeButton.setText("Select Start Time");
                                            endTimeButton.setText("Select End Time");
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
                    builder.setNegativeButton("No", null);

                    // Show the confirmation dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


    }
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(addSubject.this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
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
    private void showTimePickerDialog(final Button button) {
        // Get the current time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Convert 24-hour format to 12-hour format
                        int hour = hourOfDay % 12;
                        String amPm = (hourOfDay < 12) ? "AM" : "PM";
                        if (hour == 0) {
                            hour = 12; // Handle midnight (0 hours)
                        }
                        // Set the selected time to the Button
                        String time = String.format("%02d:%02d %s", hour, minute, amPm);
                        button.setText(time);
                    }
                },
                hour,
                minute,
                false // Set to false to use 12-hour format
        );

        // Show the dialog
        timePickerDialog.show();
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