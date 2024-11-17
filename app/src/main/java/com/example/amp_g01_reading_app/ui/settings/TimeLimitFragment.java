package com.example.amp_g01_reading_app.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.amp_g01_reading_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TimeLimitFragment extends DialogFragment {

    private EditText etHours, etMinutes;
    private TextView tvRemainingTime;
    private Button btnIncrease, btnDecrease, btnComplete;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String childId;
    private TimeLimitViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(TimeLimitViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_limit, container, false);

        etHours = view.findViewById(R.id.etHours);
        etMinutes = view.findViewById(R.id.etMinutes);
        tvRemainingTime = view.findViewById(R.id.tvRemainingTime);
        btnIncrease = view.findViewById(R.id.btnAdd);
        btnDecrease = view.findViewById(R.id.btnSubtract);
        btnComplete = view.findViewById(R.id.btnComplete);

        loadTimeLimit();

        btnIncrease.setOnClickListener(v -> adjustTime(true));
        btnDecrease.setOnClickListener(v -> adjustTime(false));
        btnComplete.setOnClickListener(v -> saveTimeLimit());

        return view;
    }

    private void loadTimeLimit() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).collection("children")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot childSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            childId = childSnapshot.getId();
                            Long timeLimit = childSnapshot.getLong("timeLimit");
                            if (timeLimit != null ) {
                                viewModel.setTimeLimit(timeLimit);
                                updateTimeLimitDisplay();
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Error loading time limit", e));
        }
    }

    private void adjustTime(boolean increase) {
        String hoursText = etHours.getText().toString();
        String minutesText = etMinutes.getText().toString();

        if (hoursText.isEmpty()) hoursText = "0";
        if (minutesText.isEmpty()) minutesText = "0";

        int hours = Integer.parseInt(hoursText);
        int minutes = Integer.parseInt(minutesText);

        if (increase) {
            viewModel.addTime(hours, minutes);
        } else {
            viewModel.subtractTime(hours, minutes);
        }

        updateTimeLimitDisplay();
    }


    private void updateTimeLimitDisplay() {
        long timeLimit = viewModel.getTimeLimit();
        int hours = (int) (timeLimit / 60);
        int minutes = (int) (timeLimit % 60);
        tvRemainingTime.setText(String.format("%02d h : %02d m", hours, minutes));
    }

    private void saveTimeLimit() {
        long timeLimit = viewModel.getTimeLimit();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && childId != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId)
                    .collection("children").document(childId)
                    .update("timeLimit", timeLimit)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Đã lưu giới hạn thời gian", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi lưu giới hạn thời gian", Toast.LENGTH_SHORT).show());
        }
    }
}