package com.example.amp_g01_reading_app.ui.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amp_g01_reading_app.MainActivity;
import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.NotificationDialogFragment;
import com.example.amp_g01_reading_app.ui.authentication.login.ParentAuthenticationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountSelectionActivity extends AppCompatActivity {

    private ListView accountListView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<String> accountList;
    private List<String> accountIds;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_selection);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        accountListView = findViewById(R.id.accountListView);
        accountList = new ArrayList<>();
        accountIds = new ArrayList<>();

        loadAccounts();

        accountListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAccountId = accountIds.get(position);
            if (position == 0) { // Parent account
                // Navigate to authentication screen
                Intent intent = new Intent(AccountSelectionActivity.this, ParentAuthenticationActivity.class);
                startActivity(intent);
            } else {
                // Log in as child
                checkTimeLimit(selectedAccountId);
            }
        });

    }

    private void checkTimeLimit(String userId) {
        DocumentReference userRef = db.collection("children").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long timeLimit = documentSnapshot.getLong("timeLimit");
                Long totalUsageToday = documentSnapshot.getLong("totalUsageToday");
                String lastUsageDateStr = documentSnapshot.getString("lastUsageDate");

                LocalDate today = LocalDate.now();
                LocalDate lastUsageDate = (lastUsageDateStr != null)
                        ? LocalDate.parse(lastUsageDateStr)
                        : today.minusDays(1);

                if (timeLimit == null || totalUsageToday == null) {
                    proceedToMainActivity(userId);
                    return;
                }

                // Nếu ngày cuối cùng sử dụng không phải là hôm nay, reset totalUsageToday
                if (!lastUsageDate.equals(today)) {
                    totalUsageToday = 0L;
                    userRef.update("totalUsageToday", 0, "lastUsageDate", today.toString());
                }

                if (totalUsageToday < timeLimit) {
                    proceedToMainActivity(userId);
                } else {
                    Intent intent = getIntent();
                    String dialogTitle = intent.getStringExtra("dialog_title_time_out");
                    String dialogMessage = intent.getStringExtra("dialog_message_time_out");

                    if (dialogTitle != null && dialogMessage != null) {
                        NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(dialogTitle, dialogMessage);
                        dialog.show(getSupportFragmentManager(), "NotificationDialog");
                    }
                }
            } else {
                NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(
                        "Thông báo",
                        "Lỗi: Không tồn tại tài khoản trẻ này!"
                );
                dialog.show(getSupportFragmentManager(), "NotificationDialog");
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error checking time limit: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void proceedToMainActivity(String accountId) {
        updateLastLoginTime(accountId);
        Intent intent = new Intent(AccountSelectionActivity.this, MainActivity.class);
        intent.putExtra("childId", accountId);
        startActivity(intent);
        finish();
    }

    private void updateLastLoginTime(String childId) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        db.collection("children")
                .document(childId)
                .update("lastLoginTime", currentTime)
                .addOnFailureListener(e -> Toast.makeText(AccountSelectionActivity.this, "Error updating last login time: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadAccounts() {
        String parentId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        accountList.add("Parent Account"); // Thêm tài khoản Parent
        accountIds.add(parentId);

        db.collection("children")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String childName = document.getString("name");
                            String childId = document.getId();
                            if (childName != null) {
                                accountList.add(childName);
                                accountIds.add(childId);
                            }
                        }
                        if (!isFinishing() && !isDestroyed()) {
                            AccountAdapter adapter = new AccountAdapter(this, accountList, accountIds);
                            accountListView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(this, "Error loading accounts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}