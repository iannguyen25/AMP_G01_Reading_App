package com.example.amp_g01_reading_app.ui.settings;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimeLimitViewModel extends ViewModel {
    private final MutableLiveData<Long> timeLimit = new MutableLiveData<>(60L); // Default 60 minutes
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<Long> getTimeLimit() {
        return timeLimit;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void setTimeLimit(long timeLimitInMinutes) {
        if (timeLimitInMinutes >= 0) {
            timeLimit.setValue(timeLimitInMinutes);
        } else {
            error.setValue("Thời gian không thể âm");
        }
    }

    public void addTime(int hours, int minutes) {
        Long currentLimit = timeLimit.getValue();
        if (currentLimit != null) {
            long newLimit = currentLimit + (hours * 60L + minutes);
            if (newLimit <= 24 * 60) { // Max 24 hours
                timeLimit.setValue(newLimit);
            } else {
                error.setValue("Không thể đặt thời gian quá 24 giờ");
            }
        }
    }

    public void subtractTime(int hours, int minutes) {
        Long currentLimit = timeLimit.getValue();
        if (currentLimit != null) {
            long newLimit = currentLimit - (hours * 60L + minutes);
            if (newLimit >= 0) {
                timeLimit.setValue(newLimit);
            } else {
                error.setValue("Thời gian không thể âm");
            }
        }
    }
}