package com.example.amp_g01_reading_app.ui.authentication.signup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.amp_g01_reading_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword, etConfirmPassword;
    private ProgressDialog progressDialog;
    private SignUpViewModel mViewModel;
    private TextView backToLogin;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tạo tài khoản...");
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        View view = getView();
        if (view != null) {
            etEmail = view.findViewById(R.id.emailEditText);
            etPassword = view.findViewById(R.id.passwordEditText);
            etConfirmPassword = view.findViewById(R.id.confirm_password_edt);
        }
    }

    private void setupClickListeners() {
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.signUpButton).setOnClickListener(v -> {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (validateInput(email, password, confirmPassword)) {
                    signUpUser(email, password);
                }
            });

            // Thêm nút back to login nếu cần
            view.findViewById(R.id.loginText).setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigateUp();
            });
        }
    }

    private boolean validateInput(String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email không hợp lệ");
            return false;
        }

        if (password.length() < 6) {
            showToast("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Mật khẩu xác nhận không khớp");
            return false;
        }

        // Thêm các validation khác nếu cần
        // Ví dụ: kiểm tra độ mạnh của mật khẩu
        if (!isStrongPassword(password)) {
            showToast("Mật khẩu phải chứa chữ hoa, chữ thường và số");
            return false;
        }

        return true;
    }

    private boolean isStrongPassword(String password) {
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d.*");
        return hasUpperCase && hasLowerCase && hasNumber;
    }

    private void signUpUser(String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        showToast("Đăng ký tài khoản thành công");
                        // Có thể thêm logic tạo profile user trong Firestore ở đây nếu cần

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                        navController.navigate(R.id.nav_loginFragment);
                    } else {
                        handleSignUpError(task.getException());
                    }
                });
    }

    private void handleSignUpError(Exception exception) {
        String message = "Đăng ký thất bại";

        if (exception instanceof FirebaseAuthWeakPasswordException) {
            message = "Mật khẩu không đủ mạnh";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            message = "Email không hợp lệ";
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            message = "Email đã được sử dụng";
        }

        showToast(message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}