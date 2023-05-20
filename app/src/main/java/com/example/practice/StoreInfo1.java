package com.example.practice;

public class StoreInfo1 {
    public String qrdata;
    public String time;
    public String name;
    public String studentnumber;

    public String getQrdata() {
        return qrdata;
    }

    public void setQrdata(String qrdata) {
        this.qrdata = qrdata;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public StoreInfo1(String course, String time, String name, String studentnumber) {
        this.qrdata = course;
        this.time = time;
        this.name = name;
        this.studentnumber = studentnumber;
    }
}