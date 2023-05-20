package com.example.practice;
import java.util.HashMap;
import java.util.Map;

public class students {

    private String courseStr;
    private Map<String, Map<String, Map<String, Map<String, Student>>>> sections;

    public students() {
    }

    public students(String courseStr) {
        this.courseStr = courseStr;
        this.sections = new HashMap<>();
    }


    public String getCourseStr() {
        return courseStr;
    }

    public void setCourseStr(String courseStr) {
        this.courseStr = courseStr;
    }

    public Map<String, Map<String, Map<String, Map<String, Student>>>> getSections() {
        return sections;
    }

    public void setSections(Map<String, Map<String, Map<String, Map<String, Student>>>> sections) {
        this.sections = sections;
    }

    public void addStudent(String yearLevel, String section, Student student) {
        if (!this.sections.containsKey(yearLevel)) {
            this.sections.put(yearLevel, new HashMap<>());
        }
        Map<String, Map<String, Map<String, Student>>> yearLevelData = this.sections.get(yearLevel);
        if (!yearLevelData.containsKey(section)) {
            yearLevelData.put(section, new HashMap<>());
        }
        Map<String, Student> sectionData = yearLevelData.get(section).getOrDefault("Students", new HashMap<>());
        sectionData.put(student.getStudnum(), student);
        yearLevelData.get(section).put("Students", sectionData);
        this.sections.put(yearLevel, yearLevelData);
    }


    public static class Student {
        private String course;
        private String defaultpass;
        private String email;
        private String fname;
        private String lname;
        private String mname;
        private String phone;
        private String section;
        private String studnum;
        private String year;

        public Student(String studnum, String fname, String lname, String mname, String phone, String defaultpass, String email,  String course, String section, String year) {
            this.course = course;
            this.defaultpass = defaultpass;
            this.email = email;
            this.fname = fname;
            this.lname = lname;
            this.mname = mname;
            this.phone = phone;
            this.section = section;
            this.studnum = studnum;
            this.year = year;
        }

        public String getCourse() {
            return course;
        }

        public void setCourse(String course) {
            this.course = course;
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

        public String getMname() {
            return mname;
        }

        public void setMname(String mname) {
            this.mname = mname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getStudnum() {
            return studnum;
        }

        public void setStudnum(String studnum) {
            this.studnum = studnum;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }

    }
}