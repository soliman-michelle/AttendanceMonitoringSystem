package com.example.practice;

public class Admin {
    public String adminName;
    public String adminPass;

    public Admin(){

    }

    public String getAdminPass() {
        return adminPass;
    }

    public void setAdminPass(String adminPass) {
        this.adminPass = adminPass;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Admin (String name, String pass ){
        this.adminName = name;
        this.adminPass = pass;
    }
}
