package com.example.practice;

public class studentSched {
    public String subject;
    public String time;
    public String day;
    public String room;

    public studentSched() {
    }
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public studentSched(String subject, String time, String day, String room){
        this.day = day;
        this.room = room;
        this.time = time;
        this.subject = subject;
    }
}
