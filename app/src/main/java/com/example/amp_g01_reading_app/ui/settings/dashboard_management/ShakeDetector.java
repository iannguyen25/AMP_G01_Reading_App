package com.example.amp_g01_reading_app.ui.settings.dashboard_management;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 3.5f; // Ngưỡng gia tốc
    private static final int SHAKE_TIME_LAPSE = 1000;   // Thời gian giữa các lần lắc (ms)
    private long lastShakeTime = 0;

    private ShakeListener listener;

    public interface ShakeListener {
        void onShakeLeft();
        void onShakeRight();
    }

    public ShakeDetector(ShakeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastShakeTime) > SHAKE_TIME_LAPSE) {
                if (x < -SHAKE_THRESHOLD) { // Lắc sang trái
                    listener.onShakeLeft();
                } else if (x > SHAKE_THRESHOLD) { // Lắc sang phải
                    listener.onShakeRight();
                }
                lastShakeTime = currentTime;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
