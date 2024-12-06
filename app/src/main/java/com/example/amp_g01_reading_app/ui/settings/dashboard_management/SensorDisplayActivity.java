package com.example.amp_g01_reading_app.ui.settings.dashboard_management;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amp_g01_reading_app.R;

public class SensorDisplayActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private EditText editTextX, editTextY, editTextZ;
    private Button buttonToggle;
    private boolean isDisplayingRealData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_display);

        editTextX = findViewById(R.id.editTextX);
        editTextY = findViewById(R.id.editTextY);
        editTextZ = findViewById(R.id.editTextZ);
        buttonToggle = findViewById(R.id.buttonToggle);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        buttonToggle.setOnClickListener(v -> toggleDisplay());

        // Hiển thị giá trị mặc định ban đầu
        displayDefaultValues();
    }

    private void toggleDisplay() {
        isDisplayingRealData = !isDisplayingRealData;
        if (isDisplayingRealData) {
            buttonToggle.setText("Hiển thị giá trị mặc định");
            startSensorReading();
        } else {
            buttonToggle.setText("Hiển thị giá trị thực");
            stopSensorReading();
            displayDefaultValues();
        }
    }

    private void startSensorReading() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void stopSensorReading() {
        sensorManager.unregisterListener(this);
    }

    private void displayDefaultValues() {
        editTextX.setText("0.00");
        editTextY.setText("0.00");
        editTextZ.setText("0.00");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            editTextX.setText(String.format("%.2f", event.values[0]));
            editTextY.setText(String.format("%.2f", event.values[1]));
            editTextZ.setText(String.format("%.2f", event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý trong trường hợp này
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSensorReading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDisplayingRealData) {
            startSensorReading();
        }
    }
}