package com.example.amp_g01_reading_app.ui.settings.management_settings;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import android.widget.AdapterView;
import android.widget.TextView;

import com.example.amp_g01_reading_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgeLimitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgeLimitFragment extends DialogFragment {

    private Spinner spinnerAgeRange;
    private TextView tvCurrentAge;

    public static AgeLimitFragment newInstance() {
        return new AgeLimitFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_age_limit, container, false);

        spinnerAgeRange = view.findViewById(R.id.spinnerAgeRange);
        tvCurrentAge = view.findViewById(R.id.tvCurrentAge);

        ImageView btnClose = view.findViewById(R.id.btnClose);

        setupAgeRangeSpinner();

        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {

            int marginHorizontal = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

            // Tính toán chiều rộng của hộp thoại với margin
            int width = getResources().getDisplayMetrics().widthPixels - (marginHorizontal * 2);

            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void setupAgeRangeSpinner() {
        String[] ageRanges = new String[]{
                "6 - 8 tuổi",
                "9 - 11 tuổi",
                "12 - 14 tuổi",
                "15 - 17 tuổi"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                ageRanges
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgeRange.setAdapter(adapter);

        // Listener khi chọn độ tuổi
        spinnerAgeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAge = parent.getItemAtPosition(position).toString();
                tvCurrentAge.setText(selectedAge);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}