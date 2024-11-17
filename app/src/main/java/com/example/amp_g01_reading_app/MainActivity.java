//package com.example.amp_g01_reading_app;
//
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.View;
//
//import com.example.amp_g01_reading_app.databinding.ActivityMainBinding;
//import com.example.amp_g01_reading_app.services.TimeLimitService;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
//
//import java.util.Objects;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final String TAG = "MainActivity";
//    private ActivityMainBinding binding;
//    private TimeLimitService timeLimitService;
//    private boolean bound = false;
//    private BroadcastReceiver logoutReceiver;
//    private boolean receiverRegistered = false;
//    private BroadcastReceiver serviceReadyReceiver;
//
//    private ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            Log.d(TAG, "Service connected");
//            TimeLimitService.LocalBinder binder = (TimeLimitService.LocalBinder) service;
//            timeLimitService = binder.getService();
//            bound = true;
//            timeLimitService.loadTimeLimit();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            Log.d(TAG, "Service disconnected");
//            bound = false;
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate called");
//
//        try {
//            Objects.requireNonNull(getSupportActionBar()).hide();
//            binding = ActivityMainBinding.inflate(getLayoutInflater());
//            setContentView(binding.getRoot());
//
//            initializeLogoutReceiver();
//            initializeServiceReadyReceiver();
//
//            FirebaseAuth mAuth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//
//            setupNavigation(currentUser);
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error in onCreate", e);
//            finish();
//        }
//    }
//
//    private void initializeLogoutReceiver() {
//        logoutReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction() != null && intent.getAction().equals(TimeLimitService.ACTION_LOGOUT)) {
//                    Log.d(TAG, "Logout broadcast received");
//                    logout();
//                }
//            }
//        };
//    }
//
//    private void initializeServiceReadyReceiver() {
//        serviceReadyReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if ("TIME_LIMIT_SERVICE_READY".equals(intent.getAction())) {
//                    Log.d(TAG, "Service ready broadcast received");
//                    registerLogoutReceiver();
//                    try {
//                        unregisterReceiver(this);
//                    } catch (IllegalArgumentException e) {
//                        Log.e(TAG, "Error unregistering serviceReadyReceiver", e);
//                    }
//                }
//            }
//        };
//        registerReceiver(serviceReadyReceiver, new IntentFilter("TIME_LIMIT_SERVICE_READY"));
//    }
//
//    private void setupNavigation(FirebaseUser currentUser) {
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_category, R.id.navigation_notifications, R.id.navigation_settings)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
//
//        if (currentUser == null) {
//            Log.d(TAG, "User not logged in, navigating to login fragment");
//            navController.navigate(R.id.nav_loginFragment);
//        } else {
//            Log.d(TAG, "User logged in, starting TimeLimitService");
//            startAndBindTimeLimitService();
//        }
//
//        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            if (destination.getId() == R.id.navigation_dashboard) {
//                navView.setVisibility(View.GONE);
//            } else {
//                navView.setVisibility(View.VISIBLE);
//            }
//        });
//    }
//
//    private void startAndBindTimeLimitService() {
//        Log.d(TAG, "Starting and binding TimeLimitService");
//        Intent intent = new Intent(this, TimeLimitService.class);
//        startService(intent);
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
//    }
//
//    private void registerLogoutReceiver() {
//        if (!receiverRegistered && logoutReceiver != null) {
//            Log.d(TAG, "Registering logout receiver");
//            registerReceiver(logoutReceiver, new IntentFilter(TimeLimitService.ACTION_LOGOUT));
//            receiverRegistered = true;
//        } else {
//            Log.d(TAG, "Receiver already registered or logoutReceiver is null");
//        }
//    }
//
//
//    private void unregisterLogoutReceiver() {
//        if (receiverRegistered && logoutReceiver != null) {
//            try {
//                Log.d(TAG, "Unregistering logout receiver");
//                unregisterReceiver(logoutReceiver);
//                receiverRegistered = false;
//            } catch (IllegalArgumentException e) {
//                Log.e(TAG, "Error unregistering logout receiver", e);
//            }
//        } else {
//            Log.d(TAG, "Logout receiver not registered or already null");
//        }
//    }
//
//    private void logout() {
//        Log.d(TAG, "Logging out");
//        FirebaseAuth.getInstance().signOut();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        navController.navigate(R.id.nav_loginFragment);
//        cleanupServiceAndReceiver();
//    }
//
//    private void cleanupServiceAndReceiver() {
//        Log.d(TAG, "Cleaning up service and receiver");
//        unregisterLogoutReceiver();
//        if (bound) {
//            unbindService(connection);
//            bound = false;
//        }
//        Intent intent = new Intent(this, TimeLimitService.class);
//        stopService(intent);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume called");
//        if (bound && !receiverRegistered) {
//            registerLogoutReceiver();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause called");
//        unregisterLogoutReceiver();
//    }
//
//    @Override
//    protected void onDestroy() {
//        Log.d(TAG, "onDestroy called");
//        super.onDestroy();
//        try {
//            unregisterReceiver(serviceReadyReceiver);
//        } catch (IllegalArgumentException e) {
//            Log.e(TAG, "Error unregistering serviceReadyReceiver", e);
//        }
//        cleanupServiceAndReceiver();
//    }
//}

