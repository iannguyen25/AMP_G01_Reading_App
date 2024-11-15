package com.example.amp_g01_reading_app.ui.settings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.amp_g01_reading_app.ui.ConfirmDialogFragment;
import com.example.amp_g01_reading_app.R;

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

        LinearLayout log_out_button = view.findViewById(R.id.log_out_button);
        log_out_button.setOnClickListener(v -> {
            ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment();
            dialogFragment.show(getChildFragmentManager(), "confirm_dialog");
        });

        return view;
    }
}
