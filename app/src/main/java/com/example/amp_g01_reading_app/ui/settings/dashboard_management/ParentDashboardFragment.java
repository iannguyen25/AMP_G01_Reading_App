package com.example.amp_g01_reading_app.ui.settings.dashboard_management;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.settings.TimeLimitFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ParentDashboardFragment extends Fragment {

    private TextView tvUserName, tvUserAge, tvTimeAverage;
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
        barChart = view.findViewById(R.id.barChart);
        iconBack = view.findViewById(R.id.icon_back);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        addSampleData();

        loadChildData();

        iconBack.setOnClickListener(e -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_settings);
        });

        return view;
    }

    private void addSampleData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String childId = UUID.randomUUID().toString();

            Map<String, Object> childData = new HashMap<>();
            childData.put("name", "Tuấn Anh");
            childData.put("age", 8);
            childData.put("timeLimit", 60);

            Map<String, Object> readingTimes = new HashMap<>();
            readingTimes.put("2024-11-11", 30);
            readingTimes.put("2024-11-12", 45);
            readingTimes.put("2024-11-13", 20);
            readingTimes.put("2024-11-14", 35);
            readingTimes.put("2024-11-15", 50);
            readingTimes.put("2024-11-16", 15);
            readingTimes.put("2024-11-17", 40);

            childData.put("readingTimes", readingTimes);

            db.collection("users").document(userId)
                    .collection("children").document(childId)
                    .set(childData)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
        }
    }

    private void loadChildData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).collection("children")
                    .limit(1) // Assuming only one child for simplicity
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot childSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String childName = childSnapshot.getString("name");
                            Long childAge = childSnapshot.getLong("age");
                            Map<String, Object> readingTimes = (Map<String, Object>) childSnapshot.get("readingTimes");

                            tvUserName.setText(childName);
                            tvUserAge.setText(childAge + " tuổi");

                            if (readingTimes != null) {
                                updateBarChart(readingTimes);
                                updateAverageReadingTime(readingTimes);
                            }
                        } else {
                            Log.w("Firestore", "No child data found");
                        }
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Error loading child data", e));
        }
    }

    private void updateBarChart(Map<String, Object> readingTimes) {
        List<BarEntry> entries = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Object> entry : readingTimes.entrySet()) {
            float time = ((Long) entry.getValue()).floatValue();
            entries.add(new BarEntry(index++, time));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Thời gian đọc");
        dataSet.setColor(getResources().getColor(R.color.primary_color));

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void updateAverageReadingTime(Map<String, Object> readingTimes) {
        if (readingTimes.isEmpty()) {
            return;
        }

        long totalTime = 0;
        for (Object time : readingTimes.values()) {
            totalTime += (Long) time;
        }
        int averageTime = (int) (totalTime / readingTimes.size());
        tvTimeAverage.setText(averageTime + " phút");
    }
}