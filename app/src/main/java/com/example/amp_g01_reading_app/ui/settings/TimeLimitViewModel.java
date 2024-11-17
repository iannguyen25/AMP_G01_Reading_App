package com.example.amp_g01_reading_app.ui.settings;

import android.util.Log;

import androidx.lifecycle.ViewModel;

public class TimeLimitViewModel extends ViewModel {
    private long timeLimit; // in minutes

    public TimeLimitViewModel() {
        timeLimit = 60; // Default 60 minutes
    }

    public void setTimeLimit(long timeLimitInMinutes) {
        this.timeLimit = timeLimitInMinutes;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void addTime(int hours, int minutes) {
        timeLimit += hours * 60 + minutes;
    }

    public void subtractTime(int hours, int minutes) {
        long newTimeLimit = timeLimit - (hours * 60 + minutes);
        timeLimit = Math.max(0, newTimeLimit);
        Log.d("TimeLimit", "Time after subtraction: " + timeLimit);
    }

}