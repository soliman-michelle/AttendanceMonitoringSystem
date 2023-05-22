package com.example.practice;

public class QRData {
    private String classId;
    private String professorId;
    private String year;
    private String section;
    private String location;
    private String term;

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setSubjectId(String subjectId) {
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getLocation() {
        return location;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public QRData(String classId,String professorId,  String year, String section, String location, String term) {
        this.classId = classId;
        this.professorId = professorId;
        this.year = year;
        this.section = section;
        this.location = location;
        this.term = term;
    }

    public String getClassId() {
        return classId;
    }

    public String getProfessorId() {
        return professorId;
    }

}
