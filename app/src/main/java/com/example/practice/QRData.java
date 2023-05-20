package com.example.practice;

public class QRData {
    private String classId;
    private String professorId;
    private String subjectId;

    public QRData(String classId, String professorId, String subjectId) {
        this.classId = classId;
        this.professorId = professorId;
        this.subjectId = subjectId;
    }

    public String getClassId() {
        return classId;
    }

    public String getProfessorId() {
        return professorId;
    }

    public String getSubjectId() {
        return subjectId;
    }
}
