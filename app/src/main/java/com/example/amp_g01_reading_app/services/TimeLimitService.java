package com.example.amp_g01_reading_app.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TimeLimitService extends Service {

    private static final String TAG = "TimeLimitService";
    private final IBinder binder = new LocalBinder();
    private CountDownTimer timer;
    private long timeRemaining;
    public static final String ACTION_LOGOUT = "com.example.amp_g01_reading_app.ACTION_LOGOUT";
    public static final String TIME_LIMIT_SERVICE_READY = "com.example.amp_g01_reading_app.TIME_LIMIT_SERVICE_READY";

    public class LocalBinder extends Binder {
        public TimeLimitService getService() {
            return TimeLimitService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind called");
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");
        loadTimeLimit();
        return START_STICKY;
    }

    public void loadTimeLimit() {
        Log.d(TAG, "Loading time limit");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore.getInstance().collection("users").document(userId).collection("children")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Long timeLimit = queryDocumentSnapshots.getDocuments().get(0).getLong("timeLimit");
                            if (timeLimit != null) {
                                timeRemaining = timeLimit * 60 * 1000; // Convert to milliseconds
                                Log.d(TAG, "Time limit loaded: " + timeRemaining + " ms");
                                startTimer();
                            } else {
                                Log.w(TAG, "Time limit is null");
                            }
                        } else {
                            Log.w(TAG, "No documents found");
                        }
                        sendServiceReadyBroadcast();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading time limit", e);
                        sendServiceReadyBroadcast();
                    });
        } else {
            Log.w(TAG, "No user logged in");
            sendServiceReadyBroadcast();
        }
    }

    private void sendServiceReadyBroadcast() {
        Log.d(TAG, "Sending SERVICE_READY broadcast");
        sendBroadcast(new Intent("com.example.amp_g01_reading_app.SERVICE_READY"));
    }


    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        Log.d(TAG, "Starting timer with " + timeRemaining + " ms remaining");
        timer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                Log.d(TAG, "Time remaining: " + timeRemaining + " ms");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Timer finished, logging out");
                logout();
            }
        }.start();
    }

    private void limitTime() {
        Log.d(TAG, "Logging out user");
        Intent timeLimitIntent = new Intent(TIME_LIMIT_SERVICE_READY);
        sendBroadcast(timeLimitIntent);
    }

    private void logout() {
        Log.d(TAG, "Logging out user");
        FirebaseAuth.getInstance().signOut();
        Intent logoutIntent = new Intent(ACTION_LOGOUT);
        sendBroadcast(logoutIntent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }
}