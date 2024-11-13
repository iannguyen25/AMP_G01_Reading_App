package com.example.amp_g01_reading_app.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.amp_g01_reading_app.R;

import android.util.TypedValue;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeLimitFragment} factory method to
 * create an instance of this fragment.
 */
public class TimeLimitFragment extends DialogFragment {

    public static TimeLimitFragment newInstance() {
        return new TimeLimitFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getDialog() != null) {
//            getDialog().setCanceledOnTouchOutside(false);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_limit, container, false);

        Button btnComplete = view.findViewById(R.id.btnComplete);
        ((View) btnComplete).setOnClickListener(v -> dismiss()); // Đóng DialogFragment khi nhấn nút Hoàn tất

        ImageView btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss()); // Đóng DialogFragment khi nhấn nút Đóng

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {

            int marginHorizontal = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

            // Tính toán chiều rộng của hộp thoại với margin
            int width = getResources().getDisplayMetrics().widthPixels - (marginHorizontal * 2);

            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}