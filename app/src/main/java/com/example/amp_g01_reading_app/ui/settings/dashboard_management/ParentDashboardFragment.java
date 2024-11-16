package com.example.amp_g01_reading_app.ui.settings.dashboard_management;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.amp_g01_reading_app.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ParentDashboardFragment extends Fragment {

    private TextView tvUserName, tvUserAge;
    private ImageView closeButton;

    private BarChart barChart;

    public ParentDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_dashboard, container, false);

        // Initialize views
        tvUserName = view.findViewById(R.id.tv_user_name);
        closeButton = view.findViewById(R.id.icon_back);
        tvUserAge = view.findViewById(R.id.tv_user_age);

        barChart = view.findViewById(R.id.barChart);

        // Set sample data
        tvUserName.setText("Tuấn Anh");
        tvUserAge.setText("21 tuổi");

        List<BarEntry> readingTimesEntries = new ArrayList<>();
        readingTimesEntries.add(new BarEntry(0, 24.0f));
        readingTimesEntries.add(new BarEntry(1, 12.5f));
        readingTimesEntries.add(new BarEntry(2, 19.8f));
        readingTimesEntries.add(new BarEntry(3, 10.1f));
        readingTimesEntries.add(new BarEntry(4, 18.4f));
        readingTimesEntries.add(new BarEntry(5, 2.7f));
        readingTimesEntries.add(new BarEntry(6, 18.9f));

        BarDataSet barDataSet = new BarDataSet(readingTimesEntries, "Reading Time");
        barDataSet.setColor(Color.BLUE);

        BarData barData = new BarData(barDataSet);

        barChart.setDescription(null);
        barChart.setDrawGridBackground(false);
        barChart.setMaxVisibleValueCount(7);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        barChart.setData(barData);
        barChart.invalidate();

        closeButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_settings);
        });

        return view;
    }
}