package com.example.amp_g01_reading_app.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsViewModel extends ViewModel {
    private FirebaseAuth mAuth;

    public SettingsViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> logOut() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        mAuth.signOut();
        result.setValue(true);
        return result;
    }
}