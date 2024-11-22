package com.example.amp_g01_reading_app.ui.settings.dashboard_management;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildData {
    private String childId;
    private String name;
    private int age;
    private int totalUsageToday;
    private int timeLimit;
    private Map<String, Long> dailyUsage;

    public String getId() {
        return childId;
    }

    public void setId(String id) {
        this.childId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getTotalUsageToday() {
        return totalUsageToday;
    }

    public void setTotalUsageToday(int timeUsedToday) {
        this.totalUsageToday = timeUsedToday;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Map<String, Long> dailyUsage() {
        return dailyUsage != null ? dailyUsage : new HashMap<>();
    }

    public Map<String, Long> getDailyUsage() {
        return dailyUsage != null ? dailyUsage : new HashMap<>();
    }
}