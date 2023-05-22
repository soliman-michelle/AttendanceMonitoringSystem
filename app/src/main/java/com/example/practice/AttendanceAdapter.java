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

public class AttendanceAdapter extends ArrayAdapter<AttendanceRecord> {
    private LayoutInflater inflater;

    public AttendanceAdapter(Context context, List<AttendanceRecord> attendanceList) {
        super(context, 0, attendanceList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.attendance_item, parent, false);
        }

        AttendanceRecord attendanceRecord = getItem(position);
        if (attendanceRecord != null) {
            TextView nameTextView = itemView.findViewById(R.id.nameTextView);
            TextView arrivalTextView = itemView.findViewById(R.id.arrivalTextView);
            TextView departureTextView = itemView.findViewById(R.id.departureTextView);
            TextView lengthTextView = itemView.findViewById(R.id.lengthOfStayTextView);
            TextView statusTextView = itemView.findViewById(R.id.statusTextView);

            nameTextView.setText(attendanceRecord.getStudentName());
            arrivalTextView.setText(attendanceRecord.getArrival());
            departureTextView.setText(attendanceRecord.getDeparture());
            lengthTextView.setText(attendanceRecord.getLengthOfStay());
            statusTextView.setText(attendanceRecord.getStatus());
        }

        return itemView;
    }
}
