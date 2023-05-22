package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class showAllStudentAttendance extends AppCompatActivity {
    private TableLayout tableLayout;
    private DatabaseReference attendanceRef;
    private TextView subjectTextView;
    private TextView dateTextView;

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_student_attendance);

        tableLayout = findViewById(R.id.tableLayout);
        subjectTextView = findViewById(R.id.subject);
        dateTextView = findViewById(R.id.date);

        // Retrieve the selected values from the intent
        Intent intent = getIntent();
        String professorId = intent.getStringExtra("professorId");
        String selectedSubject = intent.getStringExtra("subject");
        String selectedClassId = intent.getStringExtra("classId");
        String selectedTerm = intent.getStringExtra("term");
        String selectedDate = intent.getStringExtra("Date");
        List<String> attendanceDataList = intent.getStringArrayListExtra("attendanceDataList");

        // Set the subject and term in TextViews
        subjectTextView.setText(selectedSubject);
        dateTextView.setText(selectedDate);

        // Fetch the student attendance data from the database
        attendanceRef = FirebaseDatabase.getInstance().getReference().child("profTracker");

        Button exportButton = findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the permission is already granted
                if (ContextCompat.checkSelfPermission(showAllStudentAttendance.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(showAllStudentAttendance.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    // Permission is already granted, proceed with exporting to Excel
                    exportToExcel();
                }
            }
        });

        if (professorId != null && selectedSubject != null && selectedClassId != null && selectedTerm != null && selectedDate != null) {
            attendanceRef = attendanceRef.child(professorId)
                    .child(selectedSubject)
                    .child(selectedClassId)
                    .child(selectedTerm)
                    .child(selectedDate);
        } else {
            // Handle the case where any of the variables is null
            // For example, you can show an error message or take appropriate action
            return;
        }

        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Create the header row
                    TableRow headerRow = new TableRow(showAllStudentAttendance.this);

                    TextView nameHeaderTextView = new TextView(showAllStudentAttendance.this);
                    nameHeaderTextView.setText("Name");
                    headerRow.addView(nameHeaderTextView);

                    // Iterate through the dates and add them as headers
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String date = dateSnapshot.getKey(); // Get the date from the snapshot
                        TextView dateHeaderTextView = new TextView(showAllStudentAttendance.this);
                        dateHeaderTextView.setText(date);
                        headerRow.addView(dateHeaderTextView);
                    }

                    TextView totalHeaderTextView = new TextView(showAllStudentAttendance.this);
                    totalHeaderTextView.setText("Total");
                    headerRow.addView(totalHeaderTextView);

                    // Add the header row to the table layout
                    tableLayout.addView(headerRow);

                    // Iterate through the student attendance data
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        String studentName = studentSnapshot.child("name").getValue(String.class);

                        // Create a new row for each student
                        TableRow row = new TableRow(showAllStudentAttendance.this);

                        // Add the student's name to the row
                        TextView studentNameTextView = new TextView(showAllStudentAttendance.this);
                        studentNameTextView.setText(studentName);
                        row.addView(studentNameTextView);

                        int totalAttendance = 0; // Initialize total attendance count

                        // Iterate through the dates and add the status for each date
                        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                            String date = dateSnapshot.getKey(); // Get the date from the snapshot

                            String status = "Absent"; // Default status is "Absent"
                            DataSnapshot studentAttendanceSnapshot = dateSnapshot.child(studentName);
                            if (studentAttendanceSnapshot.exists()) {
                                status = studentAttendanceSnapshot.child("status").getValue(String.class);
                            }

                            // Create a TextView for each status
                            TextView attendanceStatusTextView = new TextView(showAllStudentAttendance.this);
                            attendanceStatusTextView.setText(status);
                            row.addView(attendanceStatusTextView);

                            if (status.equalsIgnoreCase("Present")) {
                                totalAttendance++; // Increment total attendance count if status is "Present"
                            }
                        }

                        // Add the total attendance count to the row
                        TextView totalAttendanceTextView = new TextView(showAllStudentAttendance.this);
                        totalAttendanceTextView.setText(String.valueOf(totalAttendance));
                        row.addView(totalAttendanceTextView);

                        // Add the row to the table layout
                        tableLayout.addView(row);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    // Check for permission and export to Excel
    private void checkPermissionAndExport() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            // Permission already granted, export to Excel
            exportToExcel();
        }
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, export to Excel
                exportToExcel();
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

// Rest of your code...

    private void exportToExcel() {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        // Get the total number of rows and columns in the table layout
        int rowCount = tableLayout.getChildCount();
        int columnCount = ((TableRow) tableLayout.getChildAt(0)).getChildCount();

        // Iterate through the rows and columns and add the data to the Excel sheet
        for (int i = 0; i < rowCount; i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            Row excelRow = sheet.createRow(i);

            for (int j = 0; j < columnCount; j++) {
                TextView textView = (TextView) row.getChildAt(j);
                String text = textView.getText().toString();

                Cell cell = excelRow.createCell(j);
                cell.setCellValue(text);
            }
        }

        // Save the workbook to a file
        String fileName = "attendance.csv";
        File directory = new File(Environment.getExternalStorageDirectory() + "/MyDirectory/");
        String filePath = getExternalFilesDir(null) + "/" + fileName;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();

            Toast.makeText(this, "Exported to " + filePath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed exported ", Toast.LENGTH_SHORT).show();

        }
    }
}
