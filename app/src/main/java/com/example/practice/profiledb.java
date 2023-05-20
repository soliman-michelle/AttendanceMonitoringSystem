package com.example.practice;

public class profiledb {
    public String fname;
    public String mname;
    public String lname;
    public String studnum;
    public String phone;
    public String defaultpass;
    public String year;

    public String block;
    public String program;
    public String email;

    public profiledb(){

    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getStudnum() {
        return studnum;
    }

    public void setStudnum(String studnum) {
        this.studnum = studnum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDefaultpass() {
        return defaultpass;
    }

    public void setDefaultpass(String defaultpass) {
        this.defaultpass = defaultpass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public profiledb(String fname, String mname, String lname, String studnum, String phone, String defaultpass, String email, String program, String year, String block){
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.studnum = studnum;
        this.phone = phone;
        this.defaultpass = defaultpass;
        this.email = email;
        this.program = program;
        this.year = year;
        this.block = block;

    }
}
