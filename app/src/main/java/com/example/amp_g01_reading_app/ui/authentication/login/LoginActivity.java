package com.example.amp_g01_reading_app.ui.authentication.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amp_g01_reading_app.MainActivity;
import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.NotificationDialogFragment;
import com.example.amp_g01_reading_app.ui.authentication.AccountSelectionActivity;
import com.example.amp_g01_reading_app.ui.authentication.ChildAccount.CreateChildAccountActivity;
import com.example.amp_g01_reading_app.ui.authentication.signup.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String dialogTitle = intent.getStringExtra("dialog_title");
        String dialogMessage = intent.getStringExtra("dialog_message");

        if (dialogTitle != null && dialogMessage != null) {
            NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(dialogTitle, dialogMessage);
            dialog.show(getSupportFragmentManager(), "NotificationDialog");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(v -> loginUser());

        signUpButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(
                    "Thông báo",
                    "Hãy điền tất cả các trường!"
            );
            dialog.show(getSupportFragmentManager(), "NotificationDialog");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveLoginState();
                        // Đảm bảo người dùng đã đăng nhập trước khi truy cập Firestore
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkForChildAccounts(user.getUid());
                            startActivity(new Intent(LoginActivity.this, AccountSelectionActivity.class));
                        } else {
                            NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(
                                    "Thông báo",
                                    "Xác thực thất bại. Hãy đăng nhập lại!"
                            );
                            dialog.show(getSupportFragmentManager(), "NotificationDialog");
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveLoginState() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void checkForChildAccounts(String userId) {
        db.collection("children")
                .whereEqualTo("parentId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // No child accounts, go to CreateChildAccountActivity
                            startActivity(new Intent(LoginActivity.this, CreateChildAccountActivity.class));
                        } else {
                            // Child accounts exist, go to MainActivity (Home)
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        finish();
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseFirestoreException) {
                            FirebaseFirestoreException ffe = (FirebaseFirestoreException) e;
                            if (ffe.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                                Toast.makeText(LoginActivity.this, "Permission denied. Please check Firestore rules.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Firestore error: " + ffe.getCode(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            assert e != null;
                            Toast.makeText(LoginActivity.this, "Error checking child accounts: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        Log.e("LoginActivity", "Error checking child accounts", e);
                    }
                });
    }
}