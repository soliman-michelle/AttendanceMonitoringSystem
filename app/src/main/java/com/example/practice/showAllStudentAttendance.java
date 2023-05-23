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
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.ListView;
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
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;

    public class showAllStudentAttendance extends AppCompatActivity {
        private ListView listView;
        private DatabaseReference attendanceRef;
        private TextView subjectTextView;
        private TextView dateTextView;
        private List<AttendanceRecord> attendanceList;
        private AttendanceAdapter adapter;

        private static final int REQUEST_CODE = 1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_all_student_attendance);

            listView = findViewById(R.id.listView);
            subjectTextView = findViewById(R.id.subject);
            dateTextView = findViewById(R.id.date);
            attendanceList = new ArrayList<>();
            listView.setAdapter(adapter);


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

            attendanceRef = FirebaseDatabase.getInstance().getReference().child("AttendanceRecord");

            attendanceRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    attendanceList.clear();

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String studentName = childSnapshot.getKey();
                        AttendanceRecord attendanceRecord = childSnapshot.getValue(AttendanceRecord.class);
                        attendanceList.add(attendanceRecord);
                    }

                    adapter = new AttendanceAdapter(showAllStudentAttendance.this, attendanceList);
                    listView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(showAllStudentAttendance.this, "Failed to retrieve attendance records", Toast.LENGTH_SHORT).show();
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
                AttendanceRecord attendanceRecord = attendanceList.get(i);
                String studentName = attendanceRecord.getStudentName();

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
