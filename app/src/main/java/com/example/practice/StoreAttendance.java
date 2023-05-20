package com.example.practice;

public class StoreAttendance {
    public String course, classId, terms, formattedDate, time, locationString, name, studNum, id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StoreAttendance(String course, String classId, String terms, String formattedDate, String time, String locationString, String name, String studNum, String id){
        this.course = course;
        this.classId = classId;
        this.terms = terms;
        this.formattedDate = formattedDate;
        this.time = time;
        this.locationString = locationString;
        this.name = name;
        this.studNum = studNum;
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

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
}
