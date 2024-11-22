package com.example.amp_g01_reading_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.authentication.AccountSelectionActivity;
import com.example.amp_g01_reading_app.ui.authentication.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000; // 3 seconds
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(this::checkLoginStatus, SPLASH_TIMEOUT);
    }

    private void checkLoginStatus() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().reload()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && auth.getCurrentUser() != null) {
                            // Vẫn đang đăng nhập, chuyển đến màn hình chọn tài khoản
                            startActivity(new Intent(SplashActivity.this, AccountSelectionActivity.class));
                        } else {
                            // Không còn phiên làm việc, quay về đăng nhập
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }
                        finish();
                    });
        } else {
            // Người dùng không đăng nhập
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

}