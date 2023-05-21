package com.example.practice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class studentAdapter extends BaseAdapter {
    private List<studentList> items;
    private LayoutInflater inflater;
    private Context context;
    private DatabaseReference databaseReference;
    private List<studentList> filteredItemList;

    public studentAdapter(Context context, List<studentList> items, List<studentList> filteredItemList) {
        this.items = items;
        this.context = context;
        this.filteredItemList = filteredItemList;
        inflater = LayoutInflater.from(context);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_student, parent, false);
        }

        TextView fullNameTextView = convertView.findViewById(R.id.fullNameTextView);
        TextView studentNumberTextView = convertView.findViewById(R.id.studentNumberTextView);
        TextView yearLevelTextView = convertView.findViewById(R.id.yearLevelTextView);
        ImageView menuImageView = convertView.findViewById(R.id.menuImageView);
        TextView sectionTextView = convertView.findViewById(R.id.sectionTextView);
        studentList student = filteredItemList.get(position);

        fullNameTextView.setText(student.getFullname());
        studentNumberTextView.setText(student.getStudNum());
        yearLevelTextView.setText(student.getYearLevel());
        sectionTextView.setText(student.getSection());
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, position);
            }
        });

        return convertView;
    }
    private void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.item_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        deleteStudent(position);
                        return true;
                    case R.id.menu_update:
                        updateStudent(position);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    public void setFilteredItemList(List<studentList> filteredItemList) {
        this.filteredItemList = filteredItemList;
        notifyDataSetChanged();
    }

    private void deleteStudent(final int position) {
        if (position >= 0 && position < filteredItemList.size()) {
            studentList student = filteredItemList.get(position);
            String year = student.getYearLevel();
            String section = student.getSection();
            String studNum = student.getStudNum();

            // Build the confirmation dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to delete this student?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseReference studentRef = databaseReference.child("StudentAcc").child("BSCS")
                            .child(year).child(section).child(studNum);

                    studentRef.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                // Find the position of the item in the original items list
                                int originalPosition = items.indexOf(student);

                                if (originalPosition >= 0) {
                                    // Remove the item from both the items list and the filteredItemList
                                    items.remove(originalPosition);
                                    filteredItemList.remove(position);
                                    notifyDataSetChanged();
                                }
                            } else {
                                // Handle the error if necessary
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("No", null);

            // Show the dialog box
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private void updateStudent(int position) {
        if (position >= 0 && position < filteredItemList.size()) {
            // Get the selected student
            studentList student = filteredItemList.get(position);

            // Pass the student data to the update activity
            Intent intent = new Intent(context, UpdateStudentInfo.class);
            intent.putExtra("fullname", student.getFullname());
            intent.putExtra("studNum", student.getStudNum());
            intent.putExtra("yearLevel", student.getYearLevel());
            intent.putExtra("section", student.getSection());
            intent.putExtra("email", student.getEmail());
            intent.putExtra("phone", student.getPhone());
            intent.putExtra("defaultpass", student.getDefaultpass());

            context.startActivity(intent);
        }
    }



}
