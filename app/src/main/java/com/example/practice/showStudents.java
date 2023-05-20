    package com.example.practice;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.Spinner;
    import android.widget.Toast;

    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;

    public class showStudents extends AppCompatActivity {
        private Spinner course, year, block;
        ArrayList<String> courseLists;
        private ArrayAdapter<String> courseAdapters;
        Button show;
        DatabaseReference reference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_students);

            year = (Spinner) findViewById(R.id.yearLevelSpinner);
            course = (Spinner) findViewById(R.id.courseSpinner);
            block = (Spinner) findViewById(R.id.block);
            show = findViewById(R.id.show);
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

            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectedCourse = course.getSelectedItem().toString();
                    String selectedYear = year.getSelectedItem().toString();
                    String selectedBlock = block.getSelectedItem().toString();
                    reference = FirebaseDatabase.getInstance().getReference();

                    DatabaseReference studentAccRef = reference.child("StudentAcc")
                            .child(selectedCourse)
                            .child(selectedYear)
                            .child(selectedBlock);

                    studentAccRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ArrayList<studentList> studentList = new ArrayList<>();

                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    String fname = childSnapshot.child("fname").getValue(String.class);
                                    String lname = childSnapshot.child("lname").getValue(String.class);
                                    String mname = childSnapshot.child("mname").getValue(String.class);
                                    String fullname = fname + " " + mname + " " + lname;
                                    String yearLevel = childSnapshot.child("year").getValue(String.class);
                                    String section = childSnapshot.child("section").getValue(String.class);
                                    String email = childSnapshot.child("email").getValue(String.class);
                                    String defaultpass = childSnapshot.child("defaultpass").getValue(String.class);
                                    String phone = childSnapshot.child("phone").getValue(String.class);
                                    String studNum = childSnapshot.child("studnum").getValue(String.class);

                                    studentList student = new studentList(fullname, studNum, yearLevel, section, email, defaultpass, phone);
                                    studentList.add(student);
                                }

                                Intent intent = new Intent(showStudents.this, showStudentList.class);
                                intent.putExtra("selectedCourse", selectedCourse);
                                intent.putExtra("selectedYear", selectedYear);
                                intent.putExtra("selectedBlock", selectedBlock);
                                intent.putParcelableArrayListExtra("studentList", studentList);
                                startActivity(intent);
                            } else {
                                // Handle the case when no students are found
                                Toast.makeText(showStudents.this, "No students found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle the error if necessary
                        }
                    });

                }
            });

        }
    }