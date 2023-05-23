package com.example.practice;

public class studentData {
    public String studNum;
    public String status;
    public String name;
    public String date;
    public String arrival;

    public String getStudNum() {
        return studNum;
    }

    public void setStudNum(String studNum) {
        this.studNum = studNum;
    }



    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public studentData(String studNum, String status, String name, String date, String arrival){
        this.name = name;
        this.studNum = studNum;
        this.status = status;
        this.date = date;
        this.arrival = arrival;

    }
}
