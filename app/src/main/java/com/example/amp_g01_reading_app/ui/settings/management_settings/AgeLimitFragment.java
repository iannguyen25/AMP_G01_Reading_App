package com.example.amp_g01_reading_app.ui.settings.management_settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.amp_g01_reading_app.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AgeLimitFragment extends DialogFragment {

    private Spinner spinnerAgeRange;
    private TextView tvCurrentAge;
    private FirebaseFirestore db;
    private String childId;

    private final String[] ageRanges = {"5-8", "9-12", "13-15"};


    public static AgeLimitFragment newInstance(String childId) {
        AgeLimitFragment fragment = new AgeLimitFragment();
        Bundle args = new Bundle();
        args.putString("childId", childId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_age_limit, null);

        spinnerAgeRange = view.findViewById(R.id.spinnerAgeRange);
        tvCurrentAge = view.findViewById(R.id.tvCurrentAge);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        ImageView btnClose = view.findViewById(R.id.btnClose);

        setupAgeRangeSpinner();
        loadCurrentAgeLimit();

        btnClose.setOnClickListener(v -> dismiss());
        btnConfirm.setOnClickListener(v -> saveAgeLimit());
        btnCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }

    @SuppressLint("SetTextI18n")
    private void loadCurrentAgeLimit() {
        db.collection("children").document(childId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy giá trị `selected_age_group` từ Firestore
                        String currentAgeGroup = documentSnapshot.getString("selected_age_group");
                        if (currentAgeGroup != null) {
                            tvCurrentAge.setText("Nhóm tuổi hiện tại: " + currentAgeGroup);
                            int position = getPositionInAgeRanges(currentAgeGroup);
                            if (position != -1) {
                                spinnerAgeRange.setSelection(position);
                            }
                        } else {
                            tvCurrentAge.setText("Chưa có nhóm tuổi được chọn.");
                        }
                    } else {
                        tvCurrentAge.setText("Không tìm thấy dữ liệu người dùng.");
                    }
                })
                .addOnFailureListener(e -> {
                    tvCurrentAge.setText("Lỗi khi tải dữ liệu.");
                    Toast.makeText(getContext(), "Không thể tải giới hạn độ tuổi hiện tại.", Toast.LENGTH_SHORT).show();
                });
    }

    private int getPositionInAgeRanges(String ageGroup) {
        for (int i = 0; i < ageRanges.length; i++) {
            if (ageRanges[i].equals(ageGroup)) {
                return i;
            }
        }
        return -1; // Không tìm thấy
    }

    @SuppressLint("SetTextI18n")
    private void saveAgeLimit() {
        String selectedAgeRange = spinnerAgeRange.getSelectedItem().toString();

        db.collection("children").document(childId)
                .update("selected_age_group", selectedAgeRange)
                .addOnSuccessListener(aVoid -> {
                    tvCurrentAge.setText("Nhóm tuổi hiện tại: " + selectedAgeRange);
                    Toast.makeText(getContext(), "Đã lưu giới hạn độ tuổi mới!", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi lưu giới hạn độ tuổi.", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupAgeRangeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                ageRanges
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgeRange.setAdapter(adapter);

        spinnerAgeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Không cần xử lý ngay khi chọn, lưu vào Firestore sẽ được xử lý khi nhấn nút "Xác nhận".
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần làm gì khi không có mục nào được chọn
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int marginHorizontal = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            int width = getResources().getDisplayMetrics().widthPixels - (marginHorizontal * 2);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
