    package com.example.practice;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.widget.EditText;

    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.List;

    public class showStudentList extends AppCompatActivity {
        private final List<studentList> itemsList = new ArrayList<>();
        private final List<studentList> filteredItemList = new ArrayList<>();
        private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        private studentAdapter adapter;
        private EditText searchEditText;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_student_list);

            final RecyclerView recyclerView = findViewById(R.id.recyclerViews);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(showStudentList.this));

            searchEditText = findViewById(R.id.searchEditText);
            adapter = new studentAdapter(filteredItemList, showStudentList.this);
            recyclerView.setAdapter(adapter);

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
                                    String fullname = fname + " " + mname + " " + lname;

                                    studentList student = new studentList(fullname, studNum);
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
                filteredItemList.clear();

                for (studentList student : itemsList) {
                    // Filter based on the search text
                    if (student.getFullname().toLowerCase().contains(searchText) || student.getStudNum().toLowerCase().contains(searchText)) {
                        filteredItemList.add(student);
                    }
                }

                adapter.notifyDataSetChanged();
            }


    }

