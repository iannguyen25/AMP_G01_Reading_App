package com.example.amp_g01_reading_app.ui.settings.dashboard_management;
import java.util.List;

public class ChildData {
    private String ChildId;
    private String name;
    private int age;
    private int totalUsageToday;
    private int timeLimit;
    private List<Integer> dailyUsage;

    public String getId() {
        return ChildId;
    }

    public void setId(String id) {
        this.ChildId = id;
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

    public List<Integer> dailyUsage() {
        return dailyUsage;
    }

    public void setDailyUsage(List<Integer> dailyUsage) {
        this.dailyUsage = dailyUsage;
    }
}