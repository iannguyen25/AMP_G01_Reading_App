package com.example.amp_g01_reading_app.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amp_g01_reading_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagementSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagementSettingsFragment extends Fragment {
//    private ManagementSettingsViewModel managementSettingsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_management_settings, container, false);

//        managementSettingsViewModel = new ViewModelProvider(this).get(ManagementSettingsViewModel.class);

        // Ánh xạ và quản lý các tùy chọn quản lý

        return view;
    }
}
