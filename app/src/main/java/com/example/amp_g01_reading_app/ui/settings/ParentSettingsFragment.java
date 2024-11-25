package com.example.amp_g01_reading_app.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.amp_g01_reading_app.MainActivity;
import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.authentication.AccountSelectionActivity;
import com.example.amp_g01_reading_app.ui.authentication.ChildAccount.CreateChildAccountActivity;
import com.example.amp_g01_reading_app.ui.authentication.ChildAccount.SelectChildDialogFragment;
import com.example.amp_g01_reading_app.ui.settings.dashboard_management.ParentDashboardActivity;
import com.example.amp_g01_reading_app.ui.settings.management_settings.AdjustTimeLimitDialogFragment;
import com.example.amp_g01_reading_app.ui.settings.management_settings.AgeLimitFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParentSettingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isChildAccount;

    public ParentSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        // Kiểm tra xem có phải tài khoản trẻ em không
        isChildAccount = requireActivity().getIntent().hasExtra("childId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int layoutId = isChildAccount ? R.layout.fragment_child_settings : R.layout.fragment_settings;
        View view = inflater.inflate(layoutId, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (isChildAccount) {
            initializeChildViews(view);
        } else {
            initializeParentViews(view);
        }

        return view;
    }

    private void showChangeDialog() {
        Intent intent = new Intent(getActivity(), AccountSelectionActivity.class);
        startActivity(intent);

    }

    private void initializeChildViews(View view) {
        TextView changeAccountButton = view.findViewById(R.id.AccountChange);
        changeAccountButton.setOnClickListener(v -> showChangeDialog());
    }

    private void initializeParentViews(View view) {
        TextView logoutButton = view.findViewById(R.id.logoutButton);
        LinearLayout adjustTimeLimitButton = view.findViewById(R.id.adjustTimeLimitButton);
        LinearLayout parentDashboardButton = view.findViewById(R.id.parentDashboardButton);
        LinearLayout addNewChildAccount = view.findViewById(R.id.add_child_account_label);
        LinearLayout adjustAgeLimitButton = view.findViewById(R.id.age_limit_fragment);
        LinearLayout changePasswordButton = view.findViewById(R.id.changePassword);
        TextView changeAccountButton = view.findViewById(R.id.AccountChange);

        logoutButton.setOnClickListener(v -> logout());
        adjustTimeLimitButton.setOnClickListener(v -> showAdjustTimeLimitDialog());
        parentDashboardButton.setOnClickListener(v -> openParentDashboard());
        changeAccountButton.setOnClickListener(v -> showChangeDialog());
        adjustAgeLimitButton.setOnClickListener(v -> showAdjustAgeLimitDialog());
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        addNewChildAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateChildAccountActivity.class);
            startActivity(intent);
        });

    }

    // New
    private void showChangePasswordDialog() {
        List<Map<String, Object>> sampleBooks = new ArrayList<>();

        sampleBooks.add(new HashMap<String, Object>() {{
            put("title", "Chú Bé Tí Hon");
            put("author_id", "author1");
            put("description", "Câu chuyện về một cậu bé nhỏ xíu");
            put("minAge", 6);
            put("maxAge", 8);
            put("cover_image", "https://example.com/ti_hon.jpg");
        }});

        sampleBooks.add(new HashMap<String, Object>() {{
            put("title", "Bí Mật Vườn Sau Nhà");
            put("author_id", "author2");
            put("description", "Khám phá khu vườn kỳ diệu");
            put("minAge", 9);
            put("maxAge", 11);
            put("cover_image", "https://example.com/vuon_sau_nha.jpg");
        }});

        sampleBooks.add(new HashMap<String, Object>() {{
            put("title", "Cuộc Phiêu Lưu Của Robot R5");
            put("author_id", "author3");
            put("description", "Hành trình của một robot thông minh");
            put("minAge", 12);
            put("maxAge", 14);
            put("cover_image", "https://example.com/robot_r5.jpg");
        }});

        sampleBooks.add(new HashMap<String, Object>() {{
            put("title", "Bí Ẩn Đảo Hoang");
            put("author_id", "author4");
            put("description", "Khám phá bí mật trên một hòn đảo xa xôi");
            put("minAge", 15);
            put("maxAge", 17);
            put("cover_image", "https://example.com/dao_hoang.jpg");
        }});

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference booksRef = db.collection("book");

        for (Map<String, Object> book : sampleBooks) {
            booksRef.add(book)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Book added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error adding book", e);
                    });
        }


    }

    private void logout() {
        ((MainActivity) requireActivity()).logoutUser();
    }

    private void showAdjustAgeLimitDialog () {

//        String parentId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
//        db.collection("children")
//                .whereEqualTo("parentId", parentId)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    if (queryDocumentSnapshots.size() == 1) {
//                        // Only one child, show Adjust Time Limit Fragment directly
//                        String childId = queryDocumentSnapshots.getDocuments().get(0).getId();
//                        AdjustTimeLimitDialogFragment dialogFragment = AdjustTimeLimitDialogFragment.newInstance(childId);
//                        dialogFragment.show(getChildFragmentManager(), "AdjustTimeLimit");
//                    } else if (queryDocumentSnapshots.size() > 1) {
//                        // Multiple children, show dialog to select child
//                        SelectChildDialogFragment dialogFragment = new SelectChildDialogFragment();
//                        dialogFragment.show(getChildFragmentManager(), "SelectChild");
//                    }
//                });
        AgeLimitFragment dialogFragment = AgeLimitFragment.newInstance();
        dialogFragment.show(getChildFragmentManager(), "AgeLimitFragment");
    }

    private void showAdjustTimeLimitDialog() {
        String parentId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db.collection("children")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() == 1) {
                        // Only one child, show Adjust Time Limit Fragment directly
                        String childId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        AdjustTimeLimitDialogFragment dialogFragment = AdjustTimeLimitDialogFragment.newInstance(childId);
                        dialogFragment.show(getChildFragmentManager(), "AdjustTimeLimit");
                    } else if (queryDocumentSnapshots.size() > 1) {
                        // Multiple children, show dialog to select child
                        SelectChildDialogFragment dialogFragment = new SelectChildDialogFragment();
                        dialogFragment.show(getChildFragmentManager(), "SelectChild");
                    }
                });
    }

    private void openParentDashboard() {
        Intent intent = new Intent(getActivity(), ParentDashboardActivity.class);
        startActivity(intent);
    }
}