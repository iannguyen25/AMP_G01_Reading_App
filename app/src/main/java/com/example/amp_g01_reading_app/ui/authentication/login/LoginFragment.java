package com.example.amp_g01_reading_app.ui.authentication.login;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.amp_g01_reading_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etEmail, etPassword;
    private TextView tvSignUp;
    private ProgressDialog progressDialog;
    private LoginViewModel mViewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCancelable(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initializeViews(view);
        setupClickListeners(view);
    }

    private void initializeViews(View view) {
        etEmail = view.findViewById(R.id.emailEditText);
        etPassword = view.findViewById(R.id.passwordEditText);
        tvSignUp = view.findViewById(R.id.signUpText);
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.loginButton).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(email, password)) {
                loginUser(email, password);
            }
        });

        tvSignUp.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.nav_signUpFragment);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Không thể chuyển trang. Lỗi: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loginUser(String email, String password) {
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkChildAccount(user.getUid());
                        }
                    } else {
                        progressDialog.dismiss();
                        handleLoginError(task.getException());
                    }
                });
    }

    private void checkChildAccount(String parentId) {
        db.collection("children")
                .whereEqualTo("parentId", parentId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Không có tài khoản con, chuyển đến trang tạo tài khoản con
                            navigateToCreateChildAccount();
                        } else {
                            // Có tài khoản con, chuyển đến trang chủ
                            navigateToHome();
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra tài khoản con", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToCreateChildAccount() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.nav_childAccountFragment);
    }

    private void navigateToHome() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.navigation_home);
    }

    private void handleLoginError(Exception exception) {
        String message = "Đăng nhập thất bại";

        if (exception instanceof FirebaseAuthInvalidUserException) {
            message = "Email không tồn tại";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            message = "Email hoặc mật khẩu không đúng";
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}