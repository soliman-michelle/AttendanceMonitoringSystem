package com.example.practice;

public class classStudents {
    public String classId;
    public String loc;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }


    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public classStudents(String id, String loc){
        this.classId = id;
        this.loc = loc;

    }
}
