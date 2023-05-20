package com.example.practice;

public class recordAttendance
{
    public String id;
    public String studnum;

    public recordAttendance(String id, String studnum){
      this.id = id;
      this.studnum = studnum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudnum() {
        return studnum;
    }

    public void setStudnum(String studnum) {
        this.studnum = studnum;
    }
}
