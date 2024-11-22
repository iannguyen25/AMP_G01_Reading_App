package com.example.amp_g01_reading_app.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.amp_g01_reading_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";

    public static NotificationDialogFragment newInstance(String title, String content) {
        NotificationDialogFragment fragment = new NotificationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    public static NotificationDialogFragment newInstance() {
        return new NotificationDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.fragment_notification_dialog, null);

        // Bind UI elements
        TextView titleTextView = view.findViewById(R.id.notification_title);
        TextView contentTextView = view.findViewById(R.id.notification_content);
        Button confirmButton = view.findViewById(R.id.btnConfirm);

        // Set data
        if (getArguments() != null) {
            String title = getArguments().getString(ARG_TITLE);
            String content = getArguments().getString(ARG_CONTENT);

            titleTextView.setText(title);
            contentTextView.setText(content);
        }

        confirmButton.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
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