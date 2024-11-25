package com.example.amp_g01_reading_app.ui.settings.management_settings;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.utils.AgeRangeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AgeLimitFragment extends DialogFragment {

    private Spinner spinnerAgeRange;
    private TextView tvCurrentAge;
    private FirebaseFirestore db;
    private String userId;

    private String selectedAgeRange;
    private int minAge;
    private int maxAge;

    public static AgeLimitFragment newInstance() {
        return new AgeLimitFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_age_limit, container, false);

        spinnerAgeRange = view.findViewById(R.id.spinnerAgeRange);
        tvCurrentAge = view.findViewById(R.id.tvCurrentAge);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        ImageView btnClose = view.findViewById(R.id.btnClose);

        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        setupAgeRangeSpinner();
        loadCurrentAgeLimit();

        btnClose.setOnClickListener(v -> dismiss());
        btnConfirm.setOnClickListener(v -> saveAgeLimit());
        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void loadCurrentAgeLimit() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy dữ liệu từ Firestore
                        Object ageLimitObj = documentSnapshot.get("selected_age_group");

                        // Kiểm tra kiểu dữ liệu
                        if (ageLimitObj instanceof List<?>) {
                            List<?> ageLimitList = (List<?>) ageLimitObj;

                            // Kiểm tra xem phần tử của danh sách có phải là Integer không
                            if (!ageLimitList.isEmpty() && ageLimitList.get(0) instanceof Integer) {
                                // Chuyển đổi an toàn từ List<?> sang List<Integer>
                                List<Integer> ageLimit = (List<Integer>) ageLimitList;
                                minAge = ageLimit.get(0);
                                maxAge = ageLimit.get(1);
                                tvCurrentAge.setText(minAge + " - " + maxAge + " tuổi");

                                // Thiết lập lại vị trí của spinner tương ứng
                                String currentAgeRange = minAge + " - " + maxAge + " tuổi";
                                int position = getAgeRangePosition(currentAgeRange);
                                spinnerAgeRange.setSelection(position);
                            } else {
                                // Xử lý trường hợp dữ liệu không đúng định dạng
                                Toast.makeText(getContext(), "Dữ liệu giới hạn độ tuổi không hợp lệ.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                });
    }


    @SuppressLint("SetTextI18n")
    private void saveAgeLimit() {
        // Lấy độ tuổi đã chọn từ Spinner
        String selectedAgeRange = spinnerAgeRange.getSelectedItem().toString();
        minAge = AgeRangeUtils.getMinAge(selectedAgeRange);
        maxAge = AgeRangeUtils.getMaxAge(selectedAgeRange);

        // Cập nhật lại vào Firestore
        db.collection("users").document(userId)
                .update("selected_age_group", Arrays.asList(minAge, maxAge))
                .addOnSuccessListener(aVoid -> {
                    tvCurrentAge.setText(minAge + " - " + maxAge + " tuổi");
                    Toast.makeText(getContext(), "Đã lưu giới hạn độ tuổi mới!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi lưu giới hạn độ tuổi.", Toast.LENGTH_SHORT).show();
                });
    }

    private int getAgeRangePosition(String ageRange) {
        String[] ageRanges = getResources().getStringArray(R.array.age_ranges);
        for (int i = 0; i < ageRanges.length; i++) {
            if (ageRanges[i].equals(ageRange)) {
                return i;
            }
        }
        return 0;
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

    private void setupAgeRangeSpinner() {
        String[] ageRanges = new String[]{
                "6 - 8 tuổi",
                "9 - 11 tuổi",
                "12 - 14 tuổi",
                "15 - 17 tuổi"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                ageRanges
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgeRange.setAdapter(adapter);

        spinnerAgeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAgeRange = parent.getItemAtPosition(position).toString();
                minAge = AgeRangeUtils.getMinAge(selectedAgeRange);
                maxAge = AgeRangeUtils.getMaxAge(selectedAgeRange);
                tvCurrentAge.setText("Tuổi được chọn: " + selectedAgeRange);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
