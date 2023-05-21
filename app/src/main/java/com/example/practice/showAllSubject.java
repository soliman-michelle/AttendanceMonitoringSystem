package com.example.practice;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.practice.R;
import com.example.practice.Subject;
import com.example.practice.SubjectAdapter;
import com.example.practice.schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class showAllSubject extends AppCompatActivity implements SubjectAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectList;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_subject);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        subjectList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(subjectList, this);
        recyclerView.setAdapter(subjectAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("studentSched");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectList.clear();
                for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                    String subjectName = scheduleSnapshot.getKey(); // Retrieve the subject name
                    Subject subject = new Subject(subjectName);
                    subjectList.add(subject);
                }
                subjectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(showAllSubject.this, "Failed to load subjects.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Subject subject = subjectList.get(position);
        Toast.makeText(this, "Clicked on: " + subject.getName(), Toast.LENGTH_SHORT).show();
    }
}
