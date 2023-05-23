package com.example.practice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AttendanceAdapter extends ArrayAdapter<studentData> {
    private Context context;
    private List<studentData> attendanceList;

    public AttendanceAdapter(Context context, List<studentData> attendanceList) {
        super(context, 0, attendanceList);
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.attendance_item, parent, false);
        }

        // Get the current attendance record
        studentData attendance = attendanceList.get(position);

        TextView nameTextView = itemView.findViewById(R.id.nameTextView);
        TextView arrivalTextView = itemView.findViewById(R.id.arrivalTextView);
        TextView statusTextView = itemView.findViewById(R.id.statusTextView);

        nameTextView.setText(attendance.getName());
        arrivalTextView.setText(attendance.getArrival());
        statusTextView.setText(attendance.getStatus());

        return itemView;
    }
}
