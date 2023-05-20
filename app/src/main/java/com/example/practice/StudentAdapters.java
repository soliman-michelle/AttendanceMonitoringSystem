package com.example.practice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentAdapters extends RecyclerView.Adapter<StudentAdapters.ViewHolder> {
    private List<studentTrack> students;

    public StudentAdapters() {

        students = new ArrayList<>();
    }
    public StudentAdapters(List<studentTrack> students) {
        this.students = students;
    }
    public void addStudent(studentTrack student) {

        students.add(student);
    }

    public void clearStudents() {

        students.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        studentTrack student = students.get(position);

        holder.nameTextView.setText("Name:  " + student.getName());
        holder.studNumTextView.setText("Student Number: " + student.getStudNum());
        holder.arrivalTextView.setText("Arrival Time: " + student.getArrival());

        // Calculate attendance
        int totalAttendance = students.size();
        int presentAttendance = position + 1;
        double attendancePercentage = (presentAttendance * 100.0) / totalAttendance;
        String attendanceText = String.format(Locale.getDefault(), "%.0f%%", attendancePercentage);

        holder.percentageTextView.setText(attendanceText);
        holder.attendanceTextView.setText("Attendance Appearance: " + presentAttendance + "/" + totalAttendance);

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView studNumTextView;
        TextView arrivalTextView;
        TextView percentageTextView;
        TextView attendanceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            studNumTextView = itemView.findViewById(R.id.studNumTextView);
            arrivalTextView = itemView.findViewById(R.id.arrivalTextView);
            percentageTextView = itemView.findViewById(R.id.percentageTextView);
            attendanceTextView = itemView.findViewById(R.id.attendanceTextView);
        }
    }
}
