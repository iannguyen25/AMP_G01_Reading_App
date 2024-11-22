package com.example.amp_g01_reading_app.ui.settings.dashboard_management;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.amp_g01_reading_app.R;
import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParentDashboardActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView appBarLabel;
    private ImageView iconBack;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<ChildData> childrenData;
    private ChildDashboardAdapter adapter;
    private TabLayout  tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View loadingView;
    private View emptyView;
    private View errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        childrenData = new ArrayList<>();

        initViews();
//        setupSwipeRefresh();
        setupViewPagerIndicator();
        loadChildrenData();
        setupRealtimeUpdates();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        appBarLabel = findViewById(R.id.app_bar_label);
        iconBack = findViewById(R.id.icon_back);
        tabLayout = findViewById(R.id.tab_layout);
//        loadingView = findViewById(R.id.loading_view);
//        emptyView = findViewById(R.id.empty_view);
//        errorView = findViewById(R.id.error_view);
//        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        iconBack.setOnClickListener(v -> finish());

        adapter = new ChildDashboardAdapter(this, childrenData);
        viewPager.setAdapter(adapter);

        // Add page transition animation
        viewPager.setPageTransformer(new MarginPageTransformer(50));
    }

//    private void setupSwipeRefresh() {
//        swipeRefreshLayout.setOnRefreshListener(this::loadChildrenData);
//    }

    private void loadChildrenData() {
        showLoading();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showError(getString(R.string.error_not_logged_in));
            return;
        }

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
                        showError(getString(R.string.error_loading_data));
                    }
//                    swipeRefreshLayout.setRefreshing(false);
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

    private void updateUI() {
        if (childrenData.isEmpty()) {
            showEmpty();
        } else {
            showContent();
            adapter.notifyDataSetChanged();
            setupViewPager();
            updateAppBarLabel(viewPager.getCurrentItem());
        }
    }

    private void setupViewPager() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateAppBarLabel(position);
            }
        });
    }

    private void updateAppBarLabel(int position) {
        if (childrenData.size() > position) {
            appBarLabel.setText(getString(R.string.dashboard_for, childrenData.get(position).getName()));
        }
    }

    private void showLoading() {
//        loadingView.setVisibility(View.VISIBLE);
//        emptyView.setVisibility(View.GONE);
//        errorView.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
    }

    private void showEmpty() {
//        loadingView.setVisibility(View.GONE);
//        emptyView.setVisibility(View.VISIBLE);
//        errorView.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
    }

    private void showError(String message) {
//        loadingView.setVisibility(View.GONE);
//        emptyView.setVisibility(View.GONE);
//        errorView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
//        ((TextView) errorView.findViewById(R.id.error_message)).setText(message);
    }

    private void setupViewPagerIndicator() {
        // Customize TabLayout appearance
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.setTabRippleColor(null);

        // Calculate number of dots based on children data
        int childCount = childrenData.size();

        // Create mediator
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager, (tab, position) -> {
            // Leave empty for dot indicators
        }
        );
        tabLayoutMediator.attach();

        // Optional: Adjust tab spacing
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            params.setMargins(8, 0, 8, 0);
            tab.requestLayout();
        }
    }

    private void showContent() {
//        loadingView.setVisibility(View.GONE);
//        emptyView.setVisibility(View.GONE);
//        errorView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
    }
}
