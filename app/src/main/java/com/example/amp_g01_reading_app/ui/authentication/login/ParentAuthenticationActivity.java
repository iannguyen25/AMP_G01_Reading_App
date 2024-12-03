package com.example.amp_g01_reading_app.ui.authentication.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amp_g01_reading_app.MainActivity;
import com.example.amp_g01_reading_app.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ParentAuthenticationActivity extends AppCompatActivity {

    private EditText parentPasswordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_authentication);

        parentPasswordInput = findViewById(R.id.parentPasswordInput);
        Button authenticateButton = findViewById(R.id.authenticateButton);
        mAuth = FirebaseAuth.getInstance();

        authenticateButton.setOnClickListener(v -> {
            String enteredPassword = parentPasswordInput.getText().toString().trim();
            String parentEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

            if (enteredPassword.isEmpty()) {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                reAuthenticateParent(parentEmail, enteredPassword);
            }
        });
    }

    private void reAuthenticateParent(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xác thực thành công
                        proceedToParentAccount();
                    } else {
                        // Xác thực thất bại
                        Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void proceedToParentAccount() {
        Intent intent = new Intent(ParentAuthenticationActivity.this, MainActivity.class);
        intent.putExtra("parentId", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        startActivity(intent);
        finish();
    }
}
