package com.example.practice;

public class enrollSub {
    public String yearLevel;
    public String programNames;
    public String professors;
    public String terms;
    public String blocks;
    public String loc;

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getProgramNames() {
        return programNames;
    }

    public void setProgramNames(String programNames) {
        this.programNames = programNames;
    }

    public String getProfessors() {
        return professors;
    }

    public void setProfessors(String professors) {
        this.professors = professors;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getBlocks() {
        return blocks;
    }

    public void setBlocks(String blocks) {
        this.blocks = blocks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String id;
    public String subject;

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    enrollSub(String programNames, String yearLevel, String blocks, String terms, String id, String subject, String professors, String loc ){
        this.yearLevel = yearLevel;
        this.programNames = programNames;
        this.professors = professors;
        this.terms = terms;
        this.id = id;
        this.subject = subject;
        this.blocks = blocks;
        this.loc = loc;
    }
}
