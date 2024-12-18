package com.example.amp_g01_reading_app.ui.authentication.ChildAccount;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.settings.management_settings.AdjustTimeLimitDialogFragment;
import com.example.amp_g01_reading_app.ui.settings.management_settings.AgeLimitFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectChildDialogFragment extends DialogFragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<String> childNames;
    private List<String> childIds;

    private static final String ARG_ACTION_TYPE = "action_type";
    public static final int ACTION_TIME_LIMIT = 1;
    public static final int ACTION_AGE_RANGE = 2;

    private int actionType;

    public static SelectChildDialogFragment newInstance(int actionType) {
        SelectChildDialogFragment fragment = new SelectChildDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION_TYPE, actionType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actionType = getArguments().getInt(ARG_ACTION_TYPE, ACTION_TIME_LIMIT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_select_child_dialog, null);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        childNames = new ArrayList<>();
        childIds = new ArrayList<>();

        ListView childListView = view.findViewById(R.id.childListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireActivity(),
                R.layout.list_item_account,
                R.id.accountName,
                childNames
        );
        childListView.setAdapter(adapter);

        childListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedChildId = childIds.get(position);

            if (actionType == ACTION_TIME_LIMIT) {
                AdjustTimeLimitDialogFragment dialogFragment =
                        AdjustTimeLimitDialogFragment.newInstance(selectedChildId);
                dialogFragment.show(getParentFragmentManager(), "AdjustTimeLimit");
            } else if (actionType == ACTION_AGE_RANGE) {
                AgeLimitFragment dialogFragment =
                        AgeLimitFragment.newInstance(selectedChildId);
                dialogFragment.show(getParentFragmentManager(), "AdjustAgeLimit");
            }

            dismiss();
        });

        loadChildren(adapter);

        builder.setView(view);

        return builder.create();
    }

    private void loadChildren(ArrayAdapter<String> adapter) {
        String parentId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db.collection("children")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        String childName = queryDocumentSnapshots.getDocuments().get(i).getString("name");
                        String childId = queryDocumentSnapshots.getDocuments().get(i).getId();
                        childNames.add(childName);
                        childIds.add(childId);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}