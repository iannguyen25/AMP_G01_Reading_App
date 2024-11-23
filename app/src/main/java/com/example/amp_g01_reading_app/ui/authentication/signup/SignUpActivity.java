package com.example.amp_g01_reading_app.ui.authentication.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.NotificationDialogFragment;
import com.example.amp_g01_reading_app.ui.authentication.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, rePasswordEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rePasswordEditText = findViewById(R.id.rePasswordEditText);
        Button signUpButton = findViewById(R.id.signUpButton);
        Button loginButton = findViewById(R.id.loginButton);

        signUpButton.setOnClickListener(v -> signUpUser());

        loginButton.setOnClickListener(v -> {
            finish(); // Go back to LoginActivity
        });
    }

    private void signUpUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String rePassword = rePasswordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(
                    "Thông báo",
                    "Hãy điền tất cả các trường!"
            );
            dialog.show(getSupportFragmentManager(), "NotificationDialog");
            return;
        }

        if (!password.equals(rePassword)) {
            NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(
                    "Thông báo",
                    "Mật khẩu không trùng khớp!"
            );
            dialog.show(getSupportFragmentManager(), "NotificationDialog");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, save user data to Firestore
                        saveUserToFirestore(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), email);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("parentId", userId);
        user.put("email", email);
        user.put("children", new ArrayList<String>()); // Thay đổi từ Array sang ArrayList

        db.collection("parents").document(userId)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        new Handler().postDelayed(() -> {
                                    NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(
                                            "Thông báo",
                                            "Đăng kí thành công!"
                                    );
                                    dialog.show(getSupportFragmentManager(), "NotificationDialog");
//                                }, 3000);

                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}