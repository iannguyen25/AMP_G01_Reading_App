package com.example.amp_g01_reading_app.ui.settings.dashboard_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amp_g01_reading_app.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ChildDashboardAdapter extends RecyclerView.Adapter<ChildDashboardAdapter.ChildViewHolder> {

    private List<ChildData> children;
    private Context context;

    public ChildDashboardAdapter(Context context, List<ChildData> children) {
        this.context = context;
        this.children = children;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_dashboard, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        ChildData child = children.get(position);
        holder.bind(child);
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserAge, tvTimeRemaining, timeAverage;
        BarChart barChart;

        ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserAge = itemView.findViewById(R.id.tv_user_age);
            tvTimeRemaining = itemView.findViewById(R.id.tv_time_remaining);
            timeAverage = itemView.findViewById(R.id.time_average);
            barChart = itemView.findViewById(R.id.barChart);
        }

        private BarData cachedBarData;

        void bind(ChildData child) {
            tvUserName.setText(child.getName());
            tvUserAge.setText(context.getString(R.string.age_format, child.getAge()));
            tvTimeRemaining.setText(context.getString(R.string.time_used_format,
                    child.getTotalUsageToday()));

            List<Integer> dailyUsage = child.dailyUsage();
            if (dailyUsage != null && !dailyUsage.isEmpty()) {
                int averageUsage = calculateAverageUsage(dailyUsage);
                timeAverage.setText(context.getString(R.string.average_time_format,
                        averageUsage));
                setupBarChart(dailyUsage);
            } else {
                timeAverage.setText(context.getString(R.string.no_data));
                barChart.clear();
            }
        }

        private int calculateAverageUsage(List<Integer>  dailyUsage) {
            if (dailyUsage == null || dailyUsage.isEmpty()) {
                return 0;
            }
            int sum = 0;
            for (int usage : dailyUsage) {
                sum += usage;
            }
            return sum / dailyUsage.size();
        }

        private void setupBarChart(List<Integer> dailyUsage) {
            if (cachedBarData == null) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                for (int i = 0; i < dailyUsage.size(); i++) {
                    entries.add(new BarEntry(i, dailyUsage.get(i)));
                }

                BarDataSet dataSet = new BarDataSet(entries,
                        "Thời gian đọc (phút)");
                dataSet.setColor(context.getResources().getColor(
                        R.color.primary_color));

                cachedBarData = new BarData(dataSet);
            }

            barChart.setData(cachedBarData);

            // Enable chart interactions
            barChart.setScaleEnabled(true);
            barChart.setPinchZoom(true);
            barChart.setDoubleTapToZoomEnabled(true);

            String[] days = new String[]{"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);

            barChart.getDescription().setEnabled(false);
            barChart.getLegend().setEnabled(false);
            barChart.invalidate();
        }
    }
}