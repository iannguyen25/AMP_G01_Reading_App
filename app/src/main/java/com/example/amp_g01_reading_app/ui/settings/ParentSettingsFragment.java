package com.example.amp_g01_reading_app.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.amp_g01_reading_app.MainActivity;
import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.ConfirmDialogFragment;
import com.example.amp_g01_reading_app.ui.authentication.AccountSelectionActivity;
import com.example.amp_g01_reading_app.ui.authentication.ChildAccount.CreateChildAccountActivity;
import com.example.amp_g01_reading_app.ui.authentication.ChildAccount.SelectChildDialogFragment;
import com.example.amp_g01_reading_app.ui.settings.dashboard_management.ParentDashboardActivity;
import com.example.amp_g01_reading_app.ui.settings.management_settings.AdjustTimeLimitDialogFragment;
import com.example.amp_g01_reading_app.ui.settings.management_settings.AgeLimitFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ParentSettingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ParentSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView logoutButton = view.findViewById(R.id.logoutButton);
        LinearLayout adjustTimeLimitButton = view.findViewById(R.id.adjustTimeLimitButton);
        LinearLayout parentDashboardButton = view.findViewById(R.id.parentDashboardButton);
        LinearLayout addNewChildAccount = view.findViewById(R.id.add_child_account_label);
        LinearLayout adjustAgeLimitButton = view.findViewById(R.id.age_limit_fragment);
        TextView changeAccountButton = view.findViewById(R.id.AccountChange);

        logoutButton.setOnClickListener(v -> logout());
        adjustTimeLimitButton.setOnClickListener(v -> showAdjustTimeLimitDialog());
        parentDashboardButton.setOnClickListener(v -> openParentDashboard());
        changeAccountButton.setOnClickListener(v -> showChangeDialog());
        adjustAgeLimitButton.setOnClickListener(v -> showAdjustAgeLimitDialog());
        addNewChildAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateChildAccountActivity.class);
            startActivity(intent);
        });
        return view;
    }

    private void showChangeDialog() {
        Intent intent = new Intent(getActivity(), AccountSelectionActivity.class);
        startActivity(intent);

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