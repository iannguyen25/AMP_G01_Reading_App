package com.example.amp_g01_reading_app.ui.settings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amp_g01_reading_app.R;

//public class SettingsFragment extends Fragment {
//
//    private SettingsViewModel mViewModel;
//
//    public static SettingsFragment newInstance() {
//        return new SettingsFragment();
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_settings, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
//        // TODO: Use the ViewModel
//    }
//
//}

public class SettingsFragment extends Fragment {
    private SettingsViewModel settingsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Load các fragment con
        getChildFragmentManager().beginTransaction()
                .replace(R.id.personalSettingsContainer, new PersonalSettingsFragment())
                .replace(R.id.managementSettingsContainer, new ManagementSettingsFragment())
                .commit();

        return view;
    }
}