package com.example.amp_g01_reading_app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.amp_g01_reading_app.databinding.ActivityMainBinding;
import com.example.amp_g01_reading_app.services.TimeLimitService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private TimeLimitService timeLimitService;
    private boolean bound = false;
    private BroadcastReceiver logoutReceiver;
    private boolean receiverRegistered = false;
    private BroadcastReceiver serviceReadyReceiver;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "Service connected");
            TimeLimitService.LocalBinder binder = (TimeLimitService.LocalBinder) service;
            timeLimitService = binder.getService();
            bound = true;
            timeLimitService.loadTimeLimit(); // Load dữ liệu thời gian từ Firestore
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "Service disconnected");
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        try {
            Objects.requireNonNull(getSupportActionBar()).hide();
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            initializeLogoutReceiver();
            initializeServiceReadyReceiver();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            setupNavigation(currentUser);

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            finish();
        }
    }

    private void initializeLogoutReceiver() {
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive called");
                if (intent.getAction() != null && intent.getAction().equals(TimeLimitService.ACTION_LOGOUT)) {
                    Log.d(TAG, "Logout broadcast received");
                    logout();
                }
            }
        };
    }

    private void initializeServiceReadyReceiver() {
        try {
            serviceReadyReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (TimeLimitService.TIME_LIMIT_SERVICE_READY.equals(intent.getAction())) {
                        Log.d(TAG, "Service ready broadcast received");
                        registerLogoutReceiver();
                        try {
                            unregisterReceiver(this);
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Error unregistering serviceReadyReceiver", e);
                        }
                    } else {
                        Log.d(TAG, "Unknown broadcast received");
                    }
                }
            };

            IntentFilter filter = new IntentFilter("TIME_LIMIT_SERVICE_READY");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(serviceReadyReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(serviceReadyReceiver, filter);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing serviceReadyReceiver", e);
        }
    }



    private void setupNavigation(FirebaseUser currentUser) {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_category, R.id.navigation_notifications, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if (currentUser == null) {
            Log.d(TAG, "User not logged in, navigating to login fragment");
            navController.navigate(R.id.nav_loginFragment);
        } else {
            Log.d(TAG, "User logged in, starting TimeLimitService");
            startAndBindTimeLimitService();
        }

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_dashboard) {
                navView.setVisibility(View.GONE);
            } else {
                navView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startAndBindTimeLimitService() {
        Log.d(TAG, "Starting and binding TimeLimitService");
        Intent intent = new Intent(this, TimeLimitService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void registerLogoutReceiver() {
        if (!receiverRegistered && logoutReceiver != null) {
            Log.d(TAG, "Registering logout receiver");

            IntentFilter filter = new IntentFilter(TimeLimitService.ACTION_LOGOUT);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(logoutReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(logoutReceiver, filter);
            }

            receiverRegistered = true;
        } else {
            Log.d(TAG, "Receiver already registered or logoutReceiver is null");
        }
    }



    private void unregisterLogoutReceiver() {
        if (receiverRegistered && logoutReceiver != null) {
            try {
                Log.d(TAG, "Unregistering logout receiver");
                unregisterReceiver(logoutReceiver);
                receiverRegistered = false;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error unregistering logout receiver", e);
            }
        } else {
            Log.d(TAG, "Logout receiver not registered or already null");
        }
    }

    private void logout() {
        Log.d(TAG, "Logging out");
        FirebaseAuth.getInstance().signOut();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.nav_loginFragment);
        cleanupServiceAndReceiver();
    }

    private void cleanupServiceAndReceiver() {
        Log.d(TAG, "Cleaning up service and receiver");
        unregisterLogoutReceiver();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
        Intent intent = new Intent(this, TimeLimitService.class);
        stopService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        if (bound && !receiverRegistered) {
            registerLogoutReceiver();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        unregisterLogoutReceiver();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
        try {
            unregisterReceiver(serviceReadyReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error unregistering serviceReadyReceiver", e);
        }
        cleanupServiceAndReceiver();
    }
}
