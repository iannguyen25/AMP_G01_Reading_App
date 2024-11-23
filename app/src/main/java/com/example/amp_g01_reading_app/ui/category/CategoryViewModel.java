package com.example.amp_g01_reading_app.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CategoryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CategoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is category fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}