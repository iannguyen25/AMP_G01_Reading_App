package com.example.amp_g01_reading_app.ui.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.services.TimeLimitService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TimeLimitFragment extends DialogFragment {

    private EditText etHours, etMinutes;
    private TextView tvRemainingTime;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String childId;
    private TimeLimitViewModel viewModel;
    private ProgressBar progressBar;
    TimeLimitService timeLimitService;

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

        initializeViews(view);
        setupViewModelObservers();
        loadTimeLimit();

        return view;
    }

    private void initializeViews(View view) {
        etHours = view.findViewById(R.id.etHours);
        etMinutes = view.findViewById(R.id.etMinutes);
        tvRemainingTime = view.findViewById(R.id.tvRemainingTime);
        Button btnIncrease = view.findViewById(R.id.btnAdd);
        Button btnDecrease = view.findViewById(R.id.btnSubtract);
        Button btnComplete = view.findViewById(R.id.btnComplete);
        Button btnReset = view.findViewById(R.id.btnReset);
        progressBar = view.findViewById(R.id.progressBar);
        btnReset.setOnClickListener(v -> resetTimeLimit());

        btnIncrease.setOnClickListener(v -> adjustTime(true));
        btnDecrease.setOnClickListener(v -> adjustTime(false));
        btnComplete.setOnClickListener(v -> saveTimeLimit());
    }

    private void resetTimeLimit() {
        viewModel.setTimeLimit(60);
        updateTimeLimitDisplay(60);
    }

    private void setupViewModelObservers() {
        viewModel.getTimeLimit().observe(getViewLifecycleOwner(), this::updateTimeLimitDisplay);
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTimeLimit() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("TimeLimitFragment", "Current user is null");
            progressBar.setVisibility(View.GONE);
            timeLimitService.sendServiceReadyBroadcast();
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        Log.d("TimeLimitFragment", "Loading time limit for user: " + userId);

        db.collection("users")
                .document(userId)
                .collection("children")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    if (queryDocumentSnapshots == null) {
                        Log.e("TimeLimitFragment", "QueryDocumentSnapshots is null");
                        Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w("TimeLimitFragment", "No children found for user: " + userId);
                        Toast.makeText(getContext(), "No child data found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DocumentSnapshot childSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    childId = childSnapshot.getId();
                    Long timeLimit = childSnapshot.getLong("timeLimit");
                    if (timeLimit != null) {
                        Log.d("TimeLimitFragment", "Time limit loaded: " + timeLimit);
                        viewModel.setTimeLimit(timeLimit);
                    } else {
                        Log.w("TimeLimitFragment", "Time limit is null for child: " + childId);
                        Toast.makeText(getContext(), "Invalid time limit data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("TimeLimitFragment", "Error loading time limit", e);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void adjustTime(boolean increase) {
        String hoursText = etHours.getText().toString();
        String minutesText = etMinutes.getText().toString();

        try {
            int hours = hoursText.isEmpty() ? 0 : Integer.parseInt(hoursText);
            int minutes = minutesText.isEmpty() ? 0 : Integer.parseInt(minutesText);

            if (hours < 0 || minutes < 0 || minutes >= 60) {
                Toast.makeText(getContext(), "Giá trị không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (increase) {
                viewModel.addTime(hours, minutes);
            } else {
                viewModel.subtractTime(hours, minutes);
            }

            viewModel.getTimeLimit().observe(getViewLifecycleOwner(), this::updateTimeLimitDisplay);

            etHours.setText("");
            etMinutes.setText("");
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTimeLimitDisplay(long timeLimit) { // Change parameter type to long
        int hours = (int) (timeLimit / 60);
        int minutes = (int) (timeLimit % 60);
        tvRemainingTime.setText(String.format("%02d h : %02d m", hours, minutes));
    }

    private void saveTimeLimit() {
        Long timeLimit = viewModel.getTimeLimit().getValue();
        if (timeLimit == null) return;

        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && childId != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId)
                    .collection("children").document(childId)
                    .update("timeLimit", timeLimit)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Đã lưu giới hạn thời gian", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Log.e("saveTimeLimit", "Lỗi khi lưu dữ liệu: ", e);
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("saveTimeLimit", "Người dùng hoặc childId null");
        }
    }

    private final BroadcastReceiver timeUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TimeLimitService.ACTION_TIME_UPDATED.equals(intent.getAction())) {
                long timeRemaining = intent.getLongExtra("timeRemaining", 0);
                updateRemainingTimeDisplay(timeRemaining);
            }
        }
    };

    private void updateRemainingTimeDisplay(long timeRemaining) {
        int hours = (int) (timeRemaining / 60 );
        int minutes = (int) (timeRemaining % 60);
        tvRemainingTime.setText(String.format("%02d h : %02d m", hours, minutes));
    }

    private void registerTimeUpdateReceiver() {
        IntentFilter filter = new IntentFilter(TimeLimitService.ACTION_TIME_UPDATED);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(timeUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            requireContext().registerReceiver(timeUpdateReceiver, filter);
        }
    }

    private void unregisterTimeUpdateReceiver() {
        try {
            requireContext().unregisterReceiver(timeUpdateReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
            Log.w("TimeLimitFragment", "TimeUpdateReceiver not registered", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerTimeUpdateReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterTimeUpdateReceiver();
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
        loadTimeLimit();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterTimeUpdateReceiver();
    }
}
