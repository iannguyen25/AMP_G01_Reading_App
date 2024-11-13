package com.example.amp_g01_reading_app.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amp_g01_reading_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalSettingsFragment extends Fragment {
//    private PersonalSettingsViewModel personalSettingsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_settings, container, false);

//        personalSettingsViewModel = new ViewModelProvider(this).get(PersonalSettingsViewModel.class);

        // Ánh xạ và quản lý các tùy chọn cá nhân

        return view;
    }
}
