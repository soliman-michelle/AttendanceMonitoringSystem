package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.practice.studentData;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class showAllStudentAttendance extends AppCompatActivity {
    private ListView listView;
    private DatabaseReference attendanceRef;
    private TextView subjectTextView;
    private TextView dateTextView;
    private List<studentData> attendanceList;
    private AttendanceAdapter adapter;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_student_attendance);

        listView = findViewById(R.id.listView);
        subjectTextView = findViewById(R.id.subject);
        dateTextView = findViewById(R.id.date);
        attendanceList = new ArrayList<studentData>();

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
        adapter = new AttendanceAdapter(this, attendanceList);
        listView.setAdapter(adapter);
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
                    // Clear the attendance list before populating it with new data
                    attendanceList.clear();

                    // Iterate through the filtered data
                    for (DataSnapshot uidSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot classSnapshot : uidSnapshot.getChildren()) {
                            for (DataSnapshot subjectSnapshot : classSnapshot.getChildren()) {
                                for (DataSnapshot termSnapshot : subjectSnapshot.getChildren()) {
                                    for (DataSnapshot dateSnapshot : termSnapshot.getChildren()) {
                                        for (DataSnapshot studentIdSnapshot : dateSnapshot.getChildren()) {
                                            for (DataSnapshot studentSnapshot : studentIdSnapshot.getChildren()) {
                                                // Get the attendance record for the student
                                                String arrival = studentSnapshot.child("arrival").getValue(String.class);
                                                String date = studentSnapshot.child("date").getValue(String.class);
                                                String name = studentSnapshot.child("name").getValue(String.class);
                                                String status = studentSnapshot.child("status").getValue(String.class);
                                                String studNum = studentSnapshot.child("studNum").getValue(String.class);

                                                // Create a studentData object
                                                studentData student = new studentData(studNum, status, name, date, arrival);

                                                attendanceList.add(student);
                                                Log.d("AttendanceDebug", "Retrieved student: " + student.getName());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Create the adapter and set it to the ListView
                    adapter = new AttendanceAdapter(showAllStudentAttendance.this, attendanceList);
                    listView.setAdapter(adapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

    }
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

    private void exportToExcel() {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        // Get the total number of items in the attendance list
        int itemCount = attendanceList.size();

        // Iterate through the items and add them to the Excel sheet
        for (int i = 0; i < itemCount; i++) {
            studentData attendanceRecord = attendanceList.get(i);
            String studentName = attendanceRecord.getName();

            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(studentName);

        }

        // Save the workbook to a file
        String fileName = "attendance.xlsx";
        File directory = new File(Environment.getExternalStorageDirectory() + "/MyDirectory/");
        String filePath = directory.getAbsolutePath() + "/" + fileName;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();

            Toast.makeText(this, "Exported to " + filePath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export.", Toast.LENGTH_SHORT).show();
        }

        // Close the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
