package com.example.amp_g01_reading_app.ui.settings.dashboard_management;
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

public class ParentDashboardFragment extends Fragment {

    private TextView tvUserName, tvUserAge;
    private ProgressBar pbBooksRead, pbVideosWatched, pbArticlesRead, pbTestsFinished;
    private LinearLayout barChartStats;
    private ImageView closeButton;
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
        pbBooksRead = view.findViewById(R.id.pb_books_read);
        pbVideosWatched = view.findViewById(R.id.pb_videos_watched);
        pbArticlesRead = view.findViewById(R.id.pb_articles_read);
        pbTestsFinished = view.findViewById(R.id.pb_tests_finished);
        barChartStats = view.findViewById(R.id.ll_stats);

        // Set sample data
        tvUserName.setText("Justin");
        tvUserAge.setText("8 y.o.");
        pbBooksRead.setProgress(47);
        pbVideosWatched.setProgress(20);
        pbArticlesRead.setProgress(15);
        pbTestsFinished.setProgress(25);

        closeButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_settings);
        });

        return view;
    }
}