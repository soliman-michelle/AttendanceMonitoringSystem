package com.example.practice;

public class StoreInfo {
    public String id;
    public String course;
    public String date;
    public String time;
    public String address;
    public String name;
    public String studentnumber; //new field

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentnumber() {
        return studentnumber;
    }

    public void setStudentnumber(String studentnumber) {
        this.studentnumber = studentnumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public StoreInfo(String id, String subject, String date, String time, String locationString, String name) {
        this.id = id;
        this.course = subject;
        this.date = date;
        this.time = time;
        this.address = locationString;
        this.name = name;
        this.studentnumber = studentnumber;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

}