package com.example.amp_g01_reading_app.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.amp_g01_reading_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagementSettingsFragment} factory method to
 * create an instance of this fragment.
 */
public class ManagementSettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_management_settings, container, false);

        LinearLayout time_limit_label = view.findViewById(R.id.time_limit_fragment);
        time_limit_label.setOnClickListener(v -> {
            // Hiển thị DialogFragment khi nhấn vào TextView
            TimeLimitFragment dialogFragment = new TimeLimitFragment();
            dialogFragment.show(getChildFragmentManager(), "timeLimitDialog");
        });

        LinearLayout age_limit_label = view.findViewById(R.id.age_limit_fragment);
        age_limit_label.setOnClickListener(v -> {

            AgeLimitFragment dialogFragment = new AgeLimitFragment();
            dialogFragment.show(getChildFragmentManager(), "ageLimitDialog");
        });

        return view;
    }
}



