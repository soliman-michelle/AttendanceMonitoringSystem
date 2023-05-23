package com.example.practice;

public class AttendanceRecord {
    private String studentName;
    private String arrival;
    private String lengthOfStay;
    private String departure;
    private String status;
    public AttendanceRecord() {
        // Default constructor required for Firebase serialization
    }
    public AttendanceRecord(String arrival, String length, String departure, String status, String studentName) {
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }


    public AttendanceRecord(String studentName) {
        this.studentName = studentName;
        this.arrival = "";  // Set initial values as empty strings
        this.lengthOfStay = "";
        this.departure = "";
        this.status = "";
    }

    public String getStudentName() {
        return studentName;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getLengthOfStay() {
        return lengthOfStay;
    }

    public void setLengthOfStay(String lengthOfStay) {
        this.lengthOfStay = lengthOfStay;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}