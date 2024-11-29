package com.example.amp_g01_reading_app.ui.bookscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import com.example.amp_g01_reading_app.R;

public class SettingsPanelFragment extends DialogFragment {

    private static final String PREFS_NAME = "UserSettings";
    private static final String BRIGHTNESS_KEY = "brightness";
    private static final String FONT_SIZE_KEY = "font_size";
    private static final String NIGHT_MODE_KEY = "night_mode";

    private SeekBar brightnessSlider;
    private Button fontIncrease, fontDecrease;
    private Switch nightModeSwitch;
    private TextView contentText;
    private SharedPreferences sharedPreferences;

    public SettingsPanelFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_panel, container, false);

        // Khởi tạo SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Ánh xạ các view từ layout
        brightnessSlider = view.findViewById(R.id.brightness_slider);
        fontIncrease = view.findViewById(R.id.font_increase);
        fontDecrease = view.findViewById(R.id.font_decrease);
        nightModeSwitch = view.findViewById(R.id.night_mode_switch);
        contentText = getActivity().findViewById(R.id.content_text);

        // Khôi phục trạng thái lưu trước đó
        float savedBrightness = sharedPreferences.getFloat(BRIGHTNESS_KEY, 0.5f);
        brightnessSlider.setProgress((int) (savedBrightness * 100));
        if (getActivity() != null) {
            WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
            layoutParams.screenBrightness = savedBrightness;
            getActivity().getWindow().setAttributes(layoutParams);
        }

        float savedFontSize = sharedPreferences.getFloat(FONT_SIZE_KEY, contentText.getTextSize());
        contentText.setTextSize(TypedValue.COMPLEX_UNIT_PX, savedFontSize);

        boolean isNightMode = sharedPreferences.getBoolean(NIGHT_MODE_KEY, false);
        nightModeSwitch.setChecked(isNightMode);
        AppCompatDelegate.setDefaultNightMode(isNightMode ?
                AppCompatDelegate.MODE_NIGHT_AUTO_TIME : AppCompatDelegate.MODE_NIGHT_NO);

        // Thiết lập điều khiển độ sáng
        brightnessSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float brightness = progress / 100.0f;
                if (getActivity() != null) {
                    WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
                    layoutParams.screenBrightness = brightness;
                    getActivity().getWindow().setAttributes(layoutParams);
                }
                sharedPreferences.edit().putFloat(BRIGHTNESS_KEY, brightness).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Thay đổi cỡ chữ
        fontIncrease.setOnClickListener(v -> {
            float currentSize = contentText.getTextSize();
            float newSize = currentSize + 2;
            contentText.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
            sharedPreferences.edit().putFloat(FONT_SIZE_KEY, newSize).apply();
        });

        fontDecrease.setOnClickListener(v -> {
            float currentSize = contentText.getTextSize();
            float newSize = currentSize - 2;
            contentText.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
            sharedPreferences.edit().putFloat(FONT_SIZE_KEY, newSize).apply();
        });

        // Chuyển chế độ night mode
        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(isChecked ?
                    AppCompatDelegate.MODE_NIGHT_AUTO_TIME : AppCompatDelegate.MODE_NIGHT_NO);
            sharedPreferences.edit().putBoolean(NIGHT_MODE_KEY, isChecked).apply();
        });

        return view;
    }
}
