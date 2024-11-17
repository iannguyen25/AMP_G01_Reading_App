package com.example.amp_g01_reading_app.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.amp_g01_reading_app.R;

public class ConfirmDialogFragment extends DialogFragment {

    public interface ConfirmDialogListener {
        void onDialogPositiveClick();
        void onDialogNegativeClick();
    }

    private ConfirmDialogListener listener;

    public ConfirmDialogFragment() {
        // Required empty public constructor
    }

    public static ConfirmDialogFragment newInstance() {
        return new ConfirmDialogFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ConfirmDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString()
                    + " must implement ConfirmDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_dialog, container, false);

        Button buttonConfirm = view.findViewById(R.id.btnConfirm);
        buttonConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDialogPositiveClick();
            }
            dismiss();
        });

        Button buttonCancel = view.findViewById(R.id.btnCancel);
        buttonCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDialogNegativeClick();
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int marginHorizontal = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

            int width = getResources().getDisplayMetrics().widthPixels - (marginHorizontal * 2);

            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}