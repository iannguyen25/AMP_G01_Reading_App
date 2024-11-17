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
        readingTimesEntries = new MutableLiveData<>();

        // Initialize with some sample data
        userName.setValue("Tuấn Anh");
        userAge.setValue("21 tuổi");

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 24.0f));
        entries.add(new BarEntry(1, 12.5f));
        entries.add(new BarEntry(2, 19.8f));
        entries.add(new BarEntry(3, 10.1f));
        entries.add(new BarEntry(4, 18.4f));
        entries.add(new BarEntry(5, 2.7f));
        entries.add(new BarEntry(6, 18.9f));
        readingTimesEntries.setValue(entries);
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

    // Optional: Method to update time limit dynamically
    public void updateReadingTime(List<BarEntry> newEntries) {
        readingTimesEntries.setValue(newEntries);
    }
}
