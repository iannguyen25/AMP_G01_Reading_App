package com.example.amp_g01_reading_app.ui.settings.management_settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.NotificationDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;


public class AdjustTimeLimitDialogFragment extends DialogFragment {

    private EditText hoursEditText, minutesEditText;
    private TextView currentTimeLimitTextView;
    private FirebaseFirestore db;
    private String childId;

    public static AdjustTimeLimitDialogFragment newInstance(String childId) {
        AdjustTimeLimitDialogFragment fragment = new AdjustTimeLimitDialogFragment();
        Bundle args = new Bundle();
        args.putString("childId", childId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_time_limit, null);

        db = FirebaseFirestore.getInstance();
        assert getArguments() != null;
        childId = getArguments().getString("childId");

        currentTimeLimitTextView = view.findViewById(R.id.currentTimeLimitTextView);
        hoursEditText = view.findViewById(R.id.hoursEditText);
        minutesEditText = view.findViewById(R.id.minutesEditText);
        Button increaseButton = view.findViewById(R.id.increaseButton);
        Button decreaseButton = view.findViewById(R.id.decreaseButton);
        Button resetButton = view.findViewById(R.id.resetButton);
        Button confirmButton = view.findViewById(R.id.confirmButton);

        loadCurrentTimeLimit();

        increaseButton.setOnClickListener(v -> adjustMinutes(30));
        decreaseButton.setOnClickListener(v -> adjustMinutes(-30));
        resetButton.setOnClickListener(v -> resetInputs());
        confirmButton.setOnClickListener(v -> updateTimeLimit());

        builder.setView(view);
//                .setTitle("Adjust Time Limit");

        return builder.create();
    }

    @SuppressLint("SetTextI18n")
    private void loadCurrentTimeLimit() {
        db.collection("children").document(childId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long timeLimit = documentSnapshot.getLong("timeLimit");
                        if (timeLimit != null) {
                            currentTimeLimitTextView.setText("Current time limit: " + timeLimit + " minutes");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(
                            "Thông báo",
                            "Không thể tải thời gian giới hạn hôm nay!"
                    );
                    dialog.show(getChildFragmentManager(), "NotificationDialog");
                    dismiss();
                });
    }


    private void adjustMinutes(int change) {
        int currentMinutes = getMinutes();
        currentMinutes += change;
        if (currentMinutes < 0) currentMinutes = 0;
        setMinutes(currentMinutes);
    }

    private void resetInputs() {
        hoursEditText.setText("");
        minutesEditText.setText("");
    }

    private void updateTimeLimit() {
        int newTimeLimit = getMinutes();
        db.collection("children").document(childId)
                .update("timeLimit", newTimeLimit)
                .addOnSuccessListener(aVoid -> {
                    NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(
                            "Thông báo",
                            "Bạn đã thay đổi thành công\n Giới hạn thời gian bây giờ là " + newTimeLimit + " phút."
                    );
                    dialog.show(getChildFragmentManager(), "NotificationDialog");
                    new android.os.Handler().postDelayed(this::dismiss, 3000);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update time limit", Toast.LENGTH_SHORT).show());
    }

    private int getMinutes() {
        try {
            int hours = hoursEditText.getText().toString().isEmpty() ? 0 : Integer.parseInt(hoursEditText.getText().toString());
            int minutes = minutesEditText.getText().toString().isEmpty() ? 0 : Integer.parseInt(minutesEditText.getText().toString());
            return hours * 60 + minutes;
        } catch (NumberFormatException e) {
            NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(
                    "Thông báo",
                    "Hãy nhập đúng định dạng số nguyên"
            );
            dialog.show(getChildFragmentManager(), "NotificationDialog");
            return 0;
        }
    }


    private void setMinutes(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        hoursEditText.setText(String.valueOf(hours));
        minutesEditText.setText(String.valueOf(minutes));
    }
}