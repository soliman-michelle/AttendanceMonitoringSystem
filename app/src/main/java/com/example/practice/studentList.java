package com.example.practice;

import android.os.Parcel;
import android.os.Parcelable;

public class studentList implements Parcelable {
    private String fullname;
    private String studNum;
    private String year;
    private String course;
    private String section;
    private String email;
    private String defaultpass;
    private String phone;
    public String getEmail() {
        return email;
    }
    public studentList() {
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDefaultpass() {
        return defaultpass;
    }

    public void setDefaultpass(String defaultpass) {
        this.defaultpass = defaultpass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public studentList(String fullname, String studNum, String course, String year, String section, String email, String defaultpass, String phone) {
        this.fullname = fullname;
        this.studNum = studNum;
        this.course = course;
        this.year = year;
        this.section = section;
        this.defaultpass = defaultpass;
        this.email = email;
        this.phone = phone;
    }

    protected studentList(Parcel in) {
        fullname = in.readString();
        studNum = in.readString();
        course = in.readString();
        year = in.readString();
        section = in.readString();
        email = in.readString();
        defaultpass = in.readString();
        phone = in.readString();

    }

    public static final Creator<studentList> CREATOR = new Creator<studentList>() {
        @Override
        public studentList createFromParcel(Parcel in) {
            return new studentList(in);
        }

        @Override
        public studentList[] newArray(int size) {
            return new studentList[size];
        }
    };

    public String getFullname() {
        return fullname;
    }
    public String getStudNum() {
        return studNum;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public void setStudNum(String studNum) {
        this.studNum = studNum;
    }
    public String getYearLevel() {
        return year;
    }
    public void setYearLevel(String yearLevel) {
        this.year = yearLevel;
    }
    public String getSection() {
        return section;
    }
    public void setSection(String section) {
        this.section = section;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fullname);
        parcel.writeString(studNum);
        parcel.writeString(course);
        parcel.writeString(year);
        parcel.writeString(section);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(defaultpass);
    }
}
