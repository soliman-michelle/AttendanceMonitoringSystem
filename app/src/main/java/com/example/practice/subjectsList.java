package com.example.practice;

public class subjectsList {
    public String coursename;
    public String program;
    public String yearLevel;
    public String term;
    public String prof;
    public String days;
    public String roomNumber;
    public String coursenumber;
    public String section;

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCoursenumber() {
        return coursenumber;
    }

    public void setCoursenumber(String coursenumber) {
        this.coursenumber = coursenumber;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String unit;
    public String time;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public subjectsList(String coursename, String program, String yearLevel, String term, String prof, String days, String roomNumber, String coursenumber, String unit, String time, String section){
        this.coursename = coursename;
        this.program = program;
        this.yearLevel = yearLevel;
        this.term = term;
        this.prof = prof;
        this.days = days;
        this.roomNumber = roomNumber;
        this.coursenumber = coursenumber;
        this.unit= unit;
        this.time = time;
        this.section = section;
    }
}
