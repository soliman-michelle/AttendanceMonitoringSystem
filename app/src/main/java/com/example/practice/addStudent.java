    package com.example.practice;

    import org.apache.poi.ss.usermodel.*;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    import java.io.InputStream;
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
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;


    public class addStudent extends AppCompatActivity {
        EditText fname, mname, lname, phone, email, studnum, df;
        private Spinner course, year, block;
        DatabaseReference reference;
        String regex = "(^((09|\\+639))(\\d{9})$)";
        String regexEmail = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        Button save, btimport, show;
        public static final int cellCount=2;
        private ActivityResultLauncher<Intent> filePickerLauncher;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_student);

            fname = (EditText) findViewById(R.id.fname);
            mname= (EditText) findViewById(R.id.mname);
            lname = (EditText) findViewById(R.id.lname);
            phone = (EditText) findViewById(R.id.editTextPhone);
            email= (EditText) findViewById(R.id.email);
            studnum = (EditText) findViewById(R.id.userStudentNumber);
            year = (Spinner) findViewById(R.id.yearLevelSpinner);
            course = (Spinner) findViewById(R.id.courseSpinner);
            block = (Spinner) findViewById(R.id.block);
            btimport = findViewById(R.id.btimport);

            save = findViewById(R.id.save);
            show = findViewById(R.id.show);
            df = findViewById(R.id.dfpass);
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

            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(addStudent.this, showStudents.class);
                    startActivity(intent);
                }
            });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    check();

                }
            });

            ArrayAdapter<CharSequence> yearLevelAdapter = ArrayAdapter.createFromResource(this,
                    R.array.year_options, android.R.layout.simple_spinner_item);
            yearLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            year.setAdapter(yearLevelAdapter);

            ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this,
                    R.array.section_options, android.R.layout.simple_spinner_item);
            sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            block.setAdapter(sectionAdapter);

            ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this,
                    R.array.program_options, android.R.layout.simple_spinner_item);
            programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            course.setAdapter(programAdapter);

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
                Toast.makeText(addStudent.this, message, Toast.LENGTH_SHORT).show();
            }

        private Boolean validateFirstName() {
            String val = fname.getText().toString();

            if (val.isEmpty()) {
                fname.setError("Field cannot be Empty!");
                return false;
            } else {
                fname.setError(null);
                return true;
            }
        }
        private Boolean validatePhone() {
            String val = phone.getText().toString();

            if (val.isEmpty()) {
                phone.setError("Field cannot be Empty!");
                return false;
            } else if (!val.matches(regex)){
                phone.setError("Should start at 09 or +639 and must be 11 digits");
                return false;
            }else {
                phone.setError(null);
                return true;
            }
        }
        private Boolean validateMiddleName() {
            String val = mname.getText().toString();

            if (val.isEmpty()) {
                mname.setError("Field cannot be Empty!");
                return false;
            } else {
                mname.setError(null);
                return true;
            }
        }
        private Boolean validateStudNum() {
            String val = studnum.getText().toString();

            if (val.isEmpty()) {
                studnum.setError("Field Cannot be Empty!");
                return false;
            } else if (val.length() < 8) {
                studnum.setError("Student number is too short!");
                return false;
            } else if (val.length() > 8) {
                studnum.setError("Student number is too long!");
                return false;
            } else if (!(val.matches("^[0-9]{3}-[0-9]{4}$") || val.equals("000-0000"))) {
                studnum.setError("Student number must be in number format of ###-####");
                return false;
            } else {
                studnum.setError(null);
                return true;
            }
        }
        private Boolean validateLastName() {
            String val = lname.getText().toString();

            if (val.isEmpty()) {
                lname.setError("Field cannot be Empty!");
                return false;
            } else {
                lname.setError(null);
                return true;
            }
        }
        private Boolean validatePassword() {
            String val = df.getText().toString();

            if (val.isEmpty()) {
                df.setError("Field cannot be Empty!");
                return false;
            } else if (val.length() < 6) {
                df.setError("Password is too weak");
                return false;
            } else {
                df.setError(null);
                return true;
            }
        }
        private Boolean validateEmail() {
            String val = email.getText().toString();

            if (val.isEmpty()) {
                email.setError("Field cannot be Empty!");
                return false;
            } else if (!val.matches(regexEmail)) {
                email.setError("Please Enter Valid Email Address");
                return false;
            } else {
                email.setError(null);
                return true;
            }
        }
        private void check() {
            if (!validateFirstName() | !validateMiddleName() | !validateLastName() | !validatePassword() | !validateEmail() | !validateStudNum() | !validatePhone()) {
                return;
            } else {
                isUser();
            }
        }

        private void isUser() {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            String firstname = fname.getText().toString();
            String middlename = mname.getText().toString();
            String lastname = lname.getText().toString();
            String user = studnum.getText().toString();
            String phonenum = phone.getText().toString();
            String pass = df.getText().toString();
            String yearLevelStr = year.getSelectedItem().toString();
            String sectionStr = block.getSelectedItem().toString();
            String courseStr = course.getSelectedItem().toString();
            String useremail = email.getText().toString();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            mAuth.createUserWithEmailAndPassword(user + "@gmail.com", pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser users = mAuth.getCurrentUser();

                                StudentAcc student = new StudentAcc(firstname, middlename, lastname, user, phonenum, pass, useremail, courseStr, yearLevelStr, sectionStr);
                                profiledb profile = new profiledb(firstname, middlename, lastname, user, phonenum, pass, useremail, courseStr, yearLevelStr, sectionStr);
                                databaseRef.child("StudentAcc")
                                        .child(courseStr)
                                        .child(yearLevelStr)
                                        .child(sectionStr)
                                        .child(user)
                                        .setValue(student);

                                databaseRef.child("profiledb")
                                        .child(users.getUid())
                                        .setValue(profile);

                                Toast.makeText(addStudent.this, "Student added", Toast.LENGTH_SHORT).show();
                                fname.setText("");
                                mname.setText("");
                                lname.setText("");
                                studnum.setText("");
                                phone.setText("");
                                df.setText("");
                                email.setText("");
                            } else {
                                Toast.makeText(addStudent.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        public void importExcelFile(Uri uri) {
            try {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                InputStream inputStream = getContentResolver().openInputStream(uri);
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    String course = getStringCellValue(row.getCell(0));
                    String year = getStringCellValue(row.getCell(1));
                    String section = getStringCellValue(row.getCell(2));
                    String studnum = getStringCellValue(row.getCell(3));
                    String lname = getStringCellValue(row.getCell(4));
                    String fname = getStringCellValue(row.getCell(5));
                    String mname = getStringCellValue(row.getCell(6));
                    String defaultpass = getStringCellValue(row.getCell(7));
                    String email = getStringCellValue(row.getCell(8));
                    String phone = getStringCellValue(row.getCell(9));

                    mAuth.createUserWithEmailAndPassword(studnum + "@gmail.com", defaultpass)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String studentId = user.getUid();

                                        // Save the user to the StudentAcc node
                                        StudentAcc student = new StudentAcc(fname, mname, lname, studnum, phone, defaultpass, email, course, year, section);
                                        databaseRef.child("StudentAcc")
                                                .child(course)
                                                .child(year)
                                                .child(section)
                                                .child(studnum)
                                                .setValue(student);

                                        // Save the user to the profiledb node with UID as key
                                        profiledb profile = new profiledb(fname, mname, lname, studnum, phone, defaultpass, email, course, year, section);
                                        databaseRef.child("profiledb")
                                                .child(studentId)
                                                .setValue(profile);

                                        Toast.makeText(addStudent.this, "Student added Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(addStudent.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                workbook.close();
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getStringCellValue(Cell cell) {
            if (cell == null) {
                return "";
            } else if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                // Convert numeric value to string
                return String.valueOf((int) cell.getNumericCellValue());
            } else {
                return "";
            }
        }


    }