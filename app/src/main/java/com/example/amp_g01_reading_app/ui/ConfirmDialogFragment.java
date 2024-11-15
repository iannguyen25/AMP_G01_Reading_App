package com.example.amp_g01_reading_app.ui;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.settings.AgeLimitFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfirmDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmDialogFragment extends DialogFragment {


    public ConfirmDialogFragment() {
        // Required empty public constructor
    }

    public static ConfirmDialogFragment newInstance() {
        return new ConfirmDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_dialog, container, false);


        Button buttonConfirm = view.findViewById(R.id.btnConfirm);
        buttonConfirm.setOnClickListener(v -> {

            NotificationDialogFragment dialogFragment = new NotificationDialogFragment();
            dialogFragment.show(getChildFragmentManager(), "notificationDialogFragment");
        });

        Button buttonCancel = view.findViewById(R.id.btnCancel);
        buttonCancel.setOnClickListener(v -> dismiss());

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