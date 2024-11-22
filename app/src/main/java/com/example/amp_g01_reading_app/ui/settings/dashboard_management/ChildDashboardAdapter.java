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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        private BarData cachedBarData;

        ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserAge = itemView.findViewById(R.id.tv_user_age);
            tvTimeRemaining = itemView.findViewById(R.id.tv_time_remaining);
            timeAverage = itemView.findViewById(R.id.time_average);
            barChart = itemView.findViewById(R.id.barChart);
        }

        void bind(ChildData child) {
            tvUserName.setText(child.getName());
            tvUserAge.setText(context.getString(R.string.age_format, child.getAge()));
            tvTimeRemaining.setText(context.getString(R.string.time_used_format,
                    child.getTotalUsageToday()));

            Map<String, Long> dailyUsage = child.getDailyUsage();
            if (dailyUsage != null && !dailyUsage.isEmpty()) {
                long averageUsage = calculateAverageUsage(dailyUsage);
                timeAverage.setText(context.getString(R.string.average_time_format,
                        averageUsage));
                setupBarChart(dailyUsage);
            } else {
                timeAverage.setText(context.getString(R.string.no_data));
                barChart.clear();
            }
        }

        private long calculateAverageUsage(Map<String, Long> dailyUsage) {
            if (dailyUsage == null || dailyUsage.isEmpty()) {
                return 0;
            }
            long sum = 0;
            for (Long usage : dailyUsage.values()) {
                sum += usage;
            }
            return sum / dailyUsage.size();
        }

        private void setupBarChart(Map<String, Long> dailyUsage) {
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();

            // Get the last 7 days of data
            LocalDate today = LocalDate.now();
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String dateStr = date.toString();
                Long usage = dailyUsage.getOrDefault(dateStr, 0L);
                entries.add(new BarEntry(6-i, usage));

                // Get day of week in Vietnamese
                String dayOfWeek;
                switch (date.getDayOfWeek()) {
                    case MONDAY: dayOfWeek = "T2"; break;
                    case TUESDAY: dayOfWeek = "T3"; break;
                    case WEDNESDAY: dayOfWeek = "T4"; break;
                    case THURSDAY: dayOfWeek = "T5"; break;
                    case FRIDAY: dayOfWeek = "T6"; break;
                    case SATURDAY: dayOfWeek = "T7"; break;
                    case SUNDAY: dayOfWeek = "CN"; break;
                    default: dayOfWeek = "";
                }
                labels.add(dayOfWeek);
            }

            BarDataSet dataSet = new BarDataSet(entries, "Thời gian đọc (phút)");
            dataSet.setColor(context.getResources().getColor(R.color.primary_color));

            BarData barData = new BarData(dataSet);
            barChart.setData(barData);

            // Customize chart appearance
            barChart.setScaleEnabled(true);
            barChart.setPinchZoom(true);
            barChart.setDoubleTapToZoomEnabled(true);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);

            // Add Y-axis formatting if needed
            barChart.getAxisLeft().setGranularity(1f);
            barChart.getAxisRight().setEnabled(false);

            barChart.getDescription().setEnabled(false);
            barChart.getLegend().setEnabled(false);

            // Animate chart
            barChart.animateY(1000);
            barChart.invalidate();
        }
    }
}