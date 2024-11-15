package com.example.amp_g01_reading_app.ui;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.amp_g01_reading_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationDialogFragment extends DialogFragment {

    public NotificationDialogFragment() {
        // Required empty public constructor
    }

    public static NotificationDialogFragment newInstance() {
        return new NotificationDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_dialog, container, false);

        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> dismiss());

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {

            int marginHorizontal = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

            int width = getResources().getDisplayMetrics().widthPixels - (marginHorizontal * 2);

            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}