package com.example.amp_g01_reading_app.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.authentication.createChildAccount.ChildAccountFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalSettingsFragment} factory method to
 * create an instance of this fragment.
 */
public class PersonalSettingsFragment extends Fragment {
//    private PersonalSettingsViewModel personalSettingsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_settings, container, false);

//        personalSettingsViewModel = new ViewModelProvider(this).get(PersonalSettingsViewModel.class);

        // Ánh xạ và quản lý các tùy chọn cá nhân

        LinearLayout add_child_account_label = view.findViewById(R.id.add_child_account_label);
        add_child_account_label.setOnClickListener(v -> {
            // Hiển thị DialogFragment khi nhấn vào TextView
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.nav_childAccountFragment);
        });

        return view;
    }
}
