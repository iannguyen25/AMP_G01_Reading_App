package com.example.amp_g01_reading_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amp_g01_reading_app.databinding.ActivityMainBinding;
import com.example.amp_g01_reading_app.ui.authentication.AccountSelectionActivity;
import com.example.amp_g01_reading_app.ui.authentication.login.LoginActivity;
import com.example.amp_g01_reading_app.ui.home.Book;
import com.example.amp_g01_reading_app.ui.settings.management_settings.StoriesAdapter;
import com.example.amp_g01_reading_app.ui.settings.management_settings.StoriesRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final long UPDATE_INTERVAL = 60000; // 1 minute in milliseconds

    private TextView timerTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private boolean isChildAccount;
    private CountDownTimer countDownTimer;
    private final AtomicLong timeLeftInMillis = new AtomicLong(0);
    private Clock clock;
    private long lastUpdateTimestamp;
    private long accumulatedUsageTime = 0;

    private StoriesRepository storiesRepository;
    private RecyclerView recyclerView;
    private List<String> ageRanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            initializeClock();
            ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setupNavigation(binding);
            initializeFirebase();
            setupViews();
            loadUserData();
            //New
            ageRanges = new ArrayList<>();
            ageRanges.add("5-8");
            ageRanges.add("9-12");
            ageRanges.add("13-15");
            loadStories();

            setupRecyclerView();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewStories);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
    }

    private void loadStories() {
        if(!isChildAccount){
            db.collection("book")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Book> storyList = queryDocumentSnapshots.toObjects(Book.class);
                        Log.d("StoriesDebug", "Story list size: " + storyList.size());
                        updateRecyclerView(storyList);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error fetching stories", e);
                        Toast.makeText(this, "Không thể tải danh sách truyện", Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            getSelectedAgeGroupFromFirestore(ageGroup -> {
                db.collection("book")
                        .whereEqualTo("age_range", ageGroup)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            List<Book> storyList = queryDocumentSnapshots.toObjects(Book.class);
                            Log.d("StoriesDebug", "Story list size: " + storyList.size());
                            updateRecyclerView(storyList);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error fetching stories", e);
                            Toast.makeText(this, "Không thể tải danh sách truyện", Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }


    private void getSelectedAgeGroupFromFirestore(OnAgeGroupFetchedListener listener) {
        if (!isChildAccount) {
            Log.e(TAG, "Selected age group is only available for child accounts");
            listener.onFetched("5-8"); // Giá trị mặc định nếu không phải tài khoản trẻ
            return;
        }

        db.collection("children")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String selectedAgeGroup = documentSnapshot.getString("selected_age_group");
                        if (selectedAgeGroup != null) {
                            listener.onFetched(selectedAgeGroup);
                        } else {
                            Log.e(TAG, "selected_age_group field is missing");
                            listener.onFetched("5-8"); // Giá trị mặc định
                        }
                    } else {
                        Log.e(TAG, "Child document not found");
                        listener.onFetched("5-8"); // Giá trị mặc định
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching selected age group", e);
                    listener.onFetched("5-8"); // Giá trị mặc định khi lỗi
                });
    }

    // Interface callback để xử lý dữ liệu không đồng bộ
    interface OnAgeGroupFetchedListener {
        void onFetched(String ageGroup);
    }


    private void updateRecyclerView(List<Book> storyList) {
        StoriesAdapter adapter = new StoriesAdapter(storyList);
        recyclerView.setAdapter(adapter);
    }


    private void initializeClock() {
        clock = Clock.system(ZoneId.systemDefault());
        lastUpdateTimestamp = Instant.now(clock).toEpochMilli();
    }

    private void setupNavigation(ActivityMainBinding binding) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        isChildAccount = getIntent().hasExtra("childId");
        if (isChildAccount) {
            userId = getIntent().getStringExtra("childId");
        }
    }

    private void setupViews() {
        timerTextView = findViewById(R.id.timerTextView);
    }

    private void loadUserData() {
        DocumentReference userRef = isChildAccount ?
                db.collection("children").document(userId) :
                db.collection("parents").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && isChildAccount) {
                Long timeLimit = documentSnapshot.getLong("timeLimit");
                Long totalUsageToday = documentSnapshot.getLong("totalUsageToday");

                if (timeLimit != null && timeLimit > 0) {
                    long remainingTime = (timeLimit - (totalUsageToday != null ? totalUsageToday : 0)) * 60 * 1000;
                    if (remainingTime > 0) {
                        timeLeftInMillis.set(remainingTime);
                        startTimer();
                    } else {
                        handleTimeOut();
                    }
                } else {
                    handleTimeOut();
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading user data", e));
    }

    private synchronized void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis.get(), 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis.set(millisUntilFinished);
                updateTimerText();
                checkAndUpdateUsageTime();
            }

            public void onFinish() {
                timeLeftInMillis.set(0);
                updateTimerText();
                checkAndUpdateUsageTime();
                syncFinalUsageTime();
                handleTimeOut();
            }
        }.start();
    }

    private void checkAndUpdateUsageTime() {
        long currentTime = Instant.now(clock).toEpochMilli();
        long timeDifference = currentTime - lastUpdateTimestamp;

        if (timeDifference >= UPDATE_INTERVAL) {
            accumulatedUsageTime += UPDATE_INTERVAL / 1000 / 60; // Convert to minutes
            updateFirebaseUsageTime(accumulatedUsageTime);
            lastUpdateTimestamp = currentTime;
            accumulatedUsageTime = 0;
        }
    }

    private void updateFirebaseUsageTime(long minutes) {
        if (isChildAccount && minutes > 0) {
            String currentDate = java.time.LocalDate.now().toString();

            Map<String, Object> updates = new HashMap<>();
            updates.put("totalUsageToday", FieldValue.increment(minutes));
            updates.put("lastUpdateTime", Instant.now(clock).toEpochMilli());
            updates.put("dailyUsage." + currentDate, FieldValue.increment(minutes));

            db.collection("children").document(userId)
                    .update(updates)
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating usage time", e));
        }
    }

    private void syncFinalUsageTime() {
        if (accumulatedUsageTime > 0) {
            updateFirebaseUsageTime(accumulatedUsageTime);
            accumulatedUsageTime = 0;
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis.get() / 1000) / 60;
        int seconds = (int) (timeLeftInMillis.get() / 1000) % 60;
        @SuppressLint("DefaultLocale")
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText("Time left: " + timeLeftFormatted);
    }

    private void handleTimeOut() {
        Intent intent = new Intent(MainActivity.this, AccountSelectionActivity.class);
        intent.putExtra("dialog_title_time_out", "Thông báo");
        intent.putExtra("dialog_message_time_out", "Hôm nay đã hết thời gian sử dụng. Hãy vào lại vào ngày mai nhé");
        startActivity(intent);
        finish();
    }

    public void logoutUser() {
        syncFinalUsageTime();
        mAuth.signOut();

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isLoggedIn");
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("dialog_title", "Thông báo");
        intent.putExtra("dialog_message", "Bạn đã đăng xuất thành công");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isChildAccount) {
            loadUserData(); // Reload data to ensure accuracy
            loadStories();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        syncFinalUsageTime();
        loadStories();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        syncFinalUsageTime();
        loadStories();
    }
}