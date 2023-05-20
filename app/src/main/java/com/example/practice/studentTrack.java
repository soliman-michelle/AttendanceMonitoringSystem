package com.example.practice;

public class studentTrack {
    public String name;
    public String studNum;
    public String status;
    public String arrival;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudNum() {
        return studNum;
    }

    public void setStudNum(String studNum) {
        this.studNum = studNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public studentTrack(String name, String studNum, String status, String arrival){
        this.name = name;
        this.studNum = studNum;
        this.status = status;
        this.arrival = arrival;
    }
}
