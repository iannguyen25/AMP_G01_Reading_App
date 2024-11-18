package com.example.amp_g01_reading_app.ui.authentication.createChildAccount;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChildAccountViewModel extends ViewModel {
    private final MutableLiveData<String> childId = new MutableLiveData<>();

    public LiveData<String> getChildId() {
        return childId;
    }

    public void setChildId(String id) {
        childId.setValue(id);
    }
}
