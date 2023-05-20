package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class masterList extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TableLayout tableLayout;
    private String selectedSubject;
    private String selectedClassId;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_list);

        tableLayout = findViewById(R.id.tableLayout);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        searchEditText = findViewById(R.id.searchEditText);

        Intent intent = getIntent();
        selectedSubject = intent.getStringExtra("subject");
        selectedClassId = intent.getStringExtra("classId");

        if (selectedSubject != null && selectedClassId != null) {
            retrieveDataFromFirebase();
        } else {
            Toast.makeText(this, "Invalid subject or class ID", Toast.LENGTH_SHORT).show();
        }

        // Add a TextWatcher to listen for changes in the search query
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No implementation needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterTableRows(s.toString());
            }
        });
    }

    private void retrieveDataFromFirebase() {
        String professorUid = getCurrentProfessorUid();
        if (professorUid != null) {
            DatabaseReference enrollSubRef = databaseReference.child("enrollSub")
                    .child(professorUid)
                    .child(selectedSubject)
                    .child(selectedClassId);

            enrollSubRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tableLayout.removeAllViews(); // Clear existing rows

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                            String uid = studentSnapshot.getKey();
                            String name = studentSnapshot.child("name").getValue(String.class);
                            addTableRow(name, uid);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error if necessary
                }
            });
        } else {
            Toast.makeText(this, "Invalid professor UID", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTableRow(String name, String uid) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        row.setLayoutParams(layoutParams);

        TextView nameTextView = new TextView(this);
        nameTextView.setText(name);
        nameTextView.setPadding(8, 8, 8, 8);
        row.addView(nameTextView);

        TextView uidTextView = new TextView(this);
        uidTextView.setText(uid);
        uidTextView.setPadding(8, 8, 8, 8);
        row.addView(uidTextView);

        tableLayout.addView(row);
    }

    private String getCurrentProfessorUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    private void filterTableRows(String query) {
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            TextView nameTextView = (TextView) row.getChildAt(0);
            TextView uidTextView = (TextView) row.getChildAt(1);

            String name = nameTextView.getText().toString();
            String uid = uidTextView.getText().toString();

            if (name.toLowerCase().contains(query.toLowerCase()) || uid.toLowerCase().contains(query.toLowerCase())) {
                row.setVisibility(View.VISIBLE);
            } else {
                row.setVisibility(View.GONE);
            }
        }
    }

}
