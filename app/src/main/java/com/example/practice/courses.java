package com.example.practice;

import java.util.HashMap;
import java.util.Map;

public class courses {
    private String programName;
    private Map<String, Map<String, Map<String, String>>> sections;

    public courses() {}

    public courses(String programName) {
        this.programName = programName;
        this.sections = new HashMap<>();
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Map<String, Map<String, Map<String, String>>> getSections() {
        return sections;
    }

    public void setSections(Map<String, Map<String, Map<String, String>>> sections) {
        this.sections = sections;
    }

    public void addSection(String yearLevel, String section) {
        if (!this.sections.containsKey(yearLevel)) {
            this.sections.put(yearLevel, new HashMap<>());
        }
        Map<String, Map<String, String>> yearLevelData = this.sections.get(yearLevel);
        if (!yearLevelData.containsKey(section)) {
            yearLevelData.put(section, new HashMap<>());
        }
        Map<String, String> sectionData = yearLevelData.get(section);
        sectionData.put("yearLevel", yearLevel);
        this.sections.put(yearLevel, yearLevelData);
    }
}
