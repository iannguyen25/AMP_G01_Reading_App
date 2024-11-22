package com.example.amp_g01_reading_app.ui.settings.dashboard_management;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.amp_g01_reading_app.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParentDashboardFragment extends Fragment {

    private TextView tvUserName, tvUserAge, tvTimeAverage, tvTimeRemaining;
    private BarChart barChart;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ImageView iconBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_dashboard, container, false);

        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserAge = view.findViewById(R.id.tv_user_age);
        tvTimeAverage = view.findViewById(R.id.time_average);
        tvTimeRemaining = view.findViewById(R.id.tv_time_remaining);
        barChart = view.findViewById(R.id.barChart);
        iconBack = view.findViewById(R.id.icon_back);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadChildData();

        iconBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_settings);
        });

        return view;
    }

    private void loadChildData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("ParentDashboard", "Loading data for user: " + userId);

            db.collection("users")
                    .document(userId)
                    .collection("children")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot childSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String childName = childSnapshot.getString("name");
                            Long childAge = childSnapshot.getLong("age");
                            Long timeLimit = childSnapshot.getLong("timeLimit");
                            Map<String, Object> readingTimes = (Map<String, Object>) childSnapshot.get("readingTimes");

                            if (childName != null) tvUserName.setText(childName);
                            if (childAge != null) tvUserAge.setText(childAge + " tuổi");

                            if (timeLimit != null) {
                                updateRemainingTimeDisplay(timeLimit);
                            } else {
                                tvTimeRemaining.setText("Không khả dụng");
                            }

                            if (readingTimes != null && !readingTimes.isEmpty()) {
                                updateBarChart(readingTimes);
                                updateAverageReadingTime(readingTimes);
                            } else {
                                Log.w("ParentDashboard", "Reading times are empty or null");
                            }
                        } else {
                            Log.w("ParentDashboard", "No child data found");
                            tvUserName.setText("Chưa có dữ liệu");
                            tvUserAge.setText("");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ParentDashboard", "Error loading child data", e);
                        Toast.makeText(getContext(), "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateRemainingTimeDisplay(long timeRemaining) {
        int hours = (int) (timeRemaining / 60);
        int minutes = (int) (timeRemaining % 60);
        tvTimeRemaining.setText(String.format("%02d h : %02d m", hours, minutes));
    }

    private void updateBarChart(Map<String, Object> readingTimes) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Object> entry : readingTimes.entrySet()) {
            labels.add(entry.getKey());
            float time = ((Long) entry.getValue()).floatValue();
            entries.add(new BarEntry(index++, time));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Thời gian đọc");
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.primary_color));

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        barChart.invalidate();
    }

    private void updateAverageReadingTime(Map<String, Object> readingTimes) {
        long totalTime = 0;
        for (Object time : readingTimes.values()) {
            totalTime += (Long) time;
        }
        int averageTime = (int) (totalTime / readingTimes.size());
        tvTimeAverage.setText(averageTime + " phút");
    }
}