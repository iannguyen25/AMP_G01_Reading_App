package com.example.amp_g01_reading_app.ui.settings.dashboard_management;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.amp_g01_reading_app.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParentDashboardActivity extends AppCompatActivity implements ShakeDetector.ShakeListener {
    private ViewPager2 viewPager;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<ChildData> childrenData;
    private ChildDashboardAdapter adapter;
    private TabLayout tabLayout;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        childrenData = new ArrayList<>();

        initViews();
        setupViewPagerIndicator();
        loadChildrenData();
        setupRealtimeUpdates();

        // Khởi tạo cảm biến
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            shakeDetector = new ShakeDetector(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
    }

    @Override
    public void onShakeLeft() {
        if (viewPager != null && viewPager.getCurrentItem() < childrenData.size() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    @Override
    public void onShakeRight() {
        if (viewPager != null && viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        TextView appBarLabel = findViewById(R.id.app_bar_label);
        ImageView iconBack = findViewById(R.id.icon_back);
        tabLayout = findViewById(R.id.tab_layout);

        iconBack.setOnClickListener(v -> finish());
        Button buttonGoToSensor = findViewById(R.id.buttonGoToSensor);
        buttonGoToSensor.setOnClickListener(v -> {
            Intent intent = new Intent(this, SensorDisplayActivity.class);
            startActivity(intent);
        });

        adapter = new ChildDashboardAdapter(this, childrenData);
        viewPager.setAdapter(adapter);

        viewPager.setPageTransformer(new MarginPageTransformer(50));
    }

    private void loadChildrenData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("children")
                .whereEqualTo("parentId", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        childrenData.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ChildData child = document.toObject(ChildData.class);
                            childrenData.add(child);
                        }
                        updateUI();
                    } else {
                        Log.e("ParentDashboardActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void setupRealtimeUpdates() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("children")
                .whereEqualTo("parentId", currentUser.getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("ParentDashboardActivity", "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        childrenData.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            ChildData child = doc.toObject(ChildData.class);
                            childrenData.add(child);
                        }
                        updateUI();
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        if (childrenData.isEmpty()) {
            showEmpty();
        } else {
            showContent();
            adapter.notifyDataSetChanged();
        }
    }

    private void showEmpty() {
        viewPager.setVisibility(View.GONE);
    }

    private void showContent() {
        viewPager.setVisibility(View.VISIBLE);
    }

    private void setupViewPagerIndicator() {
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {});
        tabLayoutMediator.attach();
    }
}
