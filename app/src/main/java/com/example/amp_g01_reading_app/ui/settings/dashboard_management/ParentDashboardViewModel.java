package com.example.amp_g01_reading_app.ui.settings.dashboard_management;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.mikephil.charting.data.BarEntry;
import java.util.ArrayList;
import java.util.List;

public class ParentDashboardViewModel extends ViewModel {

    private MutableLiveData<String> userName;
    private MutableLiveData<String> userAge;
    private MutableLiveData<List<BarEntry>> readingTimesEntries;

    public ParentDashboardViewModel() {
        userName = new MutableLiveData<>();
        userAge = new MutableLiveData<>();
        readingTimesEntries = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserAge() {
        return userAge;
    }

    public LiveData<List<BarEntry>> getReadingTimesEntries() {
        return readingTimesEntries;
    }

    public void updateReadingTime(List<BarEntry> newEntries) {
        readingTimesEntries.setValue(newEntries);
    }
}