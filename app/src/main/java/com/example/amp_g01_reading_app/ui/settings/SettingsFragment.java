package com.example.amp_g01_reading_app.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.ConfirmDialogFragment;

public class SettingsFragment extends Fragment implements ConfirmDialogFragment.ConfirmDialogListener {

    private SettingsViewModel settingsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Load các fragment con
        getChildFragmentManager().beginTransaction()
                .replace(R.id.personalSettingsContainer, new PersonalSettingsFragment())
                .replace(R.id.managementSettingsContainer, new ManagementSettingsFragment())
                .commit();

        LinearLayout log_out_button = view.findViewById(R.id.log_out_button);
        log_out_button.setOnClickListener(v -> {
            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance();
            dialogFragment.show(getChildFragmentManager(), "confirm_dialog");
        });

        return view;
    }

    @Override
    public void onDialogPositiveClick() {
        settingsViewModel.logOut().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Navigation.findNavController(requireView()).navigate(R.id.nav_loginFragment);
            } else {
                Toast.makeText(requireContext(), "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDialogNegativeClick() {
        // Do nothing or handle cancellation
    }
}