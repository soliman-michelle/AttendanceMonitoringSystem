    package com.example.practice;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.EditText;
    import android.widget.ListView;

    import com.example.practice.databinding.ActivityShowStudentListBinding;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.List;

    public class showStudentList extends AdminDrawable {
        private List<studentList> itemsList;
        private List<studentList> filteredItemList;
        private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        private studentAdapter adapter;
        ActivityShowStudentListBinding activityShowStudentListBinding;
        private EditText searchEditText;
        private ListView listView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            activityShowStudentListBinding = ActivityShowStudentListBinding.inflate(getLayoutInflater());
            setContentView(activityShowStudentListBinding.getRoot());

            itemsList = new ArrayList<>();
            filteredItemList = new ArrayList<>();

            searchEditText = findViewById(R.id.searchEditText);
            listView = findViewById(R.id.listView);

            adapter = new studentAdapter(this, itemsList, filteredItemList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected student
                    studentList selectedStudent = filteredItemList.get(position);

                    Intent intent = new Intent(showStudentList.this, UpdateStudentInfo.class);
                    intent.putExtra("fullname", selectedStudent.getFullname());
                    intent.putExtra("studNum", selectedStudent.getStudNum());
                    intent.putExtra("defaultpass", selectedStudent.getDefaultpass());
                    intent.putExtra("email", selectedStudent.getEmail());
                    intent.putExtra("phone", selectedStudent.getPhone());
                    intent.putExtra("section", selectedStudent.getSection());
                    intent.putExtra("yearLevel", selectedStudent.getYearLevel());
                    startActivity(intent);

                }
            });

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    itemsList.clear();

                    DatabaseReference studentAccRef = databaseReference.child("StudentAcc");

                    for (DataSnapshot courseSnapshot : snapshot.child("StudentAcc").getChildren()) {
                        for (DataSnapshot yearSnapshot : courseSnapshot.getChildren()) {
                            for (DataSnapshot blockSnapshot : yearSnapshot.getChildren()) {
                                for (DataSnapshot studentSnapshot : blockSnapshot.getChildren()) {
                                    String fname = studentSnapshot.child("fname").getValue(String.class);
                                    String lname = studentSnapshot.child("lname").getValue(String.class);
                                    String mname = studentSnapshot.child("mname").getValue(String.class);
                                    String studNum = studentSnapshot.child("studnum").getValue(String.class);
                                    String yearLevel = studentSnapshot.child("year").getValue(String.class);
                                    String section = studentSnapshot.child("section").getValue(String.class);
                                    String email = studentSnapshot.child("email").getValue(String.class);
                                    String defaultpass = studentSnapshot.child("defaultpass").getValue(String.class);
                                    String phone = studentSnapshot.child("phone").getValue(String.class);

                                    String fullname = fname + " " + mname + " " + lname;

                                    studentList student = new studentList(fullname, studNum, yearLevel, section, email, defaultpass, phone);
                                    itemsList.add(student);
                                }
                            }
                        }
                    }
                    filterItems();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error if necessary
                }
            });

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Not needed for this implementation
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Update the filtered list when the text changes
                    filterItems();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }

        private void filterItems() {
            String searchText = searchEditText.getText().toString().toLowerCase().trim();
            filteredItemList.clear(); // Clear the previous filtered items

            for (studentList student : itemsList) {
                if (student.getFullname().toLowerCase().contains(searchText) || student.getStudNum().toLowerCase().contains(searchText)) {
                    filteredItemList.add(student);
                }
            }

            adapter.setFilteredItemList(filteredItemList);
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            Intent intent = new Intent(this, showStudents.class);
            startActivity(intent);
            finish();
        }

        // Add this method to remove a student from the filteredItemList
        private void removeStudentFromFilteredItemList(studentList student) {
            filteredItemList.remove(student);
            adapter.notifyDataSetChanged();
        }
    }
