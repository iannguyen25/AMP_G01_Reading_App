package com.example.amp_g01_reading_app.ui.authentication.createChildAccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.authentication.createChildAccount.ChildAccountViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChildAccountFragment extends Fragment {

    private ChildAccountViewModel mViewModel;
    private EditText etChildName, etChildAge;
    private Button btnCreateChildAccount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public static ChildAccountFragment newInstance() {
        return new ChildAccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChildAccountViewModel.class);

        etChildName = view.findViewById(R.id.etChildName);
        etChildAge = view.findViewById(R.id.etChildAge);
        btnCreateChildAccount = view.findViewById(R.id.btnCreateChildAccount);

        btnCreateChildAccount.setOnClickListener(v -> createChildAccount());
    }

    private void createChildAccount() {
        String childName = etChildName.getText().toString().trim();
        String childAgeStr = etChildAge.getText().toString().trim();

        if (childName.isEmpty() || childAgeStr.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int childAge;
        try {
            childAge = Integer.parseInt(childAgeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Tuổi không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String parentId = mAuth.getCurrentUser().getUid();

        Map<String, Object> childData = new HashMap<>();
        childData.put("name", childName);
        childData.put("age", childAge);
        childData.put("parentId", parentId);

        db.collection("children")
                .add(childData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Tạo tài khoản trẻ thành công", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToHome() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.navigation_home);
    }
}