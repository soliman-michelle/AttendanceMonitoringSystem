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
import android.widget.ProgressBar;
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

public class addProf extends AppCompatActivity {
    EditText fname, mname, lname, phone, dfpass, email;
    Button btimport, save, show;
    ProgressBar progressBar;
    String regex = "(^((09|\\+639))(\\d{9})$)";
    String regexEmail = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
    public static final int cellCount=2;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prof);
        fname = findViewById(R.id.fname);
        mname = findViewById(R.id.mname);
        lname = findViewById(R.id.lname);
        phone = findViewById(R.id.editTextPhone);
        dfpass = findViewById(R.id.editTextPassword);
        email = findViewById(R.id.email);

        btimport = findViewById(R.id.btimport);
        save = findViewById(R.id.save);
        show = findViewById(R.id.show);
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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addProf.this, showProfList.class);
                startActivity(intent);
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
        Toast.makeText(addProf.this, message, Toast.LENGTH_SHORT).show();
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
        String val = dfpass.getText().toString();

        if (val.isEmpty()) {
            dfpass.setError("Field cannot be Empty!");
            return false;
        } else if (val.length() < 6) {
            dfpass.setError("Password is too weak");
            return false;
        } else {
            dfpass.setError(null);
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
        if (!validateFirstName() | !validateMiddleName() | !validateLastName() | !validatePassword() | !validateEmail() | !validatePhone()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {
        String firstname = fname.getText().toString();
        String middlename = mname.getText().toString();
        String lastname = lname.getText().toString();
        String user =  firstname + " " + lastname;
        String phonenum = phone.getText().toString();
        String pass = dfpass.getText().toString();
        String useremail = email.getText().toString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(useremail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser users = mAuth.getCurrentUser();

                            // Add user data to Firebase Realtime Database
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("addProfessors");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("profList");

                            ref.orderByChild("Professor").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // Course already exists, show an error message or do nothing
                                        Toast.makeText(addProf.this, "Prof already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Course does not exist, save it to the database
                                        profList prof = new profList(user);
                                        ref.child(user).setValue(prof).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(addProf.this, "Prof already exists", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(addProf.this, "Error checking if course exists", Toast.LENGTH_SHORT).show();
                                }
                            });

                            addProfessors professor = new addProfessors(firstname, middlename, lastname, user, phonenum, pass, useremail);
                            databaseRef.child(users.getUid()).setValue(professor);
                            Toast.makeText(addProf.this, "Professor added", Toast.LENGTH_SHORT).show();
                            fname.setText("");
                            mname.setText("");
                            lname.setText("");
                            phone.setText("");
                            dfpass.setText("");
                            email.setText("");
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(addProf.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                String pass = getStringCellValue(row.getCell(0));
                String useremail = getStringCellValue(row.getCell(1));
                String firstname = getStringCellValue(row.getCell(2));
                String  middlename= getStringCellValue(row.getCell(3));
                String  lastname= getStringCellValue(row.getCell(4));
                String  phonenum= getStringCellValue(row.getCell(5));
                String  users= getStringCellValue(row.getCell(6));
                mAuth.createUserWithEmailAndPassword(useremail, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    addProfessors professor = new addProfessors(firstname, middlename, lastname, users, phonenum, pass, useremail);
                                    databaseRef.child("addProfessors").child(user.getUid()).setValue(professor);

                                    profList prof = new profList(users);
                                    databaseRef.child("profList").child(users).setValue(prof);

                                    Toast.makeText(addProf.this, "Professor added Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(addProf.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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