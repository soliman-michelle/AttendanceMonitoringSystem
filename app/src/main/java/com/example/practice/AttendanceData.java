package com.example.practice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class AttendanceData implements Parcelable {
    private String studentId;
    private String attendanceStatus;
    private String date;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public AttendanceData(String studentId, String attendanceStatus, String date) {
        this.studentId = studentId;
        this.attendanceStatus = attendanceStatus;
        this.date = date;
    }

    protected AttendanceData(Parcel in) {
        studentId = in.readString();
        attendanceStatus = in.readString();
        date = in.readString();
    }

    public static final Creator<AttendanceData> CREATOR = new Creator<AttendanceData>() {
        @Override
        public AttendanceData createFromParcel(Parcel in) {
            return new AttendanceData(in);
        }

        @Override
        public AttendanceData[] newArray(int size) {
            return new AttendanceData[size];
        }
    };

    public String getStudentId() {
        return studentId;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public boolean isPresent() {
        // Implement your logic here to determine if attendanceStatus represents "Present"
        // For example, if attendanceStatus is a string like "Present" or "Absent", you can use:
        return attendanceStatus.equalsIgnoreCase("Present");
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(studentId);
        dest.writeString(attendanceStatus);
        dest.writeString(date);
    }
}
