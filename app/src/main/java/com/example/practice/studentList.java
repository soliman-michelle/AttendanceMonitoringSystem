package com.example.practice;

import android.os.Parcel;
import android.os.Parcelable;

public class studentList implements Parcelable {
    private String fullname;
    private String studNum;

    public studentList(String fullname, String studNum) {
        this.fullname = fullname;
        this.studNum = studNum;
    }

    protected studentList(Parcel in) {
        fullname = in.readString();
        studNum = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fullname);
        parcel.writeString(studNum);
    }
}
