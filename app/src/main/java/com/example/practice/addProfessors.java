package com.example.practice;

import android.widget.EditText;

public class addProfessors {
    public String fname;
    public String mname;
    public String lname;
    public String username;
    public String phone;
    public String defaultpass;
    public String email;

    public addProfessors() {

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public addProfessors(String fname, String mname, String lname, String username, String phone, String defaultpass, String email){
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.username = username;
        this.phone = phone;
        this.defaultpass = defaultpass;
        this.email = email;
    }
}
